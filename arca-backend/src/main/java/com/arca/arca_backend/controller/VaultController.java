package com.arca.arca_backend.controller;

import com.arca.arca_backend.dto.ApiResponse;
import com.arca.arca_backend.dto.CredentialDTO;
import com.arca.arca_backend.dto.UnlockVaultRequest;
import com.arca.arca_backend.service.UserService;
import com.arca.arca_backend.service.VaultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vault")
public class VaultController {
    
    private final VaultService vaultService;
    private final UserService userService;
    
    public VaultController(VaultService vaultService, UserService userService) {
        this.vaultService = vaultService;
        this.userService = userService;
    }
    
    /**
     * Unlock vault - validate master password and return encryption key
     * Protected by Spring Security
     */
    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse> unlockVault(@RequestBody UnlockVaultRequest request) {
        try {
            // Extract user ID from Security Context
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            // Verify master password
            if (!userService.verifyMasterPassword(UUID.fromString(userId), request.getMasterPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid master password", null));
            }
            
            return ResponseEntity.ok(new ApiResponse(true, "Vault unlocked", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error unlocking vault", null));
        }
    }
    
    /**
     * Get all credentials for the authenticated user
     * Protected by Spring Security + JWT
     */
    @GetMapping("/credentials")
    public ResponseEntity<ApiResponse> getCredentials(@RequestHeader(required = false) String masterPassword) {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            if (masterPassword == null || masterPassword.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Master password required", null));
            }
            
            // Get encryption key from master password
            String encryptionKey = userService.getEncryptionKey(userId, masterPassword);
            
            List<CredentialDTO> credentials = vaultService.getAllCredentials(userId, encryptionKey);
            return ResponseEntity.ok(new ApiResponse(true, "Credentials retrieved", credentials));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Create a new credential
     * Protected by Spring Security
     */
    @PostMapping("/credentials")
    public ResponseEntity<ApiResponse> createCredential(@RequestBody CredentialDTO credentialDTO,
                                                        @RequestHeader(required = false) String masterPassword) {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            if (masterPassword == null || masterPassword.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Master password required", null));
            }
            
            String encryptionKey = userService.getEncryptionKey(userId, masterPassword);
            CredentialDTO created = vaultService.createCredential(userId, credentialDTO, encryptionKey);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Credential created", created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Update an existing credential
     * Protected by Spring Security
     */
    @PutMapping("/credentials/{id}")
    public ResponseEntity<ApiResponse> updateCredential(@PathVariable String id,
                                                        @RequestBody CredentialDTO credentialDTO,
                                                        @RequestHeader(required = false) String masterPassword) {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            if (masterPassword == null || masterPassword.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Master password required", null));
            }
            
            String encryptionKey = userService.getEncryptionKey(userId, masterPassword);
            CredentialDTO updated = vaultService.updateCredential(id, userId, credentialDTO, encryptionKey);
            
            return ResponseEntity.ok(new ApiResponse(true, "Credential updated", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Delete a credential
     * Protected by Spring Security
     */
    @DeleteMapping("/credentials/{id}")
    public ResponseEntity<ApiResponse> deleteCredential(@PathVariable String id) {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            vaultService.deleteCredential(id, userId);
            return ResponseEntity.ok(new ApiResponse(true, "Credential deleted", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Helper method to extract user ID from Security Context
     */
    private String extractUserIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
