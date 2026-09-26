package com.closy.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "closet_items",
    indices = [Index("userEmail")]
)
data class ClosetItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val name: String,
    val category: String,
    val color: String,
    val season: String,
    val notes: String = "",
    val imageUri: String = "",
    val size: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
