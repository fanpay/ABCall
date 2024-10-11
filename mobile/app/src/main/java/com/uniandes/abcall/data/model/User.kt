package com.uniandes.abcall.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users_table")
data class User (
    @PrimaryKey
    val id:String,
    val username:String,
    val email: String,
    val fullName:String,
    val dni:String,
    val phoneNumber: String,
    val role:String,
    val token: String?
    /*val name:String,
    val lastName:String,
    val identificationType:String,
    val identificationNumber:String,
    val password:String,
    val role:String*/
) : Serializable
