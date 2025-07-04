package com.example.bihinsight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IssuedDLCardEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun issuedDLCardDao(): IssuedDLCardDao
} 