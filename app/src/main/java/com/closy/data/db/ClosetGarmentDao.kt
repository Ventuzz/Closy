package com.closy.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClosetGarmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGarment(garment: ClosetGarmentEntity)

    @Update
    suspend fun updateGarment(garment: ClosetGarmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGarments(garments: List<ClosetGarmentEntity>)

    @Query("SELECT * FROM closet_garments WHERE LOWER(userEmail) = LOWER(:userEmail)")
    suspend fun getGarmentsForUser(userEmail: String): List<ClosetGarmentEntity>

    @Delete
    suspend fun deleteGarment(garment: ClosetGarmentEntity)

    @Query("DELETE FROM closet_garments WHERE id = :garmentId")
    suspend fun deleteGarmentById(garmentId: String)

    @Query("SELECT COUNT(*) FROM closet_garments WHERE LOWER(userEmail) = LOWER(:userEmail)")
    suspend fun getGarmentCountForUser(userEmail: String): Int

    @Query("DELETE FROM closet_garments WHERE LOWER(userEmail) = LOWER(:userEmail)")
    suspend fun deleteAllForUser(userEmail: String)
}
