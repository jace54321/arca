# ProGuard rules for Arca Android

# Keep Retrofit interfaces
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.arca.android.data.api.dto.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# OkHttp
-dontwarn okio.**
-dontwarn okhttp3.**

# Supabase
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }

# Room entities
-keep class com.arca.android.data.local.** { *; }

# Biometric
-keep class androidx.biometric.** { *; }
