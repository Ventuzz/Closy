package com.closy.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedOutfitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOutfit(savedOutfit: SavedOutfitEntity)

    @Query("DELETE FROM saved_outfits WHERE userEmail = :userEmail AND outfitId = :outfitId")
    suspend fun removeSavedOutfit(userEmail: String, outfitId: String)

    @Query("SELECT outfitId FROM saved_outfits WHERE userEmail = :userEmail")
    suspend fun getSavedOutfitIdsForUser(userEmail: String): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_outfits WHERE userEmail = :userEmail AND outfitId = :outfitId)")
    suspend fun isOutfitSaved(userEmail: String, outfitId: String): Boolean

    @Query("DELETE FROM saved_outfits WHERE LOWER(userEmail) = LOWER(:userEmail)")
    suspend fun deleteAllForUser(userEmail: String)
}
