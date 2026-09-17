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

    private var pendingAuthSuccessCallback: ((hasSavedPreference: Boolean) -> Unit)? = null

    fun clearForm() {
        _uiState.update {
            it.copy(
                email = "",
                password = "",
                name = "",
                confirmPassword = "",
                errorMessage = null
            )
        }
    }

    fun dismissSuccessMessage(onAuthSuccess: ((hasSavedPreference: Boolean) -> Unit)? = null) {
        val hasPref = hasSavedGenderPreference()
        _uiState.update { it.copy(successMessage = null) }
        val callback = onAuthSuccess ?: pendingAuthSuccessCallback
        callback?.invoke(hasPref)
        pendingAuthSuccessCallback = null
    }

    fun logout() {
        repository.logout()
        clearForm()
        _uiState.update {
            it.copy(
                selectedTab = 0,
                isAuthenticated = false
            )
        }
    }

    fun hasSavedGenderPreference(): Boolean {
        return repository.hasSavedGenderPreference()
    }

    fun onTabSelected(index: Int) {
        clearForm()
        _uiState.update { it.copy(selectedTab = index) }
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
        onSubmitWithPreference { _ -> onAuthSuccess() }
    }

    fun onSubmitWithPreference(onAuthSuccess: (hasSavedPreference: Boolean) -> Unit) {
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
            val isSignUp = currentState.selectedTab == 1
            val result = if (isSignUp) {
                repository.signUp(currentState.name, currentState.email, currentState.password)
            } else {
                repository.login(currentState.email, currentState.password)
            }

            result.fold(
                onSuccess = {
                    pendingAuthSuccessCallback = onAuthSuccess
                    val msg = if (isSignUp) {
                        "Bienvenido ${currentState.name.trim()}, gracias por usar Closy"
                    } else {
                        "Inicio de sesión exitoso"
                    }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            successMessage = msg
                        )
                    }
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
        onGoogleSignInWithPreference { _ -> onAuthSuccess() }
    }

    fun onGoogleSignInWithPreference(onAuthSuccess: (hasSavedPreference: Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.loginWithGoogle()
            result.fold(
                onSuccess = {
                    pendingAuthSuccessCallback = onAuthSuccess
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            successMessage = "Inicio de sesión exitoso"
                        )
                    }
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
        onGuestLoginWithPreference { _ -> onAuthSuccess() }
    }

    fun onGuestLoginWithPreference(onAuthSuccess: (hasSavedPreference: Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.loginAsGuest()
            result.fold(
                onSuccess = {
                    pendingAuthSuccessCallback = onAuthSuccess
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            successMessage = "Inicio de sesión exitoso"
                        )
                    }
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
