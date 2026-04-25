package com.arca.arca_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/devices")
public class DeviceController {

    private final com.arca.arca_backend.service.UserService userService;
    private final com.arca.arca_backend.repository.DeviceRepository deviceRepository;

    public DeviceController(com.arca.arca_backend.service.UserService userService,
                            com.arca.arca_backend.repository.DeviceRepository deviceRepository) {
        this.userService = userService;
        this.deviceRepository = deviceRepository;
    }
    
    private com.arca.arca_backend.entity.User resolveUser(Authentication authentication) {
        return userService.getUserBySupabaseId(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<?> getDevices(Authentication authentication) {
        try {
            com.arca.arca_backend.entity.User user = resolveUser(authentication);
            var devices = deviceRepository.findByUserIdOrderByLastActiveDesc(user.getId());
            
            var dtoList = devices.stream().map(d -> Map.of(
                "id", d.getId(),
                "deviceName", d.getDeviceName(),
                "deviceType", d.getDeviceType(),
                "lastActive", d.getLastActive()
            )).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("success", true, "data", dtoList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
