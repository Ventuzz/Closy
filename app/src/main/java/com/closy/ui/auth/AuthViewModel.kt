package com.closy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index, errorMessage = null) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onSubmit(onAuthSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.selectedTab == 0) {
            // Login Validation
            if (currentState.email.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Ingresa tu correo electrónico") }
                return
            }
            if (!currentState.email.contains("@") || !currentState.email.contains(".")) {
                _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido") }
                return
            }
            if (currentState.password.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Ingresa tu contraseña") }
                return
            }
            if (currentState.password.length < 6) {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
        } else {
            // Register Validation
            if (currentState.name.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Ingresa tu nombre completo") }
                return
            }
            if (currentState.email.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Ingresa tu correo electrónico") }
                return
            }
            if (!currentState.email.contains("@") || !currentState.email.contains(".")) {
                _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido") }
                return
            }
            if (currentState.password.length < 6) {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
            if (currentState.password != currentState.confirmPassword) {
                _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (currentState.selectedTab == 0) {
                repository.login(currentState.email, currentState.password)
            } else {
                repository.signUp(currentState.name, currentState.email, currentState.password)
            }

            result.fold(
                onSuccess = {
                    _uiState.update { state -> state.copy(isLoading = false, isAuthenticated = true) }
                    onAuthSuccess()
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Ocurrió un error"
                        )
                    }
                }
            )
        }
    }

    fun onGoogleSignIn(onAuthSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.loginWithGoogle()
            result.fold(
                onSuccess = {
                    _uiState.update { state -> state.copy(isLoading = false, isAuthenticated = true) }
                    onAuthSuccess()
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Error al iniciar con Google"
                        )
                    }
                }
            )
        }
    }

    fun onGuestLogin(onAuthSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.loginAsGuest()
            result.fold(
                onSuccess = {
                    _uiState.update { state -> state.copy(isLoading = false, isAuthenticated = true) }
                    onAuthSuccess()
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Error al ingresar como invitado"
                        )
                    }
                }
            )
        }
    }
}
