package com.arca.android.data.api

import com.arca.android.data.api.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit service matching the existing Spring Boot backend endpoints.
 */
interface ArcaApiService {

    // ── Auth ────────────────────────────────────────────────────────────────────

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<AuthResponse>

    // ── Vault ───────────────────────────────────────────────────────────────────

    @POST("vault/unlock")
    suspend fun unlockVault(): Response<VaultUnlockResponse>

    @GET("vault/credentials")
    suspend fun getCredentials(): Response<List<CredentialDTO>>

    @POST("vault/credentials")
    suspend fun createCredential(@Body credential: CredentialDTO): Response<CredentialDTO>

    @PUT("vault/credentials/{id}")
    suspend fun updateCredential(
        @Path("id") id: String,
        @Body credential: CredentialDTO,
    ): Response<CredentialDTO>

    @DELETE("vault/credentials/{id}")
    suspend fun deleteCredential(@Path("id") id: String): Response<GenericResponse>

    // ── Sync ────────────────────────────────────────────────────────────────────

    @GET("sync/logs")
    suspend fun getSyncLogs(): Response<SyncLogsResponse>

    @POST("sync/trigger")
    suspend fun triggerSync(
        @Query("deviceName") deviceName: String = "Android App",
        @Query("deviceType") deviceType: String = "mobile",
    ): Response<SyncTriggerResponse>

    // ── User ────────────────────────────────────────────────────────────────────

    @GET("user/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<GenericResponse>

    // ── Devices ─────────────────────────────────────────────────────────────────

    @GET("user/devices")
    suspend fun getDevices(): Response<DevicesResponse>
}
