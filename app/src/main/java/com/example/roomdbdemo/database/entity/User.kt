package com.example.roomdbdemo.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class User (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId")  var userId: Long,
    @ColumnInfo(name = "firstName") var firstName: String? = null,
    @ColumnInfo(name = "lastName") var lastName: String? = null,
    @ColumnInfo(name = "userName") var userName: String? = null,
    @ColumnInfo(name = "phone") var phone: String? = null,
    @ColumnInfo(name = "email") var email: String? = null,
    @ColumnInfo(name = "portrait") var portrait: String? = null
){

}