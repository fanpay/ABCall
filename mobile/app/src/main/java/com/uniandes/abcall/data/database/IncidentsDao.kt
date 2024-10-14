package com.uniandes.abcall.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniandes.abcall.data.model.Incident

@Dao
interface IncidentsDao {
    @Query("SELECT * FROM incidents_table WHERE userId = :userId ORDER BY updateDate DESC")
    fun getAllIncidentsSync(userId: String): List<Incident>

    @Query("SELECT * FROM incidents_table WHERE userId = :userId ORDER BY updateDate DESC")
    fun getAllIncidents(userId:String): LiveData<List<Incident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: Incident)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incident: List<Incident>)

    @Query("DELETE FROM incidents_table")
    suspend fun deleteAll(): Int
}
