package com.uniandes.abcall.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.sql.Timestamp
import java.util.Date

@Entity(tableName = "incidents_table")
data class Incident (
    @PrimaryKey
    @SerializedName("id")
    val incidentId:Int,
    val userId:String,
    val description:String,
    val originType:String,
    val status:String,
    val solution:String,
    val creationDate:Timestamp,
    val updateDate:Timestamp,
    val solutionAgentId:String,
    val solutionDate:Timestamp
): Serializable