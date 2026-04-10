package com.arca.arca_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @deprecated The unlock endpoint now reads the auth key from the X-Auth-Key header,
 * not a request body. This DTO is kept for reference but is no longer used.
 */
@Deprecated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnlockVaultRequest {
    private String authKeyHex;
}
