package com.arca.android.crypto

import android.content.Context
import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Biometric authentication manager — scaffolded for future use.
 *
 * NOT wired into the unlock flow yet. This provides the infrastructure
 * so biometric unlock can be added post-MVP without architectural changes.
 *
 * Future flow:
 * 1. On first successful master password unlock, encrypt the vault key
 *    with a biometric-bound AndroidKeyStore key.
 * 2. On subsequent app launches, prompt biometric → decrypt vault key
 *    → skip master password entirely.
 */
@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * Check if the device supports biometric authentication.
     */
    fun canAuthenticate(): BiometricStatus {
        val biometricManager = AndroidBiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG
        )) {
            AndroidBiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            AndroidBiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            AndroidBiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            AndroidBiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            else -> BiometricStatus.UNKNOWN
        }
    }

    /**
     * Show the biometric prompt. Call this from an Activity or Fragment.
     *
     * @param activity The activity context (must be a FragmentActivity for BiometricPrompt)
     * @param title Prompt title
     * @param subtitle Prompt subtitle
     * @param onSuccess Called when authentication succeeds
     * @param onError Called when authentication fails or is cancelled
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String = "Unlock Arca",
        subtitle: String = "Use your fingerprint to unlock your vault",
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Don't call onError — BiometricPrompt handles retries internally
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Use Master Password")
            .setAllowedAuthenticators(AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }

    enum class BiometricStatus {
        AVAILABLE,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NOT_ENROLLED,
        UNKNOWN,
    }
}
