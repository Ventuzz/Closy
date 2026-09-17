package com.closy.ui.auth

data class AuthState(
    val selectedTab: Int = 0, // 0 = Iniciar sesión, 1 = Crear cuenta
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)
