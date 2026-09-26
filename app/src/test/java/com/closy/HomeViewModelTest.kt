package com.closy

import com.closy.data.db.ClosetItemEntity
import com.closy.data.db.InMemoryClosetGarmentDao
import com.closy.data.repository.AuthRepository
import com.closy.data.repository.ClosetRepository
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
import org.junit.Assert.assertNotNull
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
        val closetRepository = ClosetRepository(garmentDao = InMemoryClosetGarmentDao())
        outfitRepository = OutfitRepository()
        authRepository = AuthRepository()
        authRepository.logout()
        viewModel = HomeViewModel(outfitRepository, authRepository, closetRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun loginOrSignUp(name: String, email: String, pass: String = "123456") {
        val signUpResult = authRepository.signUp(name, email, pass)
        if (signUpResult.isFailure) {
            authRepository.login(email, pass)
        }
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
    fun testJoseGarmentPrePopulationAndMatchedGarmentsCount() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Jose", "jose@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("jose@gmail.com", state.userEmail)
        assertTrue("Jose should have 12-15 pre-populated garments", state.closetGarments.size in 12..15)

        val outfitsWithMatches = state.outfits.filter { it.matchedGarmentsCount > 0 }
        assertTrue("Outfits should match Jose's pre-populated garments", outfitsWithMatches.isNotEmpty())
    }

    @Test
    fun testNewUserAccountDefaultsToEmptyCloset() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Maria", "maria@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("maria@gmail.com", state.userEmail)
        assertTrue("New user accounts should default to empty closet", state.closetGarments.isEmpty())
    }

    @Test
    fun testGenerarTabCombinationGenerator() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Jose", "jose@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        val garment = viewModel.uiState.value.closetGarments.first()
        viewModel.selectGeneratorGarment(garment)
        viewModel.generateOutfitCombination("Trabajo")

        val state = viewModel.uiState.value
        assertNotNull("Generator should produce a recommendation", state.recommendation)
        assertTrue("Recommendation reason should explain the match", state.recommendationReason.contains(garment.name))
        assertTrue("Generated outfits should not be empty", state.generatedCombinationOutfits.isNotEmpty())
    }

    @Test
    fun testGenerarTabTagBasedRecommendationsWithoutBaseGarment() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleStyleTag("Casual")
        viewModel.toggleStyleTag("Urbano")
        viewModel.selectGeneratorGarment(null)

        viewModel.generateOutfitCombination("Diario")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull("Recommendations should be generated without base garment", state.recommendation)
        assertTrue("Generated outfits should contain style recommendations", state.generatedCombinationOutfits.isNotEmpty())
    }

    @Test
    fun testResetSessionState() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Blazer")
        viewModel.onCategorySelected("Casual")
        viewModel.onBottomTabSelected(2)
        viewModel.toggleStyleTag("Formal")

        viewModel.resetSessionState()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertEquals("Todos", state.selectedCategory)
        assertEquals(0, state.selectedBottomTab)
        assertTrue(state.selectedStyleTags.isEmpty())
        assertNull(state.selectedGeneratorGarment)
        assertTrue(state.generatedCombinationOutfits.isEmpty())
    }

    @Test
    fun testLiveProfileStatistics() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Jose", "jose@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(state.closetGarments.size, state.closetGarmentsCount)
        assertEquals(state.savedOutfitCount, state.savedOutfitsCount)
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
            assertEquals("Hombre", outfit.genderPreference)
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
    fun testBottomTabSelectionAndReset() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onBottomTabSelected(1) // Closet
        assertEquals(1, viewModel.uiState.value.selectedBottomTab)

        viewModel.onBottomTabSelected(2) // Generar
        assertEquals(2, viewModel.uiState.value.selectedBottomTab)

        viewModel.onBottomTabSelected(3) // Perfil
        assertEquals(3, viewModel.uiState.value.selectedBottomTab)

        viewModel.resetBottomTab() // Reset to Inicio
        assertEquals(0, viewModel.uiState.value.selectedBottomTab)

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

    @Test
    fun testDeletionConfirmationWorkflowForOutfit() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val targetOutfit = viewModel.uiState.value.outfits.first()
        val initialSavedStatus = targetOutfit.isSaved

        viewModel.requestUnsaveOutfit(targetOutfit)
        assertEquals(targetOutfit, viewModel.uiState.value.pendingDeleteOutfit)

        val deleteState = viewModel.uiState.value.deleteConfirmationState
        assertEquals("¿Eliminar outfit de favoritos?", deleteState?.title)
        assertEquals(
            "¿Estás seguro/a de que deseas eliminar el outfit \"${targetOutfit.title}\" de tus favoritos? Esta acción no se puede deshacer.",
            deleteState?.body
        )
        assertEquals("Eliminar de favoritos", deleteState?.confirmButtonText)

        viewModel.cancelDeletion()
        assertNull(viewModel.uiState.value.pendingDeleteOutfit)
        assertNull(viewModel.uiState.value.deleteConfirmationState)
        val afterCancelOutfit = viewModel.uiState.value.outfits.first { it.id == targetOutfit.id }
        assertEquals(initialSavedStatus, afterCancelOutfit.isSaved)

        viewModel.requestUnsaveOutfit(targetOutfit)
        viewModel.confirmDeletion()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.pendingDeleteOutfit)
        assertNull(viewModel.uiState.value.deleteConfirmationState)
        val afterConfirmOutfit = viewModel.uiState.value.outfits.first { it.id == targetOutfit.id }
        assertEquals(!initialSavedStatus, afterConfirmOutfit.isSaved)
    }

    @Test
    fun testDeletionConfirmationWorkflowForClosetItem() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val testItem = ClosetItemEntity(
            id = 100,
            userEmail = "test@closy.app",
            name = "Chaqueta de cuero",
            category = "Abrigos",
            color = "#111111",
            season = "Invierno"
        )

        viewModel.requestDeleteClosetItem(testItem)
        assertEquals(testItem, viewModel.uiState.value.pendingDeleteClosetItem)

        val deleteState = viewModel.uiState.value.deleteConfirmationState
        assertEquals("¿Eliminar prenda del closet?", deleteState?.title)
        assertEquals(
            "¿Estás seguro/a de que deseas eliminar la prenda \"${testItem.name}\" de tu closet? Esta acción no se puede deshacer.",
            deleteState?.body
        )
        assertEquals("Eliminar prenda", deleteState?.confirmButtonText)

        viewModel.cancelDeletion()
        assertNull(viewModel.uiState.value.pendingDeleteClosetItem)
        assertNull(viewModel.uiState.value.deleteConfirmationState)

        viewModel.requestDeleteClosetItem(testItem)
        viewModel.confirmDeletion()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.pendingDeleteClosetItem)
        assertNull(viewModel.uiState.value.deleteConfirmationState)
    }

    @Test
    fun testGenerarTabToggleFavoriteUpdatesGeneratedOutfits() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.generateOutfitCombination("Diario")
        testDispatcher.scheduler.advanceUntilIdle()

        val initialGenerated = viewModel.uiState.value.generatedCombinationOutfits
        assertTrue(initialGenerated.isNotEmpty())
        val targetOutfit = initialGenerated.first()
        val initialSaved = targetOutfit.isSaved

        viewModel.toggleFavorite(targetOutfit.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedGenerated = viewModel.uiState.value.generatedCombinationOutfits
        val updatedTarget = updatedGenerated.first { it.id == targetOutfit.id }
        assertEquals(!initialSaved, updatedTarget.isSaved)
        assertEquals(!initialSaved, updatedTarget.isFavorite)

        val updatedRecommendation = viewModel.uiState.value.recommendation
        if (updatedRecommendation?.id == targetOutfit.id) {
            assertEquals(!initialSaved, updatedRecommendation.isSaved)
            assertEquals(!initialSaved, updatedRecommendation.isFavorite)
        }
    }

    @Test
    fun testAddNewGarment_persistsInRoomAndUpdatesUiState() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Maria", "maria@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.uiState.value
        assertEquals("maria@gmail.com", initialState.userEmail)
        assertEquals(0, initialState.closetGarmentsCount)

        viewModel.addGarment(
            name = "Chaqueta de Cuero",
            category = "Sacos",
            color = "#111111",
            imageUrl = "https://example.com/jacket.jpg",
            styleTag = "Urbano"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.closetGarmentsCount)
        assertEquals(1, updatedState.closetGarments.size)
        val added = updatedState.closetGarments.first()
        assertEquals("Chaqueta de Cuero", added.name)
        assertEquals("Sacos", added.category)
        assertEquals("#111111", added.color)
        assertEquals("https://example.com/jacket.jpg", added.imageUrl)
        assertEquals("Urbano", added.styleTag)
        assertEquals("maria@gmail.com", added.userEmail)
    }

    @Test
    fun testUpdateGarment_persistsChangesAndRefreshesUiState() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Jose", "jose@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        val initialGarments = viewModel.uiState.value.closetGarments
        val target = initialGarments.first()
        val countBefore = viewModel.uiState.value.closetGarmentsCount

        viewModel.updateGarment(
            garmentId = target.id,
            name = "Camisa Lino Azul Editada",
            category = "Camisas",
            color = "#486FA5",
            imageUrl = "https://example.com/edited.jpg",
            styleTag = "Formal"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedGarments = viewModel.uiState.value.closetGarments
        assertEquals(countBefore, viewModel.uiState.value.closetGarmentsCount)

        val updatedItem = updatedGarments.first { it.id == target.id }
        assertEquals("Camisa Lino Azul Editada", updatedItem.name)
        assertEquals("Camisas", updatedItem.category)
        assertEquals("#486FA5", updatedItem.color)
        assertEquals("https://example.com/edited.jpg", updatedItem.imageUrl)
        assertEquals("Formal", updatedItem.styleTag)
    }

    @Test
    fun testDeleteGarment_triggersDialogAndRemovesFromRoomAndUpdateCount() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Jose", "jose@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        val initialGarments = viewModel.uiState.value.closetGarments
        val initialCount = viewModel.uiState.value.closetGarmentsCount
        assertTrue(initialCount > 0)

        val target = initialGarments.first()

        viewModel.requestDeleteGarment(target)
        val dialogState = viewModel.uiState.value.deleteConfirmationState
        assertNotNull(dialogState)
        assertEquals("¿Eliminar prenda del closet?", dialogState?.title)
        assertEquals("Eliminar prenda", dialogState?.confirmButtonText)
        assertEquals(target, viewModel.uiState.value.pendingDeleteGarment)

        viewModel.confirmDeletion()
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedGarments = viewModel.uiState.value.closetGarments
        val updatedCount = viewModel.uiState.value.closetGarmentsCount
        assertEquals(initialCount - 1, updatedCount)
        assertTrue(updatedGarments.none { it.id == target.id })
        assertNull(viewModel.uiState.value.pendingDeleteGarment)
        assertNull(viewModel.uiState.value.deleteConfirmationState)
    }

    @Test
    fun testClosetCategoryFiltering() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        loginOrSignUp("Jose", "jose@gmail.com", "123456")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadCloset()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onClosetCategorySelected("Camisas")
        assertEquals("Camisas", viewModel.uiState.value.selectedClosetCategory)
        val camisas = viewModel.uiState.value.filteredClosetGarments
        assertTrue(camisas.all { it.category.contains("Camisa", ignoreCase = true) })

        viewModel.onClosetCategorySelected("Tops")
        val tops = viewModel.uiState.value.filteredClosetGarments
        assertTrue(tops.all { it.category.contains("Top", ignoreCase = true) })

        viewModel.onClosetCategorySelected("Sacos")
        val sacos = viewModel.uiState.value.filteredClosetGarments
        assertTrue(sacos.all { it.category.contains("Saco", ignoreCase = true) || it.category.contains("Blazer", ignoreCase = true) || it.category.contains("Chaqueta", ignoreCase = true) })

        viewModel.onClosetCategorySelected("Pantalones")
        val pantalones = viewModel.uiState.value.filteredClosetGarments
        assertTrue(pantalones.all { it.category.contains("Pantal", ignoreCase = true) || it.category.contains("Jean", ignoreCase = true) })

        viewModel.onClosetCategorySelected("Calzado")
        val calzado = viewModel.uiState.value.filteredClosetGarments
        assertTrue(calzado.all { it.category.contains("Calzado", ignoreCase = true) || it.category.contains("Sneaker", ignoreCase = true) || it.category.contains("Mocasin", ignoreCase = true) })

        viewModel.onClosetCategorySelected("Accesorios")
        val accesorios = viewModel.uiState.value.filteredClosetGarments
        assertTrue(accesorios.all { it.category.contains("Accesor", ignoreCase = true) || it.category.contains("Reloj", ignoreCase = true) || it.category.contains("Gafa", ignoreCase = true) })

        viewModel.onClosetCategorySelected("Todos")
        assertEquals(viewModel.uiState.value.closetGarments.size, viewModel.uiState.value.filteredClosetGarments.size)
    }
}
