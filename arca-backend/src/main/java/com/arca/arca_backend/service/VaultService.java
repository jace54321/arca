package com.arca.arca_backend.service;

import com.arca.arca_backend.dto.CredentialDTO;
import com.arca.arca_backend.entity.Credential;
import com.arca.arca_backend.repository.CredentialRepository;
import com.arca.arca_backend.util.EncryptionUtil;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VaultService {
    
    private final CredentialRepository credentialRepository;
    private final EncryptionUtil encryptionUtil;
    private final UserService userService;
    
    public VaultService(CredentialRepository credentialRepository, EncryptionUtil encryptionUtil, UserService userService) {
        this.credentialRepository = credentialRepository;
        this.encryptionUtil = encryptionUtil;
        this.userService = userService;
    }
    
    /**
     * Get all credentials for a user (decrypted)
     */
    public List<CredentialDTO> getAllCredentials(String userIdStr, String encryptionKey) throws Exception {
        UUID userId = UUID.fromString(userIdStr);
        List<Credential> credentials = credentialRepository.findByUserIdOrderByLastModifiedDesc(userId);
        return credentials.stream()
                .map(cred -> toDTO(cred, encryptionKey))
                .collect(Collectors.toList());
    }
    
    /**
     * Get a single credential by ID (decrypted)
     */
    public Optional<CredentialDTO> getCredential(String credentialIdStr, String userIdStr, String encryptionKey) throws Exception {
        UUID credentialId = UUID.fromString(credentialIdStr);
        UUID userId = UUID.fromString(userIdStr);
        Optional<Credential> credOpt = credentialRepository.findByIdAndUserId(credentialId, userId);
        return credOpt.map(cred -> toDTO(cred, encryptionKey));
    }
    
    /**
     * Create a new credential (encrypt password)
     */
    public CredentialDTO createCredential(String userIdStr, CredentialDTO credDTO, String encryptionKey) throws Exception {
        UUID userId = UUID.fromString(userIdStr);
        Credential credential = new Credential();
        // Do not set ID manually - Hibernate will generate it
        credential.setUserId(userId);
        credential.setSiteName(credDTO.getSiteName());
        credential.setUrl(credDTO.getUrl());
        credential.setUsername(credDTO.getUsername());
        
        // Encrypt the password
        String encryptedPassword = encryptionUtil.encrypt(credDTO.getPassword(), encryptionKey);
        credential.setEncryptedPassword(encryptedPassword);
        
        credential.setCategory(credDTO.getCategory());
        credential.setNotes(credDTO.getNotes());
        credential.setSyncStatus("synced");
        credential.setOfflineModified(false);
        credential.setVersionNumber(1);
        
        Credential saved = credentialRepository.save(credential);
        return toDTO(saved, encryptionKey);
    }
    
    /**
     * Update an existing credential (encrypt password if provided)
     */
    public CredentialDTO updateCredential(String credentialIdStr, String userIdStr, CredentialDTO credDTO, String encryptionKey) throws Exception {
        UUID credentialId = UUID.fromString(credentialIdStr);
        UUID userId = UUID.fromString(userIdStr);
        Credential credential = credentialRepository.findByIdAndUserId(credentialId, userId)
                .orElseThrow(() -> new RuntimeException("Credential not found"));
        
        if (credDTO.getSiteName() != null) credential.setSiteName(credDTO.getSiteName());
        if (credDTO.getUrl() != null) credential.setUrl(credDTO.getUrl());
        if (credDTO.getUsername() != null) credential.setUsername(credDTO.getUsername());
        
        // Only encrypt if password is provided
        if (credDTO.getPassword() != null && !credDTO.getPassword().isEmpty()) {
            String encryptedPassword = encryptionUtil.encrypt(credDTO.getPassword(), encryptionKey);
            credential.setEncryptedPassword(encryptedPassword);
        }
        
        if (credDTO.getCategory() != null) credential.setCategory(credDTO.getCategory());
        if (credDTO.getNotes() != null) credential.setNotes(credDTO.getNotes());
        
        // Increment version for conflict resolution
        credential.setVersionNumber(credential.getVersionNumber() + 1);
        credential.setSyncStatus("pending");
        
        Credential updated = credentialRepository.save(credential);
        return toDTO(updated, encryptionKey);
    }
    
    /**
     * Delete a credential
     */
    public void deleteCredential(String credentialIdStr, String userIdStr) {
        UUID credentialId = UUID.fromString(credentialIdStr);
        UUID userId = UUID.fromString(userIdStr);
        credentialRepository.deleteByIdAndUserId(credentialId, userId);
    }
    
    /**
     * Convert Credential entity to CredentialDTO (decrypts password)
     */
    private CredentialDTO toDTO(Credential credential, String encryptionKey) {
        CredentialDTO dto = new CredentialDTO();
        dto.setId(credential.getId());
        dto.setSiteName(credential.getSiteName());
        dto.setUrl(credential.getUrl());
        dto.setUsername(credential.getUsername());
        
        // Decrypt password
        try {
            String decryptedPassword = encryptionUtil.decrypt(credential.getEncryptedPassword(), encryptionKey);
            dto.setPassword(decryptedPassword);
        } catch (Exception e) {
            // If decryption fails, leave password empty
            dto.setPassword("");
        }
        
        dto.setCategory(credential.getCategory());
        dto.setNotes(credential.getNotes());
        dto.setSyncStatus(credential.getSyncStatus());
        dto.setOfflineModified(credential.getOfflineModified());
        dto.setLastModified(credential.getLastModified().format(DateTimeFormatter.ISO_DATE_TIME));
        
        return dto;
    }
}
