package com.arca.arca_backend.controller;

import com.arca.arca_backend.dto.CredentialDTO;
import com.arca.arca_backend.entity.Credential;
import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.service.VaultService;
import com.arca.arca_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    private final VaultService vaultService;
    private final UserService userService;

    public VaultController(VaultService vaultService, UserService userService) {
        this.vaultService = vaultService;
        this.userService = userService;
    }

    // ── mapping helpers ────────────────────────────────────────────────────────

    private CredentialDTO toDTO(Credential c) {
        CredentialDTO dto = new CredentialDTO();
        dto.setId(c.getId());
        dto.setSiteName(c.getSiteName());
        dto.setUrl(c.getUrl());
        dto.setUsername(c.getUsername());
        dto.setEncryptedPassword(c.getEncryptedPassword()); // ciphertext — server never decrypts this
        dto.setIv(c.getIv());
        dto.setCryptoVersion(c.getCryptoVersion());
        dto.setCategory(c.getCategory());
        dto.setNotes(c.getNotes());
        dto.setSyncStatus(c.getSyncStatus());
        dto.setOfflineModified(c.getOfflineModified());
        dto.setLastModified(c.getLastModified() != null ? c.getLastModified().toString() : null);
        return dto;
    }

    private User resolveUser(Authentication authentication) {
        String supabaseUserId = authentication.getName();
        return userService.getUserBySupabaseId(supabaseUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── endpoints ──────────────────────────────────────────────────────────────

    /**
     * Unlock the vault.
     * Verifies the auth key from the X-Auth-Key header, then returns the user's
     * encrypted credential blobs. Decryption happens on the client — this server
     * is cipher-blind.
     */
    @PostMapping("/unlock")
    public ResponseEntity<?> unlockVault(
            @RequestHeader(value = "X-Auth-Key", required = false) String authKeyHex,
            Authentication authentication) {
        try {
            String supabaseUserId = authentication.getName();

            // Verify auth key
            if (authKeyHex != null && !authKeyHex.isBlank()) {
                boolean valid = userService.verifyAuthKey(supabaseUserId, authKeyHex);
                if (!valid) {
                    return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "error", "Invalid master password"
                    ));
                }
            }

            User user = resolveUser(authentication);
            List<CredentialDTO> credentials = vaultService.getCredentialsByUserId(user.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", user.getId(),
                "data", credentials
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/credentials")
    public ResponseEntity<?> getCredentials(Authentication authentication) {
        try {
            User user = resolveUser(authentication);
            List<CredentialDTO> credentials = vaultService.getCredentialsByUserId(user.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
            return ResponseEntity.ok(credentials);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Save a credential. Accepts encryptedPassword + iv from the client.
     * The server stores the ciphertext without ever seeing the plaintext.
     */
    @PostMapping("/credentials")
    public ResponseEntity<?> createCredential(
            @RequestBody CredentialDTO incoming,
            Authentication authentication) {
        try {
            User user = resolveUser(authentication);

            Credential credential = new Credential();
            credential.setUserId(user.getId());
            credential.setSiteName(incoming.getSiteName());
            credential.setUrl(incoming.getUrl());
            credential.setUsername(incoming.getUsername());
            credential.setEncryptedPassword(incoming.getEncryptedPassword());
            credential.setIv(incoming.getIv());
            credential.setCryptoVersion(incoming.getCryptoVersion() != null ? incoming.getCryptoVersion() : 1);
            credential.setCategory(incoming.getCategory());
            credential.setNotes(incoming.getNotes());
            credential.setSyncStatus("synced");
            credential.setOfflineModified(false);
            credential.setVersionNumber(1);

            Credential saved = vaultService.saveCredential(credential);
            return ResponseEntity.ok(toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/credentials/{id}")
    public ResponseEntity<?> updateCredential(
            @PathVariable UUID id,
            @RequestBody CredentialDTO incoming,
            Authentication authentication) {
        try {
            User user = resolveUser(authentication);

            Credential existing = vaultService.getCredentialById(id)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

            if (!existing.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            existing.setSiteName(incoming.getSiteName());
            existing.setUrl(incoming.getUrl());
            existing.setUsername(incoming.getUsername());
            existing.setEncryptedPassword(incoming.getEncryptedPassword());
            existing.setIv(incoming.getIv());
            existing.setCryptoVersion(incoming.getCryptoVersion() != null ? incoming.getCryptoVersion() : 1);
            existing.setCategory(incoming.getCategory());
            existing.setNotes(incoming.getNotes());

            Credential updated = vaultService.saveCredential(existing);
            return ResponseEntity.ok(toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/credentials/{id}")
    public ResponseEntity<?> deleteCredential(
            @PathVariable UUID id,
            Authentication authentication) {
        try {
            User user = resolveUser(authentication);

            Credential existing = vaultService.getCredentialById(id)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

            if (!existing.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
            }

            vaultService.deleteCredential(id);
            return ResponseEntity.ok(Map.of("message", "Deleted", "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
