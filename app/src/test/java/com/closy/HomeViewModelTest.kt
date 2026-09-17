package com.closy

import com.closy.data.repository.AuthRepository
import com.closy.data.repository.OutfitRepository
import com.closy.ui.home.HomeViewModel
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        outfitRepository = OutfitRepository()
        authRepository = AuthRepository()
        viewModel = HomeViewModel(outfitRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        assertEquals("", state.searchQuery)
        assertEquals("Todos", state.selectedCategory)
        assertEquals("Mujer", state.activeGenderPreference)
        assertEquals(0, state.selectedSegmentTab)
        assertEquals(0, state.selectedBottomTab)
        assertNull(state.selectedOutfitForDetail)
        assertTrue(state.outfits.isNotEmpty())
    }

    @Test
    fun testSearchQueryChange() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onSearchQueryChange("Blazer")

        val state = viewModel.uiState.value
        assertEquals("Blazer", state.searchQuery)
        assertTrue(state.outfits.isNotEmpty())
        state.outfits.forEach { outfit ->
            val matchesTitle = outfit.title.contains("Blazer", ignoreCase = true)
            val matchesGarment = outfit.garments.any { it.name.contains("Blazer", ignoreCase = true) }
            val matchesSummary = outfit.garmentSummary.contains("Blazer", ignoreCase = true)
            assertTrue(matchesTitle || matchesGarment || matchesSummary)
        }
    }

    @Test
    fun testCategorySelection() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onCategorySelected("Urbano")

        val state = viewModel.uiState.value
        assertEquals("Urbano", state.selectedCategory)
        assertTrue(state.outfits.isNotEmpty())
        state.outfits.forEach { outfit ->
            assertEquals("Urbano", outfit.styleCategory)
        }
    }

    @Test
    fun testGenderPreferenceSelection() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onGenderPreferenceSelected("Hombre")

        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals("Hombre", state.activeGenderPreference)
        assertTrue(state.outfits.isNotEmpty())
        state.outfits.forEach { outfit ->
            assertTrue(
                outfit.genderPreference.equals("Hombre", ignoreCase = true) ||
                        outfit.genderPreference.equals("Sin género", ignoreCase = true)
            )
        }
    }

    @Test
    fun testSegmentTabSelectionParaTiVsGuardados() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        // Switch to "Guardados" tab (index 1)
        viewModel.onSegmentTabSelected(1)
        testDispatcher.scheduler.advanceUntilIdle()

        var state = viewModel.uiState.value
        assertEquals(1, state.selectedSegmentTab)
        assertTrue(state.outfits.all { it.isSaved })

        // Switch back to "Para Ti" tab (index 0)
        viewModel.onSegmentTabSelected(0)
        testDispatcher.scheduler.advanceUntilIdle()

        state = viewModel.uiState.value
        assertEquals(0, state.selectedSegmentTab)
        assertTrue(state.outfits.isNotEmpty())
    }

    @Test
    fun testBottomTabSelection() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onBottomTabSelected(1) // Closet
        assertEquals(1, viewModel.uiState.value.selectedBottomTab)

        viewModel.onBottomTabSelected(2) // Generar
        assertEquals(2, viewModel.uiState.value.selectedBottomTab)

        viewModel.onBottomTabSelected(3) // Perfil
        assertEquals(3, viewModel.uiState.value.selectedBottomTab)

        viewModel.onBottomTabSelected(0) // Inicio
        assertEquals(0, viewModel.uiState.value.selectedBottomTab)
    }

    @Test
    fun testOutfitNewProperties() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state.outfits.isNotEmpty())

        val firstOutfit = state.outfits.first()
        assertTrue(firstOutfit.pinterestHandle.startsWith("@"))
        assertTrue(firstOutfit.garmentSummary.isNotEmpty())
        assertTrue(firstOutfit.hashtags.isNotEmpty())
    }

    @Test
    fun testToggleFavorite() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val initialOutfit = viewModel.uiState.value.outfits.first()
        val initialSavedStatus = initialOutfit.isSaved

        viewModel.toggleFavorite(initialOutfit.id)

        testDispatcher.scheduler.advanceUntilIdle()
        val updatedOutfit = viewModel.uiState.value.outfits.first { it.id == initialOutfit.id }
        assertEquals(!initialSavedStatus, updatedOutfit.isSaved)
    }

    @Test
    fun testOutfitDetailSelection() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val targetOutfit = viewModel.uiState.value.outfits.first()

        viewModel.selectOutfitForDetail(targetOutfit)
        assertEquals(targetOutfit.id, viewModel.uiState.value.selectedOutfitForDetail?.id)

        viewModel.selectOutfitForDetail(null)
        assertNull(viewModel.uiState.value.selectedOutfitForDetail)
    }
}
