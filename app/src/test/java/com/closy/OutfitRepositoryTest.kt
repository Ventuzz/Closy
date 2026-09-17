package com.closy

import com.closy.data.repository.OutfitRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OutfitRepositoryTest {

    private lateinit var repository: OutfitRepository

    @Before
    fun setUp() {
        repository = OutfitRepository()
    }

    @Test
    fun testGetOutfitsFilteredByMujerGender() {
        val outfits = repository.getOutfits(genderPreference = "Mujer")
        assertTrue(outfits.isNotEmpty())
        outfits.forEach { outfit ->
            assertTrue(
                outfit.genderPreference.equals("Mujer", ignoreCase = true) ||
                        outfit.genderPreference.equals("Sin género", ignoreCase = true)
            )
        }
    }

    @Test
    fun testGetOutfitsFilteredByHombreGender() {
        val outfits = repository.getOutfits(genderPreference = "Hombre")
        assertTrue(outfits.isNotEmpty())
        outfits.forEach { outfit ->
            assertTrue(
                outfit.genderPreference.equals("Hombre", ignoreCase = true) ||
                        outfit.genderPreference.equals("Sin género", ignoreCase = true)
            )
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
    fun testGetOutfitsFilteredBySavedOnly() {
        val savedOutfits = repository.getOutfits(genderPreference = "Todos", savedOnly = true)
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
}
