package com.uniandes.abcall.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Timestamp

@Entity(tableName = "incidents_table")
data class Incident (
    @PrimaryKey
    val id:Int,
    val userId:String,
    val subject:String?,
    val description:String,
    val originType:String,
    val status:String,
    val solution:String?,
    val creationDate: Timestamp,
    val updateDate:Timestamp,
    val solutionAgentId:String?,
    val solutionDate:Timestamp?
): Serializable