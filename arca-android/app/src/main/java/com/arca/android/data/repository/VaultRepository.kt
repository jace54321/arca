package com.arca.android.data.repository

import com.arca.android.crypto.CryptoManager
import com.arca.android.data.api.ArcaApiService
import com.arca.android.data.api.AuthInterceptor
import com.arca.android.data.api.dto.CredentialDTO
import com.arca.android.data.local.CredentialDao
import com.arca.android.data.local.CredentialEntity
import com.arca.android.data.local.PendingActionEntity
import com.arca.android.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local representation of a credential with the password in plaintext.
 * Only exists in memory — never persisted as plaintext.
 */
data class Credential(
    val id: String,
    val siteName: String,
    val url: String,
    val username: String,
    val password: String,       // decrypted plaintext — memory only
    val category: String,
    val notes: String?,
    val syncStatus: String,
    val offlineModified: Boolean,
    val lastModified: String,
)

/**
 * Offline-first vault repository.
 *
 * Strategy:
 * - Room DB is the single source of truth for credentials (stored encrypted).
 * - On unlock: fetch from server → populate Room → return decrypted list from Room.
 * - On CRUD while online: hit API first → update Room cache on success.
 * - On CRUD while offline: update Room immediately + queue PendingAction.
 * - On reconnect: process pending actions queue → sync with server.
 */
