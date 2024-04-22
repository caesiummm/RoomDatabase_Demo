package com.example.roomdbdemo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roomdbdemo.database.entity.User

@Dao
interface UserDao {

    // Room DOES NOT support db access on main thread, to avoid blocking UI thread
    // Hence, these methods will be executed on background thread (by using Kotlin coroutines)

    // If an existing row has same ID, it will be deleted & replaced
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User) : Long

//    @Insert
//    suspend fun addUser(users: List<User>)

    @Update
    suspend fun updateUser(user: User) : Int

    @Delete
    suspend fun deleteUser(user: User) : Int

    @Query("DELETE FROM user_info")
    suspend fun deleteAllUser() : Int

    @Query("SELECT * FROM user_info")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT COUNT(*) FROM user_info WHERE userName = :userName")
    suspend fun getUserNameCount(userName: String): Int
}