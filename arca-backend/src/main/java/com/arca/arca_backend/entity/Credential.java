package com.arca.arca_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credentials")
public class Credential {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID userId;  // Foreign key to users
    
    @Column(nullable = false)
    private String siteName;  // e.g., "GitHub", "Google"
    
    @Column
    private String url;  // https://github.com
    
    @Column(nullable = false)
    private String username;  // Email or username
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedPassword;  // AES-256 encrypted
    
    @Column(nullable = false)
    private String category;  // 'Work' | 'Personal' | 'Social' | 'Other'
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(nullable = false)
    private String syncStatus = "synced";  // 'synced' | 'pending' | 'syncing' | 'offline' | 'error'
    
    @Column
    private Boolean offlineModified = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastModified;
    
    @Column(nullable = false)
    private Integer versionNumber = 1;  // For conflict resolution
}
