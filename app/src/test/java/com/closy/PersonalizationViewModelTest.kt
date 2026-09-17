package com.closy

import com.closy.ui.personalization.PersonalizationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalizationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val viewModel = PersonalizationViewModel()
        val state = viewModel.uiState.value

        assertNull(state.selectedGender)
        assertNull(state.errorMessage)
    }

    @Test
    fun testSelectGenderFlow() = runTest {
        val viewModel = PersonalizationViewModel()
        var onCompleteCalled = false

        viewModel.selectGender("Mujer") {
            onCompleteCalled = true
        }

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Mujer", viewModel.uiState.value.selectedGender)
        assertTrue(viewModel.uiState.value.isSaved)
        assertTrue(onCompleteCalled)
    }
}
