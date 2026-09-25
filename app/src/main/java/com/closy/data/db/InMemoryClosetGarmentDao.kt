package com.closy.data.db

class InMemoryClosetGarmentDao : ClosetGarmentDao {
    private val garmentsList = mutableListOf<ClosetGarmentEntity>()

    override suspend fun insertGarment(garment: ClosetGarmentEntity) {
        garmentsList.removeAll { it.id == garment.id }
        garmentsList.add(garment)
    }

    override suspend fun updateGarment(garment: ClosetGarmentEntity) {
        val index = garmentsList.indexOfFirst { it.id == garment.id }
        if (index >= 0) {
            garmentsList[index] = garment
        } else {
            garmentsList.add(garment)
        }
    }

    override suspend fun insertGarments(garments: List<ClosetGarmentEntity>) {
        garments.forEach { insertGarment(it) }
    }

    override suspend fun getGarmentsForUser(userEmail: String): List<ClosetGarmentEntity> {
        return garmentsList.filter { it.userEmail.equals(userEmail, ignoreCase = true) }
    }

    override suspend fun deleteGarment(garment: ClosetGarmentEntity) {
        garmentsList.removeAll { it.id == garment.id }
    }

    override suspend fun deleteGarmentById(garmentId: String) {
        garmentsList.removeAll { it.id == garmentId }
    }

    override suspend fun getGarmentCountForUser(userEmail: String): Int {
        return garmentsList.count { it.userEmail.equals(userEmail, ignoreCase = true) }
    }

    override suspend fun deleteAllForUser(userEmail: String) {
        garmentsList.removeAll { it.userEmail.equals(userEmail, ignoreCase = true) }
    }
}
