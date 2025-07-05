package com.example.bihinsight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NewbornByRequestDateDao {
    @Query("SELECT * FROM newborn_by_request_date")
    suspend fun getAllNewborns(): List<NewbornByRequestDateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newborns: List<NewbornByRequestDateEntity>)

    @Query("DELETE FROM newborn_by_request_date")
    suspend fun deleteAll()

    @Query("SELECT * FROM newborn_by_request_date WHERE municipality LIKE '%' || :query || '%'")
    suspend fun filterByMunicipality(query: String): List<NewbornByRequestDateEntity>

    @Query("""
        SELECT * FROM newborn_by_request_date
        WHERE (:municipality IS NULL OR municipality LIKE '%' || :municipality || '%')
        AND (:year IS NULL OR year = :year)
    """)
    suspend fun filterCombined(
        municipality: String?,
        year: Int?
    ): List<NewbornByRequestDateEntity>

    @Query("UPDATE newborn_by_request_date SET isFavorite = 1 WHERE id = :id")
    suspend fun addToFavorites(id: Int)

    @Query("UPDATE newborn_by_request_date SET isFavorite = 0 WHERE id = :id")
    suspend fun removeFromFavorites(id: Int)

    @Query("SELECT * FROM newborn_by_request_date WHERE isFavorite = 1")
    suspend fun getFavorites(): List<NewbornByRequestDateEntity>

    @Query("SELECT * FROM newborn_by_request_date WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): NewbornByRequestDateEntity?

    @Query("SELECT * FROM newborn_by_request_date WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<NewbornByRequestDateEntity?>

    @Query("SELECT * FROM newborn_by_request_date WHERE isFavorite = 1")
    fun observeFavorites(): Flow<List<NewbornByRequestDateEntity>>

    @Update
    suspend fun updateNewborn(newborn: NewbornByRequestDateEntity)
} 