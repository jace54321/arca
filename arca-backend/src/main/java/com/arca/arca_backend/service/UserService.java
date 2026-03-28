package com.arca.arca_backend.service;

import com.arca.arca_backend.dto.LoginRequest;
import com.arca.arca_backend.dto.UserDTO;
import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.repository.UserRepository;
import com.arca.arca_backend.util.EncryptionUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtil encryptionUtil;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EncryptionUtil encryptionUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionUtil = encryptionUtil;
    }
    
    /**
     * Register a new user with master password
     */
    public User registerUser(String email, String masterPassword) throws Exception {
        return registerUser(email, masterPassword, null);
    }
    
    /**
     * Register a new user with master password and Supabase user ID
     */
    public User registerUser(String email, String masterPassword, String supabaseUserId) throws Exception {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        User user = new User();
        // Do not set ID manually - Hibernate will generate UUID automatically
        user.setEmail(email);
        
        // Hash master password for storage
        String masterPasswordHash = passwordEncoder.encode(masterPassword);
        user.setMasterPasswordHash(masterPasswordHash);
        
        // Generate salt for encryption key derivation
        String salt = UUID.randomUUID().toString();
        user.setEncryptionSalt(salt);
        
        // Store Supabase user ID if provided
        if (supabaseUserId != null && !supabaseUserId.isEmpty()) {
            user.setSupabaseUserId(supabaseUserId);
        }
        
        // Set defaults
        user.setUsername("User");
        user.setAvatarUrl(null);
        user.setVaultVersion(1);
        
        return userRepository.save(user);
    }
    
    /**
     * Verify master password
     */
    public boolean verifyMasterPassword(UUID userId, String masterPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return passwordEncoder.matches(masterPassword, user.getMasterPasswordHash());
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Get user by Supabase ID
     */
    public Optional<User> getUserBySupabaseId(String supabaseUserId) {
        return userRepository.findBySupabaseUserId(supabaseUserId);
    }
    
    /**
     * Update user profile
     */
    public User updateProfile(String userIdStr, String username, String avatarUrl) throws Exception {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (username != null) {
            user.setUsername(username);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Convert User entity to UserDTO
     */
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
    
    /**
     * Get encryption key for a user (derived from master password + salt)
     */
    public String getEncryptionKey(String userIdStr, String masterPassword) throws Exception {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return encryptionUtil.deriveKeyFromPassword(masterPassword, user.getEncryptionSalt());
    }
}
