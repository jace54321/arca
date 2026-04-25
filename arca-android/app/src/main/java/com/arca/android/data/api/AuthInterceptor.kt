package com.arca.android.data.api

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that attaches:
 * 1. Authorization: Bearer <supabase-jwt> — on every authenticated request
 * 2. X-Auth-Key: <hex> — only when set (login, vault unlock)
 *
 * The auth key is stored transiently and cleared after use.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val supabaseClient: SupabaseClient,
) : Interceptor {

    @Volatile
    private var authKeyHex: String? = null

    /**
     * Set the auth key for the next request(s) that need it.
     * Call this before login/unlock API calls.
     */
    fun setAuthKey(key: String?) {
        authKeyHex = key
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")

        // Attach JWT if available
        try {
            val session = runBlocking {
                supabaseClient.auth.currentSessionOrNull()
            }
            session?.accessToken?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        } catch (_: Exception) {
            // No session available — request will proceed without auth header
        }

        // Attach X-Auth-Key if set
        authKeyHex?.let { key ->
            requestBuilder.addHeader("X-Auth-Key", key)
        }

        return chain.proceed(requestBuilder.build())
    }
}
