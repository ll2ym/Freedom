package com.freedom.data.repository

import com.freedom.data.User
import com.freedom.data.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun addUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun getUser(username: String): User? = withContext(Dispatchers.IO) {
        userDao.getUser(username)
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAllUsers()
    }

    suspend fun updateLastSeen(username: String, timestamp: Long) = withContext(Dispatchers.IO) {
        val user = userDao.getUser(username)
        if (user != null) {
            val updated = user.copy(lastSeen = timestamp)
            userDao.insertUser(updated)
        }
    }

    suspend fun markUserAsTrusted(username: String) = withContext(Dispatchers.IO) {
        val user = userDao.getUser(username)
        if (user != null) {
            val updated = user.copy(isTrusted = true)
            userDao.insertUser(updated)
        }
    }

    suspend fun deleteUser(username: String) = withContext(Dispatchers.IO) {
        userDao.deleteUserByUsername(username)
    }
}
