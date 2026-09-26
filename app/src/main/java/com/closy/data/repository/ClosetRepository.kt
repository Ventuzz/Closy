package com.closy.data.repository

import com.closy.data.db.ClosetGarmentDao
import com.closy.data.db.ClosetGarmentEntity
import com.closy.data.db.ClosetItemDao
import com.closy.data.db.ClosetItemEntity
import com.closy.data.db.InMemoryClosetGarmentDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ClosetRepository(
    garmentDao: ClosetGarmentDao? = null,
    itemDao: ClosetItemDao? = null
) {
    private val activeGarmentDao: ClosetGarmentDao = garmentDao
        ?: globalGarmentDao
        ?: InMemoryClosetGarmentDao().also { globalGarmentDao = it }

    private val activeItemDao: ClosetItemDao? = itemDao ?: globalItemDao

    private val memoryItems = mutableListOf<ClosetItemEntity>()
    private val mutex = Mutex()
    private var nextId = 1L

    suspend fun getGarmentsForUser(userEmail: String): List<ClosetGarmentEntity> {
        val count = activeGarmentDao.getGarmentCountForUser(userEmail)
        if (count == 0) {
            if (userEmail.equals("jose@gmail.com", ignoreCase = true)) {
                val seedList = getJoseSeedGarments(userEmail)
                activeGarmentDao.insertGarments(seedList)
                return seedList
            } else {
                return emptyList()
            }
        }
        return activeGarmentDao.getGarmentsForUser(userEmail)
    }

    suspend fun addGarment(garment: ClosetGarmentEntity) {
        activeGarmentDao.insertGarment(garment)
    }

    suspend fun updateGarment(garment: ClosetGarmentEntity) {
        activeGarmentDao.updateGarment(garment)
    }

    suspend fun insertGarment(garment: ClosetGarmentEntity) {
        addGarment(garment)
    }

    suspend fun insertGarments(garments: List<ClosetGarmentEntity>) {
        activeGarmentDao.insertGarments(garments)
    }

    suspend fun deleteGarment(garment: ClosetGarmentEntity) {
        activeGarmentDao.deleteGarment(garment)
    }

    suspend fun deleteGarment(garmentId: String, userEmail: String? = null) {
        activeGarmentDao.deleteGarmentById(garmentId)
    }

    suspend fun getGarmentCountForUser(userEmail: String): Int {
        val garments = getGarmentsForUser(userEmail)
        return garments.size
    }

    // --- Legacy ClosetItemEntity API compatibility ---

    suspend fun getItems(userEmail: String): List<ClosetItemEntity> {
        val itemDao = activeItemDao
        if (itemDao != null) {
            return itemDao.getItems(userEmail)
        }
        val garments = getGarmentsForUser(userEmail)
        if (garments.isNotEmpty()) {
            return garments.mapIndexed { index, g ->
                ClosetItemEntity(
                    id = (index + 1).toLong(),
                    userEmail = g.userEmail,
                    name = g.name,
                    category = g.category,
                    color = g.color,
                    season = g.styleTag,
                    imageUri = g.imageUrl
                )
            }
        }
        return mutex.withLock {
            memoryItems.filter { it.userEmail.equals(userEmail, ignoreCase = true) }
                .sortedByDescending { it.createdAt }
        }
    }

    suspend fun create(item: ClosetItemEntity): ClosetItemEntity {
        val itemDao = activeItemDao
        val garmentEntity = ClosetGarmentEntity(
            id = if (item.id != 0L) item.id.toString() else "garment_${System.currentTimeMillis()}_${(1000..9999).random()}",
            userEmail = item.userEmail,
            name = item.name,
            category = item.category,
            color = item.color,
            imageUrl = item.imageUri,
            styleTag = item.season.ifBlank { "General" }
        )
        insertGarment(garmentEntity)

        if (itemDao != null) {
            val generatedId = itemDao.insert(item)
            return item.copy(id = generatedId)
        }

        return mutex.withLock {
            val saved = item.copy(id = nextId++)
            memoryItems += saved
            saved
        }
    }

    suspend fun update(item: ClosetItemEntity) {
        activeItemDao?.update(item)
        val garmentEntity = ClosetGarmentEntity(
            id = item.id.toString(),
            userEmail = item.userEmail,
            name = item.name,
            category = item.category,
            color = item.color,
            imageUrl = item.imageUri,
            styleTag = item.season.ifBlank { "General" }
        )
        insertGarment(garmentEntity)

        mutex.withLock {
            val index = memoryItems.indexOfFirst { it.id == item.id && it.userEmail == item.userEmail }
            if (index >= 0) memoryItems[index] = item
        }
    }

    suspend fun delete(item: ClosetItemEntity) {
        activeItemDao?.delete(item)
        val garments = getGarmentsForUser(item.userEmail)
        val target = garments.find { it.id == item.id.toString() || it.name.equals(item.name, ignoreCase = true) }
        if (target != null) {
            deleteGarment(target)
        }
        mutex.withLock { memoryItems.removeAll { it.id == item.id } }
    }

    companion object {
        @Volatile private var globalGarmentDao: ClosetGarmentDao? = null
        @Volatile private var globalItemDao: ClosetItemDao? = null

        fun init(garmentDao: ClosetGarmentDao, itemDao: ClosetItemDao? = null) {
            globalGarmentDao = garmentDao
            globalItemDao = itemDao
        }

        fun getGlobalGarmentDao(): ClosetGarmentDao? = globalGarmentDao

        fun getJoseSeedGarments(userEmail: String): List<ClosetGarmentEntity> {
            val email = if (userEmail.isBlank()) "jose@gmail.com" else userEmail
            return listOf(
                ClosetGarmentEntity("jose_1", email, "Camisa Lino Blanca", "Camisas", "#FFFFFF", "https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=600", "Casual"),
                ClosetGarmentEntity("jose_2", email, "Top Básico Negro", "Tops", "#111111", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600", "Básico"),
                ClosetGarmentEntity("jose_3", email, "Blazer Oversize Beige", "Sacos/Blazers", "#DCCFB5", "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600", "Formal"),
                ClosetGarmentEntity("jose_4", email, "Pantalón Wide Leg Negro", "Pantalones", "#111111", "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=600", "Elegante"),
                ClosetGarmentEntity("jose_5", email, "Jeans Levi's 501", "Jeans", "#2886B8", "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600", "Urbano"),
                ClosetGarmentEntity("jose_6", email, "Sneakers Retro Blancos", "Calzado/Sneakers", "#FFFFFF", "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=600", "Urbano"),
                ClosetGarmentEntity("jose_7", email, "Mocasines Cuero Café", "Calzado/Sneakers", "#805133", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=600", "Formal"),
                ClosetGarmentEntity("jose_8", email, "Saco Marino Slim", "Sacos/Blazers", "#486FA5", "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600", "Formal"),
                ClosetGarmentEntity("jose_9", email, "Camisa Oxford Azul", "Camisas", "#486FA5", "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600", "Casual"),
                ClosetGarmentEntity("jose_10", email, "Pantalón Chino Beige", "Pantalones", "#DCCFB5", "https://images.unsplash.com/photo-1562183241-b937e95585b6?w=600", "Casual"),
                ClosetGarmentEntity("jose_11", email, "Chaqueta Cuero Negra", "Sacos/Blazers", "#111111", "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600", "Urbano"),
                ClosetGarmentEntity("jose_12", email, "Reloj Cuero Minimalista", "Accesorios", "#805133", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600", "Accesorios"),
                ClosetGarmentEntity("jose_13", email, "Gafas de Sol Clásicas", "Accesorios", "#111111", "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=600", "Verano")
            )
        }
    }
}
