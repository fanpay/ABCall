package com.uniandes.abcall.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniandes.abcall.data.model.User

@Dao
interface UsersDao {
    @Query("SELECT * FROM users_table WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT token FROM users_table WHERE id = :userId LIMIT 1")
    suspend fun getUserTokenById(userId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("DELETE FROM users_table")
    suspend fun deleteAll(): Int
}
