package com.arca.android.di

import android.content.Context
import androidx.room.Room
import com.arca.android.BuildConfig
import com.arca.android.data.api.ArcaApiService
import com.arca.android.data.api.AuthInterceptor
import com.arca.android.data.local.ArcaDatabase
import com.arca.android.data.local.CredentialDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        ) {
            install(Auth)
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(supabaseClient: SupabaseClient): AuthInterceptor {
        return AuthInterceptor(supabaseClient)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideArcaApiService(retrofit: Retrofit): ArcaApiService {
        return retrofit.create(ArcaApiService::class.java)
    }

    // ── Room Database ──────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideArcaDatabase(@ApplicationContext context: Context): ArcaDatabase {
        return Room.databaseBuilder(
            context,
            ArcaDatabase::class.java,
            "arca_vault.db",
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCredentialDao(database: ArcaDatabase): CredentialDao {
        return database.credentialDao()
    }
}
