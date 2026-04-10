package com.arca.android.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arca.android.crypto.CryptoManager
import com.arca.android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoginMode: Boolean = true,        // true = login, false = register
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val agreedToTerms: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Store derived keys in memory for passing to UnlockScreen
    var derivedKeys: CryptoManager.DerivedKeys? = null
        private set

    fun setLoginMode(isLogin: Boolean) {
        _uiState.value = _uiState.value.copy(isLoginMode = isLogin, error = null)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun updateConfirmPassword(password: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = password, error = null)
    }

    fun setAgreedToTerms(agreed: Boolean) {
        _uiState.value = _uiState.value.copy(agreedToTerms = agreed)
    }

    fun submit() {
        val state = _uiState.value

        // Validation
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Email and password are required")
            return
        }

        if (!state.isLoginMode) {
            if (state.password != state.confirmPassword) {
                _uiState.value = state.copy(error = "Passwords do not match")
                return
            }
            if (!state.agreedToTerms) {
                _uiState.value = state.copy(error = "You must acknowledge the password warning")
                return
            }
            if (state.password.length < 8) {
                _uiState.value = state.copy(error = "Password must be at least 8 characters")
                return
            }
        }

        _uiState.value = state.copy(isLoading = true, error = null)

        viewModelScope.launch(Dispatchers.IO) {
            val result = if (state.isLoginMode) {
                authRepository.login(state.email, state.password)
            } else {
                authRepository.register(state.email, state.password)
            }

            result.fold(
                onSuccess = { keys ->
                    derivedKeys = keys
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = true,
                        error = null,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Authentication failed",
                    )
                },
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
