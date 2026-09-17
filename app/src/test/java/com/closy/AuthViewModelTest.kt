package com.closy

import com.closy.data.db.InMemoryUserDao
import com.closy.data.repository.AuthRepository
import com.closy.ui.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = AuthRepository(InMemoryUserDao())
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val state = viewModel.uiState.value

        assertEquals(0, state.selectedTab)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isPasswordVisible)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNull(state.successMessage)
    }

    @Test
    fun testTabSwitchingClearsForm() {
        viewModel.onEmailChanged("test@closy.app")
        viewModel.onPasswordChanged("123456")
        viewModel.onTabSelected(1)

        val state = viewModel.uiState.value
        assertEquals(1, state.selectedTab)
        assertEquals("", state.email)
        assertEquals("", state.password)

        viewModel.onNameChanged("Carlos")
        viewModel.onTabSelected(0)
        assertEquals(0, viewModel.uiState.value.selectedTab)
        assertEquals("", viewModel.uiState.value.name)
    }

    @Test
    fun testClearFormAndLogout() {
        viewModel.onEmailChanged("test@closy.app")
        viewModel.onPasswordChanged("123456")
        viewModel.onTabSelected(1)
        viewModel.clearForm()

        assertEquals("", viewModel.uiState.value.email)
        assertEquals("", viewModel.uiState.value.password)

        viewModel.logout()
        assertFalse(viewModel.uiState.value.isAuthenticated)
        assertEquals(0, viewModel.uiState.value.selectedTab)
        assertNull(repository.currentUser.value)
    }

    @Test
    fun testPasswordVisibilityToggle() {
        assertFalse(viewModel.uiState.value.isPasswordVisible)

        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.isPasswordVisible)

        viewModel.togglePasswordVisibility()
        assertFalse(viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun testLoginValidationError() {
        var successCalled = false

        viewModel.onSubmit { successCalled = true }

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(successCalled)
    }

    @Test
    fun testInvalidCredentialLoginRejection() = runTest {
        var successCalled = false

        viewModel.onEmailChanged("desconocido@closy.app")
        viewModel.onPasswordChanged("123456")
        viewModel.onSubmit { successCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAuthenticated)
        assertFalse(successCalled)
        assertEquals(
            "Usuario no registrado o contraseña incorrecta.",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun testSuccessfulRegistrationAndLoginBypassFlow() = runTest {
        var registerHasPreference: Boolean? = null

        // 1. New Registration -> Should NOT have saved preference initially
        viewModel.onTabSelected(1)
        viewModel.onNameChanged("María García")
        viewModel.onEmailChanged("maria@closy.app")
        viewModel.onPasswordChanged("123456")
        viewModel.onConfirmPasswordChanged("123456")

        viewModel.onSubmitWithPreference { hasPref ->
            registerHasPreference = hasPref
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertEquals("Bienvenido María García, gracias por usar Closy", viewModel.uiState.value.successMessage)

        viewModel.dismissSuccessMessage()
        assertEquals(false, registerHasPreference)
        assertNull(viewModel.uiState.value.successMessage)

        // User saves preference
        repository.saveGenderPreference("Mujer")

        // 2. Logout and Login -> Existing user WITH saved preference
        viewModel.logout()
        val loginViewModel = AuthViewModel(repository)
        var loginHasPreference: Boolean? = null

        loginViewModel.onTabSelected(0)
        loginViewModel.onEmailChanged("maria@closy.app")
        loginViewModel.onPasswordChanged("123456")
        loginViewModel.onSubmitWithPreference { hasPref ->
            loginHasPreference = hasPref
        }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(loginViewModel.uiState.value.isAuthenticated)
        assertEquals("Inicio de sesión exitoso", loginViewModel.uiState.value.successMessage)

        loginViewModel.dismissSuccessMessage()
        assertEquals(true, loginHasPreference)
        assertNull(loginViewModel.uiState.value.successMessage)
    }

    @Test
    fun testDuplicateEmailRegistrationRejection() = runTest {
        var firstSuccess = false
        viewModel.onTabSelected(1)
        viewModel.onNameChanged("María García")
        viewModel.onEmailChanged("maria@closy.app")
        viewModel.onPasswordChanged("123456")
        viewModel.onConfirmPasswordChanged("123456")
        viewModel.onSubmit { firstSuccess = true }
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.dismissSuccessMessage()
        assertTrue(firstSuccess)

        // Try registering again with same email
        val duplicateViewModel = AuthViewModel(repository)
        var secondSuccess = false
        duplicateViewModel.onTabSelected(1)
        duplicateViewModel.onNameChanged("María Copia")
        duplicateViewModel.onEmailChanged("maria@closy.app")
        duplicateViewModel.onPasswordChanged("123456")
        duplicateViewModel.onConfirmPasswordChanged("123456")
        duplicateViewModel.onSubmit { secondSuccess = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(secondSuccess)
        assertFalse(duplicateViewModel.uiState.value.isAuthenticated)
        assertEquals(
            "El correo electrónico ya está registrado. Por favor inicia sesión.",
            duplicateViewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun testRegisterPasswordMismatchError() {
        var successCalled = false

        viewModel.onTabSelected(1)
        viewModel.onNameChanged("María García")
        viewModel.onEmailChanged("maria@closy.app")
        viewModel.onPasswordChanged("123456")
        viewModel.onConfirmPasswordChanged("654321")

        viewModel.onSubmit { successCalled = true }

        assertEquals("Las contraseñas no coinciden", viewModel.uiState.value.errorMessage)
        assertFalse(successCalled)
    }

    @Test
    fun testGoogleSignInFlow() = runTest {
        var successCalled = false

        viewModel.onGoogleSignIn { successCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertEquals("Inicio de sesión exitoso", viewModel.uiState.value.successMessage)

        viewModel.dismissSuccessMessage()
        assertTrue(successCalled)
    }

    @Test
    fun testGuestLoginFlow() = runTest {
        var successCalled = false

        viewModel.onGuestLogin { successCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertEquals("Inicio de sesión exitoso", viewModel.uiState.value.successMessage)

        viewModel.dismissSuccessMessage()
        assertTrue(successCalled)
    }
}
