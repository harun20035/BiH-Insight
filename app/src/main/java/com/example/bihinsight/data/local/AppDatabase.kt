package com.example.bihinsight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IssuedDLCardEntity::class, PersonsByRecordDateEntity::class, NewbornByRequestDateEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun issuedDLCardDao(): IssuedDLCardDao
    abstract fun personsByRecordDateDao(): PersonsByRecordDateDao
    abstract fun newbornByRequestDateDao(): NewbornByRequestDateDao
} 