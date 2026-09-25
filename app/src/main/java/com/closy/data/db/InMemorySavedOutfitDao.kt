package com.closy.data.db

class InMemorySavedOutfitDao : SavedOutfitDao {
    private val savedOutfits = mutableListOf<SavedOutfitEntity>()

    override suspend fun saveOutfit(savedOutfit: SavedOutfitEntity) {
        savedOutfits.removeAll { it.userEmail == savedOutfit.userEmail && it.outfitId == savedOutfit.outfitId }
        savedOutfits.add(savedOutfit)
    }

    override suspend fun removeSavedOutfit(userEmail: String, outfitId: String) {
        savedOutfits.removeAll { it.userEmail == userEmail && it.outfitId == outfitId }
    }

    override suspend fun getSavedOutfitIdsForUser(userEmail: String): List<String> {
        return savedOutfits.filter { it.userEmail == userEmail }.map { it.outfitId }
    }

    override suspend fun isOutfitSaved(userEmail: String, outfitId: String): Boolean {
        return savedOutfits.any { it.userEmail == userEmail && it.outfitId == outfitId }
    }

    override suspend fun deleteAllForUser(userEmail: String) {
        savedOutfits.removeAll { it.userEmail.equals(userEmail, ignoreCase = true) }
    }
}
