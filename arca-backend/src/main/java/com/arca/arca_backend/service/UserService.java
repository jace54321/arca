package com.arca.arca_backend.service;

import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user or return the existing one.
     * On first creation, stores a BCrypt hash of the client-derived auth key.
     */
    public User createOrGetUser(String supabaseUserId, String email, String authKeyHex) {
        Optional<User> existing = userRepository.findBySupabaseUserId(supabaseUserId);
        if (existing.isPresent()) {
            User user = existing.get();
            // Migrate existing users: set authKeyHash on first login if not yet stored
            if (user.getAuthKeyHash() == null && authKeyHex != null && !authKeyHex.isBlank()) {
                user.setAuthKeyHash(passwordEncoder.encode(authKeyHex));
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
            return user;
        }

        User user = new User();
        user.setSupabaseUserId(supabaseUserId);
        user.setEmail(email);
        if (authKeyHex != null && !authKeyHex.isBlank()) {
            user.setAuthKeyHash(passwordEncoder.encode(authKeyHex));
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Verify the submitted auth key against the stored BCrypt hash.
     * If no hash is stored yet (legacy account), accept and store it (migration).
     *
     * @return true if the key matches or was successfully enrolled
     */
    public boolean verifyAuthKey(String supabaseUserId, String authKeyHex) {
        Optional<User> opt = userRepository.findBySupabaseUserId(supabaseUserId);
        if (opt.isEmpty()) return false;

        User user = opt.get();

        if (user.getAuthKeyHash() == null) {
            // First login for a legacy account — enroll the auth key transparently
            if (authKeyHex != null && !authKeyHex.isBlank()) {
                user.setAuthKeyHash(passwordEncoder.encode(authKeyHex));
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return true;
            }
            return false;
        }

        return passwordEncoder.matches(authKeyHex, user.getAuthKeyHash());
    }

    public Optional<User> getUserBySupabaseId(String supabaseUserId) {
        return userRepository.findBySupabaseUserId(supabaseUserId);
    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
