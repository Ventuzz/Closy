package com.closy.data.db

import androidx.room.Entity

@Entity(
    tableName = "saved_outfits",
    primaryKeys = ["userEmail", "outfitId"]
)
data class SavedOutfitEntity(
    val userEmail: String,
    val outfitId: String,
    val savedAt: Long = System.currentTimeMillis()
)
