package com.arca.arca_backend.service;

import com.arca.arca_backend.entity.Credential;
import com.arca.arca_backend.repository.CredentialRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VaultService {
    
    private final CredentialRepository credentialRepository;
    
    public VaultService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }
    
    public List<Credential> getCredentialsByUserId(UUID userId) {
        return credentialRepository.findByUserIdOrderByLastModifiedDesc(userId);
    }
    
    public Optional<Credential> getCredentialById(UUID id) {
        return credentialRepository.findById(id);
    }
    
    public Credential saveCredential(Credential credential) {
        if (credential.getCreatedAt() == null) {
            credential.setCreatedAt(LocalDateTime.now());
        }
        credential.setLastModified(LocalDateTime.now());
        if (credential.getSyncStatus() == null) {
            credential.setSyncStatus("synced");
        }
        return credentialRepository.save(credential);
    }
    
    public void deleteCredential(UUID id) {
        credentialRepository.deleteById(id);
    }
}
