package com.closy.data.repository

import com.closy.data.db.ClosetItemDao
import com.closy.data.db.ClosetItemEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ClosetRepository(dao: ClosetItemDao? = null) {
    private val activeDao = dao ?: globalDao
    private val memoryItems = mutableListOf<ClosetItemEntity>()
    private val mutex = Mutex()
    private var nextId = 1L

    suspend fun getItems(userEmail: String): List<ClosetItemEntity> =
        activeDao?.getItems(userEmail) ?: mutex.withLock {
            memoryItems.filter { it.userEmail == userEmail }.sortedByDescending { it.createdAt }
        }

    suspend fun create(item: ClosetItemEntity): ClosetItemEntity {
        val dao = activeDao
        if (dao != null) return item.copy(id = dao.insert(item))
        return mutex.withLock {
            val saved = item.copy(id = nextId++)
            memoryItems += saved
            saved
        }
    }

    suspend fun update(item: ClosetItemEntity) {
        activeDao?.update(item) ?: mutex.withLock {
            val index = memoryItems.indexOfFirst { it.id == item.id && it.userEmail == item.userEmail }
            if (index >= 0) memoryItems[index] = item
        }
    }

    suspend fun delete(item: ClosetItemEntity) {
        activeDao?.delete(item) ?: mutex.withLock { memoryItems.removeAll { it.id == item.id } }
    }

    companion object {
        @Volatile private var globalDao: ClosetItemDao? = null
        fun init(dao: ClosetItemDao) { globalDao = dao }
    }
}
