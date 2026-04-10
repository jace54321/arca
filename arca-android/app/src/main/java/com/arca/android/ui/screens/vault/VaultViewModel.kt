package com.arca.android.ui.screens.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arca.android.data.repository.Credential
import com.arca.android.data.repository.SyncRepository
import com.arca.android.data.repository.VaultRepository
import com.arca.android.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.crypto.SecretKey
import javax.inject.Inject

data class VaultUiState(
    val credentials: List<Credential> = emptyList(),
    val searchQuery: String = "",
    val syncStatus: String = "synced",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddEditSheet: Boolean = false,
    val editingCredential: Credential? = null,
    val isOnline: Boolean = true,
    val unsyncedCount: Int = 0,
)

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository,
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    var vaultKey: SecretKey? = null

    init {
        // Observe connectivity changes
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val wasOffline = !_uiState.value.isOnline
                _uiState.value = _uiState.value.copy(isOnline = online)

                // Auto-sync when coming back online
                if (online && wasOffline) {
                    syncPendingChanges()
                }
            }
        }
    }

    fun setInitialCredentials(credentials: List<Credential>) {
        _uiState.value = _uiState.value.copy(credentials = credentials)
        updateUnsyncedCount()

        // Start observing Room for real-time updates
        vaultKey?.let { key ->
            viewModelScope.launch {
                vaultRepository.observeCredentials(key).collect { creds ->
                    _uiState.value = _uiState.value.copy(credentials = creds)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    val filteredCredentials: List<Credential>
        get() {
            val query = _uiState.value.searchQuery.lowercase()
            if (query.isBlank()) return _uiState.value.credentials
            return _uiState.value.credentials.filter {
                it.siteName.lowercase().contains(query) ||
                    it.username.lowercase().contains(query) ||
                    it.url.lowercase().contains(query)
            }
        }

    fun showAddSheet() {
        _uiState.value = _uiState.value.copy(showAddEditSheet = true, editingCredential = null)
    }

    fun showEditSheet(credential: Credential) {
        _uiState.value = _uiState.value.copy(showAddEditSheet = true, editingCredential = credential)
    }

    fun hideSheet() {
        _uiState.value = _uiState.value.copy(showAddEditSheet = false, editingCredential = null)
    }

    fun addCredential(
        siteName: String,
        url: String?,
        username: String,
        password: String,
        category: String,
        notes: String?,
    ) {
        val key = vaultKey ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val result = vaultRepository.createCredential(
                siteName, url, username, password, category, notes, key,
            )

            result.fold(
                onSuccess = { cred ->
                    _uiState.value = _uiState.value.copy(
                        credentials = listOf(cred) + _uiState.value.credentials,
                        showAddEditSheet = false,
                    )
                    updateUnsyncedCount()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                },
            )
        }
    }

    fun updateCredential(
        id: String,
        siteName: String,
        url: String?,
        username: String,
        password: String,
        category: String,
        notes: String?,
    ) {
        val key = vaultKey ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val result = vaultRepository.updateCredential(
                id, siteName, url, username, password, category, notes, key,
            )

            result.fold(
                onSuccess = { updated ->
                    _uiState.value = _uiState.value.copy(
                        credentials = _uiState.value.credentials.map {
                            if (it.id == id) updated else it
                        },
                        showAddEditSheet = false,
                        editingCredential = null,
                    )
                    updateUnsyncedCount()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                },
            )
        }
    }

    fun deleteCredential(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = vaultRepository.deleteCredential(id)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        credentials = _uiState.value.credentials.filter { it.id != id },
                        showAddEditSheet = false,
                        editingCredential = null,
                    )
                    updateUnsyncedCount()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                },
            )
        }
    }

    fun triggerSync() {
        _uiState.value = _uiState.value.copy(syncStatus = "syncing")

        viewModelScope.launch(Dispatchers.IO) {
            // First process pending offline changes
            syncPendingChanges()

            // Then trigger server-side sync
            val result = syncRepository.triggerSync()
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(syncStatus = "synced")
                    updateUnsyncedCount()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(syncStatus = "error")
                },
            )
        }
    }

    private suspend fun syncPendingChanges() {
        val key = vaultKey ?: return
        try {
            _uiState.value = _uiState.value.copy(syncStatus = "syncing")
            vaultRepository.processPendingActions(key)
            _uiState.value = _uiState.value.copy(syncStatus = "synced")
            updateUnsyncedCount()
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(syncStatus = "error")
        }
    }

    private fun updateUnsyncedCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = vaultRepository.getPendingCount()
            _uiState.value = _uiState.value.copy(unsyncedCount = count)
        }
    }
}
