package com.example.bihinsight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IssuedDLCardEntity::class, PersonsByRecordDateEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun issuedDLCardDao(): IssuedDLCardDao
    abstract fun personsByRecordDateDao(): PersonsByRecordDateDao
} 