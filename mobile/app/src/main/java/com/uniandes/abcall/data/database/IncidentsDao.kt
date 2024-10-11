package com.uniandes.abcall.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniandes.abcall.data.model.Incident

@Dao
interface IncidentsDao {
    @Query("SELECT * FROM incidents_table ORDER BY id ASC")
    fun getAllIncidents(): LiveData<List<Incident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: Incident)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(artists: List<Incident>)

    @Query("DELETE FROM incidents_table")
    suspend fun deleteAll(): Int
}