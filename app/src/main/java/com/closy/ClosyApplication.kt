package com.closy

import android.app.Application
import com.closy.data.db.ClosyDatabase
import com.closy.data.repository.AuthRepository
import com.closy.data.repository.ClosetRepository
import com.closy.data.repository.OutfitRepository

class ClosyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val database = ClosyDatabase.getDatabase(this)
        AuthRepository.init(database.userDao(), database.savedOutfitDao(), database.closetGarmentDao())
        OutfitRepository.init(database.savedOutfitDao())
        ClosetRepository.init(database.closetGarmentDao(), database.closetItemDao())
    }
}
