package com.closy.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "closet_garments",
    indices = [Index("userEmail")]
)
data class ClosetGarmentEntity(
    @PrimaryKey val id: String,
    val userEmail: String,
    val name: String,
    val category: String,
    val color: String,
    val imageUrl: String,
    val styleTag: String
)
