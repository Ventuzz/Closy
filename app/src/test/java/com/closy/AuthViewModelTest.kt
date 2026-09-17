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
    }

    @Test
    fun testTabSwitching() {
        viewModel.onTabSelected(1)
        assertEquals(1, viewModel.uiState.value.selectedTab)

        viewModel.onTabSelected(0)
        assertEquals(0, viewModel.uiState.value.selectedTab)
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
    fun testSuccessfulRegistrationAndLoginFlow() = runTest {
        var registerSuccessCalled = false

        // Register
        viewModel.onTabSelected(1)
        viewModel.onNameChanged("María García")
        viewModel.onEmailChanged("maria@closy.app")
        viewModel.onPasswordChanged("123456")
        viewModel.onConfirmPasswordChanged("123456")

        viewModel.onSubmit { registerSuccessCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertTrue(registerSuccessCalled)
        assertNull(viewModel.uiState.value.errorMessage)

        // Reset state for Login test
        repository.logout()
        val loginViewModel = AuthViewModel(repository)
        var loginSuccessCalled = false

        loginViewModel.onTabSelected(0)
        loginViewModel.onEmailChanged("maria@closy.app")
        loginViewModel.onPasswordChanged("123456")
        loginViewModel.onSubmit { loginSuccessCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(loginViewModel.uiState.value.isAuthenticated)
        assertTrue(loginSuccessCalled)
        assertNull(loginViewModel.uiState.value.errorMessage)
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
        assertTrue(successCalled)
    }

    @Test
    fun testGuestLoginFlow() = runTest {
        var successCalled = false

        viewModel.onGuestLogin { successCalled = true }

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertTrue(successCalled)
    }
}
