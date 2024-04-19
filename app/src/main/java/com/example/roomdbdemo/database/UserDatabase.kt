package com.example.roomdbdemo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdbdemo.database.dao.UserDao
import com.example.roomdbdemo.database.entity.User

/*
* Automated Room migrations rely on the generated database schema for both the old and the new versions of the database.
* If exportSchema is set to false, or if you have not yet compiled the database with the new version number, then automated migrations fail.
*/
@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDao: UserDao

    // should only use one instance of Room database for the entire application
    companion object {
        @Volatile // makes the field immediately visible to other threads
        private var INSTANCE : UserDatabase? = null
        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_info_db"
                    ).allowMainThreadQueries().build()
                }
                return INSTANCE!!
            }
        }
    }
}