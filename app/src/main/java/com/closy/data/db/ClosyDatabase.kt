package com.closy.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, SavedOutfitEntity::class], version = 2, exportSchema = false)
abstract class ClosyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun savedOutfitDao(): SavedOutfitDao

    companion object {
        @Volatile
        private var INSTANCE: ClosyDatabase? = null

        fun getDatabase(context: Context): ClosyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClosyDatabase::class.java,
                    "closy_database",
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
