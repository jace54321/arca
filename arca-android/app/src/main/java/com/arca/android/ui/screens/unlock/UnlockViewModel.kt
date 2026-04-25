package com.arca.android.ui.screens.unlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arca.android.crypto.CryptoManager
import com.arca.android.data.repository.Credential
import com.arca.android.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.crypto.SecretKey
import javax.inject.Inject

data class UnlockUiState(
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val unlockSuccess: Boolean = false,
)

@HiltViewModel
class UnlockViewModel @Inject constructor(
    private val vaultRepository: VaultRepository,
    private val cryptoManager: CryptoManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnlockUiState())
    val uiState: StateFlow<UnlockUiState> = _uiState.asStateFlow()

    // Vault key lives in memory only
    var vaultKey: SecretKey? = null
        private set

    // Decrypted credentials
    var credentials: List<Credential> = emptyList()
        private set

    // Store the auth key hex for later use
    var authKeyHex: String? = null
        private set

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    /**
     * Unlock with derived keys (from login — no re-derivation needed).
     */
    fun unlockWithKeys(keys: CryptoManager.DerivedKeys) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch(Dispatchers.IO) {
            val result = vaultRepository.unlockVault(keys.authKeyHex, keys.vaultKey)

            result.fold(
                onSuccess = { creds ->
                    vaultKey = keys.vaultKey
                    authKeyHex = keys.authKeyHex
                    credentials = creds
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        unlockSuccess = true,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unlock failed",
                    )
                },
            )
        }
    }

    /**
     * Unlock by re-deriving keys from the master password.
     */
    fun unlockWithPassword(email: String) {
        val password = _uiState.value.password
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Enter your master password")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val keys = cryptoManager.deriveKeys(password, email)
                val result = vaultRepository.unlockVault(keys.authKeyHex, keys.vaultKey)

                result.fold(
                    onSuccess = { creds ->
                        vaultKey = keys.vaultKey
                        authKeyHex = keys.authKeyHex
                        credentials = creds
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            unlockSuccess = true,
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Incorrect password.",
                        )
                    },
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Incorrect password.",
                )
            }
        }
    }
}
