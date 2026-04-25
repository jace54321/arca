package com.arca.android.data.repository

import com.arca.android.crypto.CryptoManager
import com.arca.android.data.api.ArcaApiService
import com.arca.android.data.api.AuthInterceptor
import com.arca.android.data.api.dto.AuthResponse
import com.arca.android.data.api.dto.RegisterRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val apiService: ArcaApiService,
    private val authInterceptor: AuthInterceptor,
    private val cryptoManager: CryptoManager,
) {

    /**
     * Register a new user:
     * 1. Derive keys from master password
     * 2. Register with Supabase (email/password)
     * 3. Register with our backend (email + authKeyHex + supabaseUserId)
     *
     * @return DerivedKeys if successful
     */
    suspend fun register(
        email: String,
        masterPassword: String,
    ): Result<CryptoManager.DerivedKeys> {
        return try {
            // 1. Derive vault key + auth key
            val keys = cryptoManager.deriveKeys(masterPassword, email)

            // 2. Register with Supabase
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = masterPassword
            }

            // 3. Get the Supabase user ID
            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: throw Exception("Failed to get Supabase user ID after registration")

            // 4. Register with our backend
            val response = apiService.register(
                RegisterRequest(
                    email = email,
                    authKeyHex = keys.authKeyHex,
                    supabaseUserId = userId,
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(keys)
            } else {
                val error = response.body()?.error ?: "Registration failed"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Login an existing user:
     * 1. Derive keys from master password
     * 2. Authenticate with Supabase (email/password)
     * 3. Verify auth key with our backend
     *
     * @return DerivedKeys if successful
     */
    suspend fun login(
        email: String,
        masterPassword: String,
    ): Result<CryptoManager.DerivedKeys> {
        return try {
            // 1. Derive vault key + auth key
            val keys = cryptoManager.deriveKeys(masterPassword, email)

            // 2. Authenticate with Supabase
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = masterPassword
            }

            // 3. Verify auth key with backend
            authInterceptor.setAuthKey(keys.authKeyHex)
            val response = apiService.login()
            authInterceptor.setAuthKey(null)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(keys)
            } else {
                val error = response.body()?.error ?: "Invalid master password"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            authInterceptor.setAuthKey(null)
            Result.failure(e)
        }
    }

    /**
     * Logout — sign out of Supabase.
     */
    suspend fun logout() {
        try {
            supabaseClient.auth.signOut()
        } catch (_: Exception) {
            // Best-effort logout
        }
    }

    /**
     * Check if we have an active Supabase session.
     */
    suspend fun hasActiveSession(): Boolean {
        return try {
            supabaseClient.auth.currentSessionOrNull() != null
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Get the current user's email from the active session.
     */
    suspend fun getCurrentEmail(): String? {
        return try {
            supabaseClient.auth.currentUserOrNull()?.email
        } catch (_: Exception) {
            null
        }
    }
}
