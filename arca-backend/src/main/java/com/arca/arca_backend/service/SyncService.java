package com.arca.arca_backend.service;

import com.arca.arca_backend.dto.SyncLogDTO;
import com.arca.arca_backend.entity.SyncLog;
import com.arca.arca_backend.entity.User;
import com.arca.arca_backend.repository.SyncLogRepository;
import com.arca.arca_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SyncService {
    
    private final SyncLogRepository syncLogRepository;
    private final UserRepository userRepository;
    
    public SyncService(SyncLogRepository syncLogRepository, UserRepository userRepository) {
        this.syncLogRepository = syncLogRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Get all sync logs for a user
     */
    public List<SyncLogDTO> getSyncLogs(String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        List<SyncLog> logs = syncLogRepository.findByUserIdOrderByTimestampDesc(userId);
        return logs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Trigger a sync event for the user
     */
    public SyncLogDTO triggerSync(String userIdStr, String deviceName, String deviceType) throws Exception {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        SyncLog syncLog = new SyncLog();
        // Do not set ID manually - Hibernate will generate it
        syncLog.setUserId(userId);
        syncLog.setDevice(deviceName);
        syncLog.setDeviceType(deviceType);
        syncLog.setStatus("synced");
        syncLog.setVersionFrom(user.getVaultVersion());
        
        // Increment vault version
        user.setVaultVersion(user.getVaultVersion() + 1);
        userRepository.save(user);
        
        syncLog.setVersionTo(user.getVaultVersion());
        syncLog.setMessage("Vault synchronized successfully");
        syncLog.setIsCurrentDevice(true);
        
        SyncLog saved = syncLogRepository.save(syncLog);
        return toDTO(saved);
    }
    
    /**
     * Record a sync conflict
     */
    public SyncLogDTO recordSyncConflict(String userIdStr, String deviceName, String deviceType, String message) throws Exception {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        SyncLog syncLog = new SyncLog();
        // Do not set ID manually - Hibernate will generate it
        syncLog.setUserId(userId);
        syncLog.setDevice(deviceName);
        syncLog.setDeviceType(deviceType);
        syncLog.setStatus("conflict");
        syncLog.setVersionFrom(user.getVaultVersion());
        syncLog.setVersionTo(user.getVaultVersion());
        syncLog.setMessage(message != null ? message : "Sync conflict detected and auto-resolved (last-write-wins)");
        syncLog.setIsCurrentDevice(false);
        
        SyncLog saved = syncLogRepository.save(syncLog);
        return toDTO(saved);
    }
    
    /**
     * Record a sync error
     */
    public SyncLogDTO recordSyncError(String userIdStr, String deviceName, String deviceType, String errorMessage) throws Exception {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        SyncLog syncLog = new SyncLog();
        // Do not set ID manually - Hibernate will generate it
        syncLog.setUserId(userId);
        syncLog.setDevice(deviceName);
        syncLog.setDeviceType(deviceType);
        syncLog.setStatus("error");
        syncLog.setVersionFrom(user.getVaultVersion());
        syncLog.setVersionTo(user.getVaultVersion());
        syncLog.setMessage(errorMessage != null ? errorMessage : "Sync failed");
        syncLog.setIsCurrentDevice(false);
        
        SyncLog saved = syncLogRepository.save(syncLog);
        return toDTO(saved);
    }
    
    /**
     * Convert SyncLog entity to SyncLogDTO
     */
    private SyncLogDTO toDTO(SyncLog syncLog) {
        SyncLogDTO dto = new SyncLogDTO();
        dto.setId(syncLog.getId());
        dto.setDevice(syncLog.getDevice());
        dto.setDeviceType(syncLog.getDeviceType());
        dto.setTimestamp(syncLog.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setStatus(syncLog.getStatus());
        dto.setVersionFrom(syncLog.getVersionFrom());
        dto.setVersionTo(syncLog.getVersionTo());
        dto.setMessage(syncLog.getMessage());
        dto.setIsCurrentDevice(syncLog.getIsCurrentDevice());
        return dto;
    }
}
