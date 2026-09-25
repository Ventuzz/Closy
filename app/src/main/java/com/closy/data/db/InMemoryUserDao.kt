package com.closy.data.db

class InMemoryUserDao : UserDao {
    private val users = mutableListOf<UserEntity>()

    override suspend fun insertUser(user: UserEntity) {
        val existing = users.find { it.email.equals(user.email, ignoreCase = true) }
        if (existing != null) {
            throw IllegalStateException("UNIQUE constraint failed: users.email")
        }
        val nextId = if (user.id == 0) (users.maxOfOrNull { it.id } ?: 0) + 1 else user.id
        users.add(user.copy(id = nextId))
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return users.find { it.email.equals(email, ignoreCase = true) }
    }

    override suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity? {
        return users.find { it.email.equals(email, ignoreCase = true) && it.password == password }
    }

    override suspend fun updateGenderPreference(email: String, genderPreference: String) {
        val index = users.indexOfFirst { it.email.equals(email, ignoreCase = true) }
        if (index >= 0) {
            users[index] = users[index].copy(genderPreference = genderPreference)
        }
    }

    override suspend fun deleteUserByEmail(email: String) {
        users.removeAll { it.email.equals(email, ignoreCase = true) }
    }
}
