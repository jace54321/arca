package com.arca.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CredentialEntity::class, PendingActionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ArcaDatabase : RoomDatabase() {
    abstract fun credentialDao(): CredentialDao
}
