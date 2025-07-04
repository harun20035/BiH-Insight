package com.example.bihinsight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IssuedDLCardDao {
    @Query("SELECT * FROM issued_dl_cards")
    suspend fun getAllIssuedDL(): List<IssuedDLCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<IssuedDLCardEntity>)

    @Query("DELETE FROM issued_dl_cards")
    suspend fun deleteAll()

    @Query("SELECT * FROM issued_dl_cards WHERE municipality LIKE '%' || :query || '%'")
    suspend fun filterByMunicipality(query: String): List<IssuedDLCardEntity>

    @Query("""
        SELECT * FROM issued_dl_cards
        WHERE (:municipality IS NULL OR municipality LIKE '%' || :municipality || '%')
        AND (:year IS NULL OR year = :year)
    """)
    suspend fun filterCombined(
        municipality: String?,
        year: Int?
    ): List<IssuedDLCardEntity>

    @Query("UPDATE issued_dl_cards SET isFavorite = 1 WHERE id = :id")
    suspend fun addToFavorites(id: Int)

    @Query("UPDATE issued_dl_cards SET isFavorite = 0 WHERE id = :id")
    suspend fun removeFromFavorites(id: Int)

    @Query("SELECT * FROM issued_dl_cards WHERE isFavorite = 1")
    suspend fun getFavorites(): List<IssuedDLCardEntity>

    @Query("SELECT * FROM issued_dl_cards WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): IssuedDLCardEntity?

    @Query("SELECT * FROM issued_dl_cards WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<IssuedDLCardEntity?>

    @Update
    suspend fun updateCard(card: IssuedDLCardEntity)
} 