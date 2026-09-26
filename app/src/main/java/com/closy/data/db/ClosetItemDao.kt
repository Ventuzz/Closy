package com.closy.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClosetItemDao {
    @Query("SELECT * FROM closet_items WHERE userEmail = :userEmail ORDER BY createdAt DESC")
    suspend fun getItems(userEmail: String): List<ClosetItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClosetItemEntity): Long

    @Update
    suspend fun update(item: ClosetItemEntity)

    @Delete
    suspend fun delete(item: ClosetItemEntity)
}
