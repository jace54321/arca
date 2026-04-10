package com.arca.arca_backend.controller;

import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user.
     * Receives the client-derived auth key hex (PBKDF2 output) — never the raw master password.
     * Stores BCrypt(authKeyHex) in the users table.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String supabaseUserId = request.get("supabaseUserId");
            String email = request.get("email");
            String authKeyHex = request.get("authKeyHex");

            if (supabaseUserId == null || email == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing supabaseUserId or email"));
            }

            User user = userService.createOrGetUser(supabaseUserId, email, authKeyHex);
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "supabaseUserId", user.getSupabaseUserId(),
                "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Login: verify the Supabase JWT (handled by Spring Security) plus the secondary
     * auth key check (proves the user knows the master password, not just the JWT).
     *
     * The auth key hex is expected in the X-Auth-Key request header.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestHeader(value = "X-Auth-Key", required = false) String authKeyHex,
            Authentication authentication) {
        try {
            String supabaseUserId = authentication.getName();

            // Ensure user record exists (auto-create for accounts that pre-date this version)
            userService.getUserBySupabaseId(supabaseUserId)
                .orElseGet(() -> userService.createOrGetUser(supabaseUserId, "user@example.com", authKeyHex));

            // Verify auth key
            if (authKeyHex != null && !authKeyHex.isBlank()) {
                boolean valid = userService.verifyAuthKey(supabaseUserId, authKeyHex);
                if (!valid) {
                    return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "error", "Invalid master password"
                    ));
                }
            }

            User user = userService.getUserBySupabaseId(supabaseUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "supabaseUserId", user.getSupabaseUserId(),
                "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            String supabaseUserId = authentication.getName();
            User user = userService.getUserBySupabaseId(supabaseUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "supabaseUserId", user.getSupabaseUserId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
