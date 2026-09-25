package com.closy

import com.closy.data.repository.AuthRepository
import com.closy.data.repository.ThemeRepository
import com.closy.ui.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        ThemeRepository.setDarkMode(false)
    }

    @After
    fun tearDown() {
        ThemeRepository.setDarkMode(false)
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialDarkModeIsFalse() {
        assertFalse(ThemeRepository.isDarkMode.value)
    }

    @Test
    fun testSetDarkMode() {
        ThemeRepository.setDarkMode(true)
        assertTrue(ThemeRepository.isDarkMode.value)

        ThemeRepository.setDarkMode(false)
        assertFalse(ThemeRepository.isDarkMode.value)
    }

    @Test
    fun testToggleDarkMode() {
        assertFalse(ThemeRepository.isDarkMode.value)

        ThemeRepository.toggleDarkMode()
        assertTrue(ThemeRepository.isDarkMode.value)

        ThemeRepository.toggleDarkMode()
        assertFalse(ThemeRepository.isDarkMode.value)
    }

    @Test
    fun testInstanceDelegationSharesState() {
        val repo = ThemeRepository()
        assertFalse(repo.isDarkMode.value)

        repo.setDarkMode(true)
        assertTrue(ThemeRepository.isDarkMode.value)
        assertTrue(repo.isDarkMode.value)

        repo.toggleDarkMode()
        assertFalse(ThemeRepository.isDarkMode.value)
        assertFalse(repo.isDarkMode.value)
    }

    @Test
    fun testLogoutDoesNotResetDarkModeState() {
        ThemeRepository.setDarkMode(true)
        val authViewModel = AuthViewModel(AuthRepository())

        authViewModel.logout()

        assertTrue(ThemeRepository.isDarkMode.value)
    }
}