@Singleton
class VaultRepository @Inject constructor(
    private val apiService: ArcaApiService,
    private val authInterceptor: AuthInterceptor,
    private val cryptoManager: CryptoManager,
    private val credentialDao: CredentialDao,
    private val networkMonitor: NetworkMonitor,
) {

    // ── Unlock ─────────────────────────────────────────────────────────────────

    /**
     * Unlock the vault: verify authKeyHex + fetch from server + cache in Room.
     * Falls back to Room cache if offline.
     */
    suspend fun unlockVault(
        authKeyHex: String,
        vaultKey: SecretKey,
    ): Result<List<Credential>> {
        return try {
            if (networkMonitor.isCurrentlyOnline()) {
                // Online: fetch from server and cache
                authInterceptor.setAuthKey(authKeyHex)
                val response = apiService.unlockVault()
                authInterceptor.setAuthKey(null)

                if (!response.isSuccessful) {
                    // Fall back to local cache on server error
                    return loadFromCache(vaultKey)
                }

                val body = response.body()
                if (body?.success != true) {
                    return Result.failure(Exception(body?.error ?: "Vault unlock failed"))
                }

                // Cache encrypted credentials in Room
                val entities = (body.data ?: emptyList()).map { dto -> dtoToEntity(dto) }
                credentialDao.deleteAllCredentials()
                credentialDao.insertAllCredentials(entities)

                // Process any pending offline actions
                processPendingActions(vaultKey)

                // Return decrypted from cache
                loadFromCache(vaultKey)
            } else {
                // Offline: use cached data
                loadFromCache(vaultKey)
            }
        } catch (e: Exception) {
            authInterceptor.setAuthKey(null)
            // On network error, try cache
            val cached = loadFromCache(vaultKey)
            if (cached.isSuccess && cached.getOrNull()?.isNotEmpty() == true) {
                cached
            } else {
                Result.failure(e)
            }
        }
    }

    // ── Read from cache ────────────────────────────────────────────────────────

    /**
     * Observable Flow of credentials from Room (decrypted in memory).
     */
    fun observeCredentials(vaultKey: SecretKey): Flow<List<Credential>> {
        return credentialDao.getAllCredentials().map { entities ->
            entities.map { entity -> entityToCredential(entity, vaultKey) }
        }
    }

    /**
     * One-shot load from Room cache.
     */
    private suspend fun loadFromCache(vaultKey: SecretKey): Result<List<Credential>> {
        return try {
            val entities = credentialDao.getAllCredentialsList()
            val credentials = entities.map { entityToCredential(it, vaultKey) }
            Result.success(credentials)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Create ─────────────────────────────────────────────────────────────────

    /**
     * Create a new credential.
     * Online: API first → cache in Room.
     * Offline: Save to Room with local ID + queue pending action.
     */
    suspend fun createCredential(
        siteName: String,
        url: String?,
        username: String,
        password: String,
        category: String,
        notes: String?,
        vaultKey: SecretKey,
    ): Result<Credential> {
        val encrypted = cryptoManager.encryptField(password, vaultKey)

        if (networkMonitor.isCurrentlyOnline()) {
            return try {
                val dto = CredentialDTO(
                    siteName = siteName,
                    url = url,
                    username = username,
                    encryptedPassword = encrypted.ciphertext,
                    iv = encrypted.iv,
                    cryptoVersion = CryptoManager.CRYPTO_VERSION,
                    category = category,
                    notes = notes,
                )

                val response = apiService.createCredential(dto)
                if (!response.isSuccessful) {
                    return createOffline(siteName, url, username, password, encrypted, category, notes)
                }

                val saved = response.body()
                    ?: return createOffline(siteName, url, username, password, encrypted, category, notes)

                // Cache server response
                val entity = dtoToEntity(saved)
                credentialDao.insertCredential(entity)

                Result.success(
                    Credential(
                        id = saved.id ?: "",
                        siteName = saved.siteName,
                        url = saved.url ?: "",
                        username = saved.username,
                        password = password,
                        category = saved.category,
                        notes = saved.notes,
                        syncStatus = "synced",
                        offlineModified = false,
                        lastModified = saved.lastModified ?: "",
                    )
                )
            } catch (e: Exception) {
                // Network failed mid-request — save locally
                createOffline(siteName, url, username, password, encrypted, category, notes)
            }
        } else {
            return createOffline(siteName, url, username, password, encrypted, category, notes)
        }
    }

    private suspend fun createOffline(
        siteName: String,
        url: String?,
        username: String,
        password: String,
        encrypted: CryptoManager.EncryptedField,
        category: String,
        notes: String?,
    ): Result<Credential> {
        val localId = "local-${UUID.randomUUID()}"
        val now = System.currentTimeMillis().toString()

        val entity = CredentialEntity(
            id = localId,
            siteName = siteName,
            url = url ?: "",
            username = username,
            encryptedPassword = encrypted.ciphertext,
            iv = encrypted.iv,
            cryptoVersion = CryptoManager.CRYPTO_VERSION,
            category = category,
            notes = notes,
            syncStatus = "pending",
            offlineModified = true,
            lastModified = now,
            isLocalOnly = true,
        )

        credentialDao.insertCredential(entity)
        credentialDao.insertPendingAction(
            PendingActionEntity(credentialId = localId, action = "create")
        )

        return Result.success(
            Credential(
                id = localId,
                siteName = siteName,
                url = url ?: "",
                username = username,
                password = password,
                category = category,
                notes = notes,
                syncStatus = "pending",
                offlineModified = true,
                lastModified = now,
            )
        )
    }

    // ── Update ─────────────────────────────────────────────────────────────────

    /**
     * Update an existing credential.
     * Online: API first → update Room.
     * Offline: Update Room + queue pending action.
     */
    suspend fun updateCredential(
        id: String,
        siteName: String,
        url: String?,
        username: String,
        password: String,
        category: String,
        notes: String?,
        vaultKey: SecretKey,
    ): Result<Credential> {
        val encrypted = cryptoManager.encryptField(password, vaultKey)

        if (networkMonitor.isCurrentlyOnline() && !id.startsWith("local-")) {
            return try {
                val dto = CredentialDTO(
                    siteName = siteName,
                    url = url,
                    username = username,
                    encryptedPassword = encrypted.ciphertext,
                    iv = encrypted.iv,
                    cryptoVersion = CryptoManager.CRYPTO_VERSION,
                    category = category,
                    notes = notes,
                )

                val response = apiService.updateCredential(id, dto)
                if (!response.isSuccessful) {
                    return updateOffline(id, siteName, url, username, password, encrypted, category, notes)
                }

                val saved = response.body()
                    ?: return updateOffline(id, siteName, url, username, password, encrypted, category, notes)

                // Update cache
                val entity = dtoToEntity(saved).copy(id = id)
                credentialDao.insertCredential(entity)

                Result.success(
                    Credential(
                        id = saved.id ?: id,
                        siteName = saved.siteName,
                        url = saved.url ?: "",
                        username = saved.username,
                        password = password,
                        category = saved.category,
                        notes = saved.notes,
                        syncStatus = "synced",
                        offlineModified = false,
                        lastModified = saved.lastModified ?: "",
                    )
                )
            } catch (e: Exception) {
                updateOffline(id, siteName, url, username, password, encrypted, category, notes)
            }
        } else {
            return updateOffline(id, siteName, url, username, password, encrypted, category, notes)
        }
    }

    private suspend fun updateOffline(
        id: String,
        siteName: String,
        url: String?,
        username: String,
        password: String,
        encrypted: CryptoManager.EncryptedField,
        category: String,
        notes: String?,
    ): Result<Credential> {
        val now = System.currentTimeMillis().toString()
        val existing = credentialDao.getCredentialById(id)

        val entity = CredentialEntity(
            id = id,
            siteName = siteName,
            url = url ?: "",
            username = username,
            encryptedPassword = encrypted.ciphertext,
            iv = encrypted.iv,
            cryptoVersion = CryptoManager.CRYPTO_VERSION,
            category = category,
            notes = notes,
            syncStatus = "pending",
            offlineModified = true,
            lastModified = now,
            isLocalOnly = existing?.isLocalOnly ?: false,
        )

        credentialDao.insertCredential(entity)

        // Only add pending action if not already a local-only create
        if (existing?.isLocalOnly != true) {
            credentialDao.insertPendingAction(
                PendingActionEntity(credentialId = id, action = "update")
            )
        }

        return Result.success(
            Credential(
                id = id,
                siteName = siteName,
                url = url ?: "",
                username = username,
                password = password,
                category = category,
                notes = notes,
                syncStatus = "pending",
                offlineModified = true,
                lastModified = now,
            )
        )
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    /**
     * Delete a credential.
     * Online: API first → remove from Room.
     * Offline: Remove from Room + queue pending action (unless local-only).
     */
    suspend fun deleteCredential(id: String): Result<Unit> {
        if (networkMonitor.isCurrentlyOnline() && !id.startsWith("local-")) {
            return try {
                val response = apiService.deleteCredential(id)
                if (response.isSuccessful) {
                    credentialDao.deleteCredentialById(id)
                    credentialDao.deletePendingActionsForCredential(id)
                    Result.success(Unit)
                } else {
                    deleteOffline(id)
                }
            } catch (e: Exception) {
                deleteOffline(id)
            }
        } else {
            return deleteOffline(id)
        }
    }

    private suspend fun deleteOffline(id: String): Result<Unit> {
        val existing = credentialDao.getCredentialById(id)

        if (existing?.isLocalOnly == true) {
            // Never synced — just remove locally
            credentialDao.deleteCredentialById(id)
            credentialDao.deletePendingActionsForCredential(id)
        } else {
            // Mark for deletion + queue action
            credentialDao.deleteCredentialById(id)
            credentialDao.insertPendingAction(
                PendingActionEntity(credentialId = id, action = "delete")
            )
        }

        return Result.success(Unit)
    }

    // ── Sync pending actions ───────────────────────────────────────────────────

    /**
     * Process all queued offline actions.
     * Called automatically on vault unlock when online, or manually via triggerSync().
     */
    suspend fun processPendingActions(vaultKey: SecretKey) {
        val actions = credentialDao.getAllPendingActions()
        if (actions.isEmpty()) return

        for (action in actions) {
            try {
                when (action.action) {
                    "create" -> {
                        val entity = credentialDao.getCredentialById(action.credentialId)
                            ?: continue

                        val dto = CredentialDTO(
                            siteName = entity.siteName,
                            url = entity.url.ifBlank { null },
                            username = entity.username,
                            encryptedPassword = entity.encryptedPassword,
                            iv = entity.iv,
                            cryptoVersion = entity.cryptoVersion,
                            category = entity.category,
                            notes = entity.notes,
                        )

                        val response = apiService.createCredential(dto)
                        if (response.isSuccessful) {
                            val saved = response.body()
                            if (saved?.id != null) {
                                // Replace local entity with server entity
                                credentialDao.deleteCredentialById(action.credentialId)
                                credentialDao.insertCredential(dtoToEntity(saved))
                            }
                            credentialDao.deletePendingAction(action.id)
                        }
                    }

                    "update" -> {
                        val entity = credentialDao.getCredentialById(action.credentialId)
                        if (entity == null) {
                            credentialDao.deletePendingAction(action.id)
                            continue
                        }

                        val dto = CredentialDTO(
                            siteName = entity.siteName,
                            url = entity.url.ifBlank { null },
                            username = entity.username,
                            encryptedPassword = entity.encryptedPassword,
                            iv = entity.iv,
                            cryptoVersion = entity.cryptoVersion,
                            category = entity.category,
                            notes = entity.notes,
                        )

                        val response = apiService.updateCredential(action.credentialId, dto)
                        if (response.isSuccessful) {
                            // Update sync status
                            credentialDao.insertCredential(
                                entity.copy(syncStatus = "synced", offlineModified = false)
                            )
                            credentialDao.deletePendingAction(action.id)
                        }
                    }

                    "delete" -> {
                        val response = apiService.deleteCredential(action.credentialId)
                        if (response.isSuccessful || response.code() == 404) {
                            credentialDao.deletePendingAction(action.id)
                        }
                    }
                }
            } catch (_: Exception) {
                // Network failed — leave the action in the queue for next sync
            }
        }
    }

    /**
     * Get count of pending offline changes.
     */
    suspend fun getPendingCount(): Int {
        return credentialDao.getPendingActionCount()
    }

    // ── Entity/DTO mapping ─────────────────────────────────────────────────────

    private fun dtoToEntity(dto: CredentialDTO): CredentialEntity {
        return CredentialEntity(
            id = dto.id ?: UUID.randomUUID().toString(),
            siteName = dto.siteName,
            url = dto.url ?: "",
            username = dto.username,
            encryptedPassword = dto.encryptedPassword,
            iv = dto.iv,
            cryptoVersion = dto.cryptoVersion,
            category = dto.category,
            notes = dto.notes,
            syncStatus = dto.syncStatus ?: "synced",
            offlineModified = dto.offlineModified ?: false,
            lastModified = dto.lastModified ?: "",
            isLocalOnly = false,
        )
    }

    private fun entityToCredential(entity: CredentialEntity, vaultKey: SecretKey): Credential {
        val password = if (entity.cryptoVersion == CryptoManager.CRYPTO_VERSION &&
            entity.encryptedPassword.isNotBlank() &&
            entity.iv.isNotBlank()
        ) {
            try {
                cryptoManager.decryptField(entity.encryptedPassword, entity.iv, vaultKey)
            } catch (_: Exception) {
                entity.encryptedPassword // Decryption failed — show ciphertext (wrong key)
            }
        } else {
            entity.encryptedPassword
        }

        return Credential(
            id = entity.id,
            siteName = entity.siteName,
            url = entity.url,
            username = entity.username,
            password = password,
            category = entity.category,
            notes = entity.notes,
            syncStatus = entity.syncStatus,
            offlineModified = entity.offlineModified,
            lastModified = entity.lastModified,
        )
    }
}
