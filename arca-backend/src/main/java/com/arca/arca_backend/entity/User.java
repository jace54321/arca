package com.arca.arca_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String supabaseUserId;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * BCrypt hash of the client-derived auth key (PBKDF2 output).
     * The server NEVER sees the raw master password or the vault key.
     * Nullable for existing users — set on first login after this migration.
     */
    @Column
    private String authKeyHash;
    
    @Column
    private String username;
    
    @Column(columnDefinition = "TEXT")
    private String avatarUrl;
}
