package com.arca.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for cached credentials.
 *
 * IMPORTANT: Passwords are stored in their ENCRYPTED form (ciphertext + IV).
 * Plaintext NEVER touches the database. Decryption happens in memory only
 * when the vault key is available.
 */
@Entity(tableName = "credentials")
data class CredentialEntity(
    @PrimaryKey
    val id: String,               // Server-assigned UUID (or local UUID for offline-created)
    val siteName: String,
    val url: String = "",
    val username: String,
    val encryptedPassword: String, // AES-256-GCM ciphertext (base64) — NEVER plaintext
    val iv: String,                // AES-GCM IV (base64)
    val cryptoVersion: Int = 1,
    val category: String = "Other",
    val notes: String? = null,
    val syncStatus: String = "synced",   // synced | pending | error
    val offlineModified: Boolean = false,
    val lastModified: String = "",
    val isLocalOnly: Boolean = false,    // true if created offline and not yet synced
)

/**
 * Tracks pending offline actions that need to be synced when connectivity returns.
 */
@Entity(tableName = "pending_actions")
data class PendingActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val credentialId: String,            // ID of the affected credential
    val action: String,                  // "create" | "update" | "delete"
    val timestamp: Long = System.currentTimeMillis(),
)
