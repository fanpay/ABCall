package com.uniandes.abcall.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uniandes.abcall.data.model.User
import com.uniandes.abcall.data.model.Incident


@Database(
    entities = [User::class, Incident::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ABCallRoomDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun incidentsDao(): IncidentsDao

    companion object {
        @Volatile
        private var INSTANCE: ABCallRoomDatabase? = null

        private const val DATABASE_NAME = "abcall_database"

        fun getDatabase(context: Context
        ): ABCallRoomDatabase{
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ABCallRoomDatabase::class.java,
                    DATABASE_NAME
                )
                // Wipes and rebuilds instead of migrating if no Migration object.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}