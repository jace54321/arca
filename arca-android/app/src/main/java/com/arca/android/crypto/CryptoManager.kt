package com.arca.android.crypto

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Zero-knowledge cryptography for Arca Android.
 *
 * This implementation is byte-for-byte compatible with the web frontend's crypto.ts:
 *
 * Key derivation:
 *   masterPassword + email
 *     → PBKDF2(600,000 iterations, SHA-256, salt = UTF-8(email))
 *     → 512 raw bits (64 bytes)
 *   bytes[0..31]  → AES-256-GCM vault key  (stays in memory only)
 *   bytes[32..63] → auth key hex           (bcrypt-hashed copy stored on server)
 *
 * Encryption:
 *   AES-256-GCM with a fresh cryptographically-random 12-byte IV per field.
 *   Ciphertext and IV are encoded as Base64 (default/standard alphabet, matching btoa/atob).
 */
@Singleton
class CryptoManager @Inject constructor() {

    companion object {
        const val CRYPTO_VERSION = 1
        private const val PBKDF2_ITERATIONS = 600_000
        private const val KEY_LENGTH_BITS = 512  // 64 bytes total: 32 vault + 32 auth
        private const val AES_KEY_SIZE = 256
        private const val GCM_IV_LENGTH = 12     // 96 bits
        private const val GCM_TAG_LENGTH = 128   // bits
        private const val ALGORITHM = "AES/GCM/NoPadding"
    }

    /**
     * Result of key derivation from the master password.
     *
     * @property vaultKey AES-256-GCM secret key — use for encrypt/decrypt. Keep in memory only.
     * @property authKeyHex 32-byte hex string — send to server for BCrypt verification.
     */
    data class DerivedKeys(
        val vaultKey: SecretKey,
        val authKeyHex: String,
    )

    /**
     * Result of encrypting a field.
     *
     * @property ciphertext Base64-encoded ciphertext (includes GCM auth tag).
     * @property iv Base64-encoded 12-byte IV.
     */
    data class EncryptedField(
        val ciphertext: String,
        val iv: String,
    )

    // ── Key derivation ─────────────────────────────────────────────────────────

    /**
     * Derive both the vault key and the auth key from the user's master password.
     * This matches the web frontend's `deriveKeys(masterPassword, email)` exactly.
     *
     * @param masterPassword The password the user types in.
     * @param email Used as PBKDF2 salt — must match the value used at registration.
     */
    fun deriveKeys(masterPassword: String, email: String): DerivedKeys {
        val salt = email.lowercase().trim().toByteArray(Charsets.UTF_8)
        val spec = PBEKeySpec(
            masterPassword.toCharArray(),
            salt,
            PBKDF2_ITERATIONS,
            KEY_LENGTH_BITS,
        )

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val allBytes = factory.generateSecret(spec).encoded
        spec.clearPassword()

        // Split: first 32 bytes → vault key, last 32 bytes → auth key
        val vaultKeyBytes = allBytes.copyOfRange(0, 32)
        val authKeyBytes = allBytes.copyOfRange(32, 64)

        val vaultKey = SecretKeySpec(vaultKeyBytes, "AES")
        val authKeyHex = authKeyBytes.joinToString("") { "%02x".format(it) }

        // Clear sensitive arrays
        allBytes.fill(0)
        vaultKeyBytes.fill(0)
        authKeyBytes.fill(0)

        return DerivedKeys(vaultKey, authKeyHex)
    }

    // ── Field encryption / decryption ──────────────────────────────────────────

    /**
     * Encrypt a plaintext string with AES-256-GCM.
     * A new random IV is generated for every call.
     *
     * Returns ciphertext and IV as Base64 strings (matching the web frontend's btoa encoding).
     */
    fun encryptField(plaintext: String, vaultKey: SecretKey): EncryptedField {
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, vaultKey, gcmSpec)

        val ciphertextBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        return EncryptedField(
            ciphertext = Base64.encodeToString(ciphertextBytes, Base64.NO_WRAP),
            iv = Base64.encodeToString(iv, Base64.NO_WRAP),
        )
    }

    /**
     * Decrypt AES-256-GCM ciphertext (Base64) using the stored IV (Base64).
     * Throws an exception if the key is wrong or the data is corrupted.
     * This throw is the "wrong password" signal — catch it in the caller.
     */
    fun decryptField(ciphertext: String, iv: String, vaultKey: SecretKey): String {
        val ciphertextBytes = Base64.decode(ciphertext, Base64.NO_WRAP)
        val ivBytes = Base64.decode(iv, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(ALGORITHM)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, vaultKey, gcmSpec)

        val plaintextBytes = cipher.doFinal(ciphertextBytes)
        return String(plaintextBytes, Charsets.UTF_8)
    }
}
