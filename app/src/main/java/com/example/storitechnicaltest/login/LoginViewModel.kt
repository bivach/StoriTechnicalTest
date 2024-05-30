package com.example.storitechnicaltest.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storitechnicaltest.core.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Start)
    val uiState = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(LoginFormState())
    val formState = _formState.asStateFlow()

    fun updateFormState(newState: LoginFormState) {
        _formState.value = newState
    }

    fun validateForm(): Boolean {
        val currentState = _formState.value
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        val emailError = when {
            currentState.email.trim().isEmpty() -> "Email cannot be empty"
            !currentState.email.trim().matches(emailPattern) -> "Invalid email format"
            else -> null
        }

        val passwordError = if (currentState.password.isEmpty()) "Password cannot be empty" else null

        val valid = emailError == null && passwordError == null
        if (!valid) {
            _formState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError
            )
        }
        return valid
    }

    fun loginUser() {
        val currentState = _formState.value
        viewModelScope.launch {
            _uiState.emit(LoginUIState.Loading)
            val result = authRepository.loginUser(currentState.email.trim(), currentState.password)
            if (result) {
                _uiState.emit(LoginUIState.Success)
            } else {
                _uiState.emit(LoginUIState.Error("Authentication failed"))
                //Make Sure UI consumes both states
                delay(100)
                _uiState.emit(LoginUIState.Start)
            }
        }
    }

    sealed class LoginUIState {
        object Start : LoginUIState()
        object Loading : LoginUIState()
        object Success : LoginUIState()
        data class Error(val message: String) : LoginUIState()
    }

    data class LoginFormState(
        val email: String = "",
        val password: String = "",
        val emailError: String? = null,
        val passwordError: String? = null,
    )
}
