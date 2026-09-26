package com.closy

import com.closy.data.db.InMemorySavedOutfitDao
import com.closy.data.repository.OutfitRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OutfitRepositoryTest {

    private lateinit var repository: OutfitRepository
    private lateinit var savedOutfitDao: InMemorySavedOutfitDao

    @Before
    fun setUp() {
        savedOutfitDao = InMemorySavedOutfitDao()
        repository = OutfitRepository(savedOutfitDao)
    }

    @Test
    fun testGetOutfitsFilteredByMujerGenderStrict() {
        val outfits = repository.getOutfits(genderPreference = "Mujer")
        assertTrue(outfits.isNotEmpty())
        outfits.forEach { outfit ->
            assertEquals("Mujer", outfit.genderPreference)
        }
    }

    @Test
    fun testGetOutfitsFilteredByHombreGenderStrict() {
        val outfits = repository.getOutfits(genderPreference = "Hombre")
        assertTrue(outfits.isNotEmpty())
        outfits.forEach { outfit ->
            assertEquals("Hombre", outfit.genderPreference)
        }
    }

    @Test
    fun testGetOutfitsFilteredBySinGeneroStrict() {
        val outfits = repository.getOutfits(genderPreference = "Sin género")
        assertTrue(outfits.isNotEmpty())
        outfits.forEach { outfit ->
            assertEquals("Sin género", outfit.genderPreference)
        }
    }

    @Test
    fun testGetOutfitsFilteredByCategory() {
        val casualOutfits = repository.getOutfits(genderPreference = "Mujer", categoryFilter = "Casual")
        assertTrue(casualOutfits.isNotEmpty())
        casualOutfits.forEach { outfit ->
            assertEquals("Casual", outfit.styleCategory)
        }
    }

    @Test
    fun testGetOutfitsFilteredBySearchQuery() {
        val blazers = repository.getOutfits(genderPreference = "Mujer", searchQuery = "Blazer")
        assertTrue(blazers.isNotEmpty())
        blazers.forEach { outfit ->
            val matchesTitle = outfit.title.contains("Blazer", ignoreCase = true)
            val matchesGarment = outfit.garments.any { it.name.contains("Blazer", ignoreCase = true) }
            val matchesSummary = outfit.garmentSummary.contains("Blazer", ignoreCase = true)
            assertTrue(matchesTitle || matchesGarment || matchesSummary)
        }
    }

    @Test
    fun testGetOutfitsFilteredBySavedOnly() = runBlocking {
        val userEmail = repository.getActiveUserEmail()
        repository.saveOutfit(userEmail, "outfit_1")
        val savedOutfits = repository.getOutfits(genderPreference = "Todos", savedOnly = true, userEmail = userEmail)
        assertTrue(savedOutfits.isNotEmpty())
        savedOutfits.forEach { outfit ->
            assertTrue(outfit.isSaved)
        }
    }

    @Test
    fun testNewOutfitProperties() {
        val outfits = repository.getOutfits(genderPreference = "Todos")
        assertTrue(outfits.isNotEmpty())
        outfits.forEach { outfit ->
            assertTrue(outfit.pinterestHandle.startsWith("@"))
            assertTrue(outfit.garmentSummary.isNotEmpty())
            assertTrue(outfit.hashtags.isNotEmpty())
            assertTrue(outfit.garmentThumbnails.isNotEmpty())
        }
    }

    @Test
    fun testToggleFavorite() {
        val testOutfitId = "outfit_2"
        val initialOutfits = repository.getOutfits(genderPreference = "Mujer")
        val initialOutfit = initialOutfits.find { it.id == testOutfitId }
        val initialSaved = initialOutfit?.isSaved ?: false

        repository.toggleFavorite(testOutfitId)

        val updatedOutfits = repository.getOutfits(genderPreference = "Mujer")
        val updatedOutfit = updatedOutfits.find { it.id == testOutfitId }
        assertEquals(!initialSaved, updatedOutfit?.isSaved)

        repository.toggleFavorite(testOutfitId)
        val revertedOutfits = repository.getOutfits(genderPreference = "Mujer")
        val revertedOutfit = revertedOutfits.find { it.id == testOutfitId }
        assertEquals(initialSaved, revertedOutfit?.isSaved)
    }

    @Test
    fun testPerUserSavedOutfitsIsolation() = runBlocking {
        val user1 = "user1@closy.app"
        val user2 = "user2@closy.app"

        // Save outfit_2 for user1
        repository.saveOutfit(user1, "outfit_2")

        assertTrue(repository.isOutfitSaved(user1, "outfit_2"))
        assertFalse(repository.isOutfitSaved(user2, "outfit_2"))

        // user1 guardados contains outfit_2
        val user1Outfits = repository.getOutfits(genderPreference = "Todos", savedOnly = true, userEmail = user1)
        assertTrue(user1Outfits.any { it.id == "outfit_2" })

        // user2 guardados does NOT contain outfit_2
        val user2Outfits = repository.getOutfits(genderPreference = "Todos", savedOnly = true, userEmail = user2)
        assertFalse(user2Outfits.any { it.id == "outfit_2" })

        // Remove outfit_2 for user1
        repository.removeSavedOutfit(user1, "outfit_2")
        assertFalse(repository.isOutfitSaved(user1, "outfit_2"))
    }

    @Test
    fun testSavedOutfitsRemainVisibleWhenGenderChanges() = runBlocking {
        val user = "gender-change@closy.app"
        repository.saveOutfit(user, "outfit_7") // outfit de Hombre

        val savedWhileBrowsingMujer = repository.getOutfits(
            genderPreference = "Mujer",
            savedOnly = true,
            userEmail = user,
            ignoreGenderForSaved = true
        )

        assertTrue(savedWhileBrowsingMujer.any { it.id == "outfit_7" })
    }

    @Test
    fun testGuestAccountInitialSavedOutfitsIsEmpty() = runBlocking {
        val guestEmail = "guest@closy.com"
        val savedOutfits = repository.getOutfits(genderPreference = "Todos", savedOnly = true, userEmail = guestEmail)
        assertTrue(savedOutfits.isEmpty())
        assertFalse(repository.isOutfitSaved(guestEmail, "outfit_1"))
        assertTrue(repository.favoriteOutfitIds.value.isEmpty())
    }

    @Test
    fun testInvitadoAccountInitialSavedOutfitsIsEmpty() = runBlocking {
        val guestEmail = "invitado@closy.app"
        val savedOutfits = repository.getOutfits(genderPreference = "Todos", savedOnly = true, userEmail = guestEmail)
        assertTrue(savedOutfits.isEmpty())
        assertFalse(repository.isOutfitSaved(guestEmail, "outfit_1"))
    }
}
