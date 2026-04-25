package com.arca.android.ui.screens.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arca.android.data.api.dto.SyncLogDTO
import com.arca.android.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SyncLogsUiState(
    val logs: List<SyncLogDTO> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class SyncLogsViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncLogsUiState())
    val uiState: StateFlow<SyncLogsUiState> = _uiState.asStateFlow()

    init {
        loadSyncLogs()
    }

    fun loadSyncLogs() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch(Dispatchers.IO) {
            val result = syncRepository.getSyncLogs()
            result.fold(
                onSuccess = { logs ->
                    _uiState.value = _uiState.value.copy(
                        logs = logs,
                        isLoading = false,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message,
                    )
                },
            )
        }
    }
}
