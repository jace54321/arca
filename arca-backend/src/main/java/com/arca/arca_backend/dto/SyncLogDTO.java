package com.arca.arca_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncLogDTO {
    private UUID id;
    private String device;
    private String deviceType;
    private String timestamp;
    private String status;
    private Integer versionFrom;
    private Integer versionTo;
    private String message;
    private Boolean isCurrentDevice;
}
