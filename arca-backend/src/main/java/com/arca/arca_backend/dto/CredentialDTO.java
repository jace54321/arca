package com.arca.arca_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Credential data transferred to the frontend.
 * The password field is CLIENT-SIDE encrypted — the server never sees plaintext.
 * The frontend decrypts encryptedPassword using its in-memory vault key.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialDTO {
    private UUID id;
    private String siteName;
    private String url;
    private String username;
    private String encryptedPassword; // AES-256-GCM ciphertext (base64)
    private String iv;                // AES-GCM IV (base64)
    private Integer cryptoVersion;
    private String category;
    private String notes;
    private String syncStatus;
    private Boolean offlineModified;
    private String lastModified;
}
