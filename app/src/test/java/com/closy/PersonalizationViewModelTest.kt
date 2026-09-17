package com.closy

import com.closy.data.db.InMemoryUserDao
import com.closy.data.repository.AuthRepository
import com.closy.ui.personalization.PersonalizationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalizationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = AuthRepository(InMemoryUserDao())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateWithoutSavedPreference() {
        val viewModel = PersonalizationViewModel(repository)
        val state = viewModel.uiState.value

        assertNull(state.selectedGender)
        assertNull(state.errorMessage)
    }

    @Test
    fun testPreselectSavedGenderPreference() = runTest {
        // User has a saved gender preference in AuthRepository
        repository.signUp("Carlos", "carlos@closy.app", "123456")
        repository.saveGenderPreference("Hombre")

        val viewModel = PersonalizationViewModel(repository)
        assertEquals("Hombre", viewModel.uiState.value.selectedGender)
    }

    @Test
    fun testSelectGenderFlow() = runTest {
        val viewModel = PersonalizationViewModel(repository)
        var onCompleteCalled = false

        viewModel.selectGender("Mujer") {
            onCompleteCalled = true
        }

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Mujer", viewModel.uiState.value.selectedGender)
        assertTrue(viewModel.uiState.value.isSaved)
        assertTrue(onCompleteCalled)
    }

    @Test
    fun testResetGenderSelection() = runTest {
        val viewModel = PersonalizationViewModel(repository)
        viewModel.selectGender("Hombre") {}
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Hombre", viewModel.uiState.value.selectedGender)

        viewModel.resetGenderSelection()
        assertNull(viewModel.uiState.value.selectedGender)
        assertFalse(viewModel.uiState.value.isSaved)
    }
}
