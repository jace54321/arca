package com.arca.arca_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sync_logs")
public class SyncLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID userId;  // Foreign key to users
    
    @Column(nullable = false)
    private String device;  // Device name (e.g., "Pixel 7 Pro", "MacBook Pro")
    
    @Column(nullable = false)
    private String deviceType;  // 'mobile' | 'desktop'
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private String status;  // 'synced' | 'conflict' | 'error'
    
    @Column
    private Integer versionFrom;  // Vault version before sync
    
    @Column
    private Integer versionTo;  // Vault version after sync
    
    @Column(columnDefinition = "TEXT")
    private String message;  // User-readable sync event description
    
    @Column
    private Boolean isCurrentDevice = false;
}
