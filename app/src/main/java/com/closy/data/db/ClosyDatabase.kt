package com.closy.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserEntity::class, SavedOutfitEntity::class, ClosetItemEntity::class],
    version = 4,
    exportSchema = false
)
abstract class ClosyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun savedOutfitDao(): SavedOutfitDao
    abstract fun closetItemDao(): ClosetItemDao

    companion object {
        @Volatile
        private var INSTANCE: ClosyDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `closet_items` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userEmail` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `color` TEXT NOT NULL,
                        `season` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )""".trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_closet_items_userEmail` ON `closet_items` (`userEmail`)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `closet_items` ADD COLUMN `imageUri` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `closet_items` ADD COLUMN `size` TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): ClosyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClosyDatabase::class.java,
                    "closy_database",
                ).addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
