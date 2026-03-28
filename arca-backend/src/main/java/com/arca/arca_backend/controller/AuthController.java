package com.arca.arca_backend.controller;

import com.arca.arca_backend.dto.ApiResponse;
import com.arca.arca_backend.dto.LoginRequest;
import com.arca.arca_backend.dto.LoginResponse;
import com.arca.arca_backend.dto.UnlockVaultRequest;
import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Register a new user with email and master password
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody LoginRequest request) {
        try {
            System.out.println("DEBUG: Register request received");
            System.out.println("DEBUG: Email: " + request.getEmail());
            System.out.println("DEBUG: SupabaseUserId: " + request.getSupabaseUserId());
            
            if (request.getEmail() == null || request.getMasterPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Email and master password are required", null));
            }
            
            User user = userService.registerUser(request.getEmail(), request.getMasterPassword(), request.getSupabaseUserId());
            System.out.println("DEBUG: User registered with ID: " + user.getId());
            System.out.println("DEBUG: User SupabaseUserId: " + user.getSupabaseUserId());
            
            return ResponseEntity.ok(new ApiResponse(true, "User registered successfully", userService.toDTO(user)));
        } catch (Exception e) {
            System.err.println("DEBUG: Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Login - Supabase JWT is handled by Spring Security
     * This endpoint validates that the user's master password matches
     * Protected by Spring Security JWT filter
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UnlockVaultRequest request) {
        try {
            // Extract Supabase user ID from Security Context (JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                System.err.println("DEBUG: Authentication is null or not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, null, null));
            }
            
            String supabaseUserId = authentication.getName();  // This is the 'sub' from JWT
            System.out.println("DEBUG: Extracted supabaseUserId from JWT: " + supabaseUserId);
            System.out.println("DEBUG: Authentication details: " + authentication.getDetails());
            
            // Find user by Supabase ID
            Optional<User> userOpt = userService.getUserBySupabaseId(supabaseUserId);
            System.out.println("DEBUG: User lookup result: " + (userOpt.isPresent() ? "FOUND" : "NOT FOUND"));
            
            if (userOpt.isEmpty()) {
                System.err.println("DEBUG: User not found with Supabase ID: " + supabaseUserId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new LoginResponse(false, null, null));
            }
            
            User user = userOpt.get();
            System.out.println("DEBUG: User found: " + user.getEmail());
            
            // Verify master password
            boolean masterPasswordValid = userService.verifyMasterPassword(user.getId(), request.getMasterPassword());
            System.out.println("DEBUG: Master password valid: " + masterPasswordValid);
            
            if (!masterPasswordValid) {
                System.err.println("DEBUG: Invalid master password for user: " + user.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, null, null));
            }
            
            // In production, generate a session token or refresh token here
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setUser(userService.toDTO(user));
            
            System.out.println("DEBUG: Login successful for user: " + user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("DEBUG: Login exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(false, null, null));
        }
    }
}
