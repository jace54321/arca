package com.arca.arca_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final com.arca.arca_backend.service.UserService userService;
    private final com.arca.arca_backend.repository.SyncLogRepository syncLogRepository;
    private final com.arca.arca_backend.repository.DeviceRepository deviceRepository;

    public SyncController(com.arca.arca_backend.service.UserService userService,
                          com.arca.arca_backend.repository.SyncLogRepository syncLogRepository,
                          com.arca.arca_backend.repository.DeviceRepository deviceRepository) {
        this.userService = userService;
        this.syncLogRepository = syncLogRepository;
        this.deviceRepository = deviceRepository;
    }
    
    private com.arca.arca_backend.entity.User resolveUser(Authentication authentication) {
        return userService.getUserBySupabaseId(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getSyncLogs(Authentication authentication) {
        try {
            com.arca.arca_backend.entity.User user = resolveUser(authentication);
            var logs = syncLogRepository.findByUserIdOrderByTimestampDesc(user.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", logs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @PostMapping("/trigger")
    public ResponseEntity<?> triggerSync(
            @RequestParam(required = false, defaultValue = "Web Client") String deviceName,
            @RequestParam(required = false, defaultValue = "desktop") String deviceType,
            Authentication authentication) {
        try {
            com.arca.arca_backend.entity.User user = resolveUser(authentication);
            
            // Upsert device
            com.arca.arca_backend.entity.Device device = deviceRepository.findByUserIdAndDeviceName(user.getId(), deviceName)
                .orElse(new com.arca.arca_backend.entity.Device());
            device.setUserId(user.getId());
            device.setDeviceName(deviceName);
            device.setDeviceType(deviceType);
            device.setLastActive(java.time.LocalDateTime.now());
            deviceRepository.save(device);

            // Log sync
            com.arca.arca_backend.entity.SyncLog log = new com.arca.arca_backend.entity.SyncLog();
            log.setUserId(user.getId());
            log.setDevice(deviceName);
            log.setDeviceType(deviceType);
            log.setStatus("synced");
            log.setVersionFrom(1);
            log.setVersionTo(1);
            log.setMessage("Vault successfully synced with server");
            log.setIsCurrentDevice(true);
            syncLogRepository.save(log);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
