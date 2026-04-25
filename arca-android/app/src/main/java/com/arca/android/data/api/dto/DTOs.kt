package com.arca.android.data.api.dto

import com.google.gson.annotations.SerializedName

// ── Auth DTOs ──────────────────────────────────────────────────────────────────

data class RegisterRequest(
    val email: String,
    val authKeyHex: String,
    val supabaseUserId: String,
)

data class AuthResponse(
    val id: String?,
    val email: String?,
    val supabaseUserId: String?,
    val success: Boolean?,
    val username: String?,
    val avatarUrl: String?,
    val error: String?,
)

// ── Credential DTOs ────────────────────────────────────────────────────────────

data class CredentialDTO(
    val id: String? = null,
    val siteName: String,
    val url: String? = null,
    val username: String,
    val encryptedPassword: String,
    val iv: String,
    val cryptoVersion: Int = 1,
    val category: String = "Other",
    val notes: String? = null,
    val syncStatus: String? = null,
    val offlineModified: Boolean? = null,
    val lastModified: String? = null,
    val versionNumber: Int? = null,
)

// ── Vault unlock response ──────────────────────────────────────────────────────

data class VaultUnlockResponse(
    val success: Boolean,
    val userId: String?,
    val data: List<CredentialDTO>?,
    val error: String?,
)

// ── Sync DTOs ──────────────────────────────────────────────────────────────────

data class SyncLogDTO(
    val id: String,
    val device: String,
    val deviceType: String,
    val timestamp: String,
    val status: String,
    val versionFrom: Int?,
    val versionTo: Int?,
    val message: String?,
    val isCurrentDevice: Boolean?,
)

data class SyncLogsResponse(
    val success: Boolean,
    val data: List<SyncLogDTO>?,
    val error: String?,
)

data class SyncTriggerResponse(
    val success: Boolean,
    val error: String?,
)

// ── Device DTOs ────────────────────────────────────────────────────────────────

data class DeviceDTO(
    val id: String,
    val deviceName: String,
    val deviceType: String,
    val lastActive: String,
)

data class DevicesResponse(
    val success: Boolean,
    val data: List<DeviceDTO>?,
    val error: String?,
)

// ── User DTOs ──────────────────────────────────────────────────────────────────

data class ProfileResponse(
    val id: String?,
    val email: String?,
    val supabaseUserId: String?,
    val username: String?,
    val avatarUrl: String?,
    val error: String?,
)

data class UpdateProfileRequest(
    val username: String?,
    val avatarUrl: String?,
)

data class GenericResponse(
    val success: Boolean?,
    val message: String?,
    val error: String?,
)
