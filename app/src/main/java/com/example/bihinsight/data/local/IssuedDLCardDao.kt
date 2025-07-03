package com.example.bihinsight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IssuedDLCardDao {
    @Query("SELECT * FROM issued_dl_cards")
    suspend fun getAllIssuedDL(): List<IssuedDLCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<IssuedDLCardEntity>)

    @Query("DELETE FROM issued_dl_cards")
    suspend fun deleteAll()
} 