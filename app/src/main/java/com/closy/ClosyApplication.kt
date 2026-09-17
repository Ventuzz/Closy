package com.closy

import android.app.Application
import com.closy.data.db.ClosyDatabase
import com.closy.data.repository.AuthRepository

class ClosyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val database = ClosyDatabase.getDatabase(this)
        AuthRepository.init(database.userDao())
    }
}
