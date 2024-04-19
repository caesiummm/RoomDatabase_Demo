package com.example.roomdbdemo.database.repository

import com.example.roomdbdemo.database.dao.UserDao
import com.example.roomdbdemo.database.entity.User

class UserRepository(private val userDao: UserDao) {

    val users = userDao.getAllUsers()

    suspend fun add(user: User) : Long{
        return userDao.addUser(user)
    }

    suspend fun add(users: List<User>) {
        userDao.addUser(users)
    }

    suspend fun update(user: User) : Int {
        return userDao.updateUser(user)
    }

    suspend fun delete(user: User) : Int {
        return userDao.deleteUser(user)
    }

    suspend fun deleteAll() : Int {
        return userDao.deleteAllUser()
    }

    suspend fun isUserNameExists(userName: String): Boolean {
        val count = userDao.getUserNameCount(userName)
        return count > 0
    }
}