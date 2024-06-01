package com.example.storitechnicaltest.register

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storitechnicaltest.core.domain.AuthRepository
import com.storitechnicaltest.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUIState>(RegisterUIState.Start)
    val uiState = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState = _formState.asStateFlow()

    fun updateFormState(newState: RegisterFormState) {
        _formState.value = newState
    }

    /**
     * Validates the registration form fields and attempts to register the user if all fields are valid.
     * Updates the form state with any validation errors.
     */
    fun validateForm(): Boolean {
        val currentState = _formState.value

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val emailError = if (currentState.email.isEmpty()) "Email cannot be empty"
        else if (!currentState.email.matches(emailPattern.toRegex()))
            "Invalid email format" else null

        val passwordError =
            if (currentState.password.isEmpty()) "Password cannot be empty" else null
        val firstNameError =
            if (currentState.firstName.isEmpty()) "First name cannot be empty" else null
        val lastNameError =
            if (currentState.lastName.isEmpty()) "Last name cannot be empty" else null
        val photoUriError = if (currentState.photoUri == null) "Photo cannot be empty" else null

        _formState.value = currentState.copy(
            emailError = emailError,
            passwordError = passwordError,
            firstNameError = firstNameError,
            lastNameError = lastNameError,
            photoUriError = photoUriError
        )

        return emailError == null && passwordError == null && firstNameError == null &&
                lastNameError == null && photoUriError == null
    }

    /**
     * Registers a new user with a photo.
     *
     * This method launches a coroutine to handle the registration process. It updates the UI state
     * to show loading, attempts to register the user with the provided photo URI,
     * and updates the UI state accordingly based on the registration result.
     *
     */
    fun registerUserWithPhoto() {
        val currentState = _formState.value
        val user = User(
            email = currentState.email,
            password = currentState.password,
            firstName = currentState.firstName,
            lastName = currentState.lastName
        )
        currentState.photoUri?.let { uri ->
            viewModelScope.launch {
                _uiState.emit(RegisterUIState.Loading)
                val result = authRepository.registerUser(user, uri)
                if (result) {
                    _uiState.emit(RegisterUIState.Success)
                } else {
                    _uiState.emit(RegisterUIState.Error("Registration failed"))
                    //Make Sure UI consumes both states
                    delay(100)
                    _uiState.emit(RegisterUIState.Start)
                }
            }
        }
    }

    sealed class RegisterUIState {
        object Start : RegisterUIState()
        object Loading : RegisterUIState()
        object Success : RegisterUIState()
        data class Error(val message: String) : RegisterUIState()
    }

    data class RegisterFormState(
        val email: String = "",
        val password: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val photoUri: Uri? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val photoUriError: String? = null
    )
}
