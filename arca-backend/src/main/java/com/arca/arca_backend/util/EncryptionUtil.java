package com.arca.arca_backend.util;

import org.springframework.stereotype.Component;

/**
 * @deprecated Server-side encryption has been removed as part of the zero-knowledge
 * architecture migration. All encryption/decryption now happens client-side using
 * AES-256-GCM with a key derived from the user's master password via PBKDF2.
 * The server stores only ciphertext and never participates in decryption.
 *
 * This class is kept as a stub to avoid breaking any lingering references.
 * It will be removed in a future cleanup.
 */
@Deprecated
@Component
public class EncryptionUtil {

    @Deprecated
    public String generateEncryptionKey() {
        throw new UnsupportedOperationException(
            "Server-side encryption is no longer supported. " +
            "All encryption is now performed client-side."
        );
    }

    @Deprecated
    public String encrypt(String plaintext, String keyString) {
        throw new UnsupportedOperationException(
            "Server-side encryption is no longer supported. " +
            "All encryption is now performed client-side."
        );
    }

    @Deprecated
    public String decrypt(String ciphertext, String keyString) {
        throw new UnsupportedOperationException(
            "Server-side decryption is no longer supported. " +
            "All decryption is now performed client-side."
        );
    }

    @Deprecated
    public String deriveKeyFromPassword(String masterPassword, String salt) {
        throw new UnsupportedOperationException(
            "Server-side key derivation is no longer supported. " +
            "Key derivation (PBKDF2) is performed client-side only."
        );
    }
}
