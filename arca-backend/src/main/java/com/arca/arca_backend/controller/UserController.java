package com.arca.arca_backend.controller;

import com.arca.arca_backend.dto.ApiResponse;
import com.arca.arca_backend.dto.UpdateProfileRequest;
import com.arca.arca_backend.dto.UserDTO;
import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Get current user profile
     * Protected by Spring Security
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            Optional<User> userOpt = userService.getUserById(UUID.fromString(userId));
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "User not found", null));
            }
            
            UserDTO userDTO = userService.toDTO(userOpt.get());
            return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved", userDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Update user profile (username, avatar)
     * Protected by Spring Security
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            User updatedUser = userService.updateProfile(userId, request.getUsername(), request.getAvatarUrl());
            UserDTO userDTO = userService.toDTO(updatedUser);
            
            return ResponseEntity.ok(new ApiResponse(true, "Profile updated", userDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Helper method to extract user ID from Security Context
     */
    private String extractUserIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
