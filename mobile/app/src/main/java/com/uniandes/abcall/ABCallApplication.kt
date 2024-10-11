package com.uniandes.abcall

import android.app.Application
import com.uniandes.abcall.data.database.ABCallRoomDatabase

class ABCallApplication: Application()  {
    val database by lazy { ABCallRoomDatabase.getDatabase(this) }
}