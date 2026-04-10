package com.arca.android.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arca.android.data.api.dto.DeviceDTO
import com.arca.android.data.repository.AuthRepository
import com.arca.android.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val username: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val devices: List<DeviceDTO> = emptyList(),
    val isLoadingProfile: Boolean = true,
    val isLoadingDevices: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val logoutTriggered: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        loadDevices()
    }

    private fun loadProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = syncRepository.getProfile()
            result.fold(
                onSuccess = { profile ->
                    _uiState.value = _uiState.value.copy(
                        username = profile.username ?: "",
                        email = profile.email ?: "",
                        avatarUrl = profile.avatarUrl,
                        isLoadingProfile = false,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingProfile = false,
                        error = e.message,
                    )
                },
            )
        }
    }

    private fun loadDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = syncRepository.getDevices()
            result.fold(
                onSuccess = { devices ->
                    _uiState.value = _uiState.value.copy(
                        devices = devices,
                        isLoadingDevices = false,
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoadingDevices = false)
                },
            )
        }
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, saveSuccess = false)
    }

    fun saveProfile() {
        val state = _uiState.value
        _uiState.value = state.copy(isSaving = true, error = null, saveSuccess = false)

        viewModelScope.launch(Dispatchers.IO) {
            val result = syncRepository.updateProfile(state.username, state.avatarUrl)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = e.message,
                    )
                },
            )
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(logoutTriggered = true)
        }
    }
}
