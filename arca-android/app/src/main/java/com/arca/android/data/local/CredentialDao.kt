package com.arca.android.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {

    // ── Credentials ────────────────────────────────────────────────────────────

    @Query("SELECT * FROM credentials ORDER BY lastModified DESC")
    fun getAllCredentials(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials ORDER BY lastModified DESC")
    suspend fun getAllCredentialsList(): List<CredentialEntity>

    @Query("SELECT * FROM credentials WHERE id = :id")
    suspend fun getCredentialById(id: String): CredentialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: CredentialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCredentials(credentials: List<CredentialEntity>)

    @Update
    suspend fun updateCredential(credential: CredentialEntity)

    @Query("DELETE FROM credentials WHERE id = :id")
    suspend fun deleteCredentialById(id: String)

    @Query("DELETE FROM credentials")
    suspend fun deleteAllCredentials()

    @Query("SELECT COUNT(*) FROM credentials WHERE syncStatus = 'pending'")
    suspend fun getPendingCount(): Int

    @Query("SELECT * FROM credentials WHERE syncStatus = 'pending' OR isLocalOnly = 1")
    suspend fun getUnsyncedCredentials(): List<CredentialEntity>

    // ── Pending Actions ────────────────────────────────────────────────────────

    @Query("SELECT * FROM pending_actions ORDER BY timestamp ASC")
    suspend fun getAllPendingActions(): List<PendingActionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingAction(action: PendingActionEntity)

    @Query("DELETE FROM pending_actions WHERE id = :id")
    suspend fun deletePendingAction(id: Long)

    @Query("DELETE FROM pending_actions WHERE credentialId = :credentialId")
    suspend fun deletePendingActionsForCredential(credentialId: String)

    @Query("DELETE FROM pending_actions")
    suspend fun deleteAllPendingActions()

    @Query("SELECT COUNT(*) FROM pending_actions")
    suspend fun getPendingActionCount(): Int
}
