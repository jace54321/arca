package com.arca.arca_backend.controller;

import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String supabaseUserId = authentication.getName();
            User user = userService.getUserBySupabaseId(supabaseUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "supabaseUserId", user.getSupabaseUserId(),
                "username", user.getUsername() != null ? user.getUsername() : "",
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String supabaseUserId = authentication.getName();
            User user = userService.getUserBySupabaseId(supabaseUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (request.containsKey("username")) {
                user.setUsername(request.get("username"));
            }
            if (request.containsKey("avatarUrl")) {
                user.setAvatarUrl(request.get("avatarUrl"));
            }
            // If the avatarUrl key is present but null/empty, we handle it as setting it to null
            if (request.containsKey("avatarUrl") && request.get("avatarUrl") == null) {
                user.setAvatarUrl(null);
            }
            
            userService.updateUser(user);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
