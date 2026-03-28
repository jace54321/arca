package com.arca.arca_backend.controller;

import com.arca.arca_backend.dto.ApiResponse;
import com.arca.arca_backend.dto.SyncLogDTO;
import com.arca.arca_backend.service.SyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
public class SyncController {
    
    private final SyncService syncService;
    
    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }
    
    /**
     * Get all sync logs for the authenticated user
     * Protected by Spring Security
     */
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse> getSyncLogs() {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            List<SyncLogDTO> logs = syncService.getSyncLogs(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Sync logs retrieved", logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Trigger a sync event
     * Protected by Spring Security
     * 
     * Query parameters:
     * - deviceName: Name of current device (optional, defaults to "Web Client")
     * - deviceType: 'mobile' or 'desktop' (optional, defaults to 'desktop')
     */
    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse> triggerSync(
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String deviceType) {
        try {
            String userId = extractUserIdFromContext();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Unauthorized", null));
            }
            
            // Set defaults
            if (deviceName == null || deviceName.isEmpty()) {
                deviceName = "Web Client";
            }
            if (deviceType == null || deviceType.isEmpty()) {
                deviceType = "desktop";
            }
            
            SyncLogDTO syncLog = syncService.triggerSync(userId, deviceName, deviceType);
            return ResponseEntity.ok(new ApiResponse(true, "Sync triggered successfully", syncLog));
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
