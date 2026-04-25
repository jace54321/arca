package com.arca.arca_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "devices")
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID userId;
    
    @Column(nullable = false)
    private String deviceName;
    
    @Column(nullable = false)
    private String deviceType;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastActive;
}
