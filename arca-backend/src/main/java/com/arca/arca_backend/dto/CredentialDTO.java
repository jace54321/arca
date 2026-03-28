package com.arca.arca_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialDTO {
    private UUID id;
    private String siteName;
    private String url;
    private String username;
    private String password;  // Decrypted for frontend
    private String category;
    private String notes;
    private String syncStatus;
    private Boolean offlineModified;
    private String lastModified;
}
