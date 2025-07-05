package com.example.bihinsight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonsByRecordDateDao {
    @Query("SELECT * FROM persons_by_record_date")
    suspend fun getAllPersonsByRecordDate(): List<PersonsByRecordDateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(persons: List<PersonsByRecordDateEntity>)

    @Query("DELETE FROM persons_by_record_date")
    suspend fun deleteAll()

    @Query("SELECT * FROM persons_by_record_date WHERE municipality LIKE '%' || :query || '%'")
    suspend fun filterByMunicipality(query: String): List<PersonsByRecordDateEntity>

    @Query("""
        SELECT * FROM persons_by_record_date
        WHERE (:municipality IS NULL OR municipality LIKE '%' || :municipality || '%')
        AND (:year IS NULL OR year = :year)
    """)
    suspend fun filterCombined(
        municipality: String?,
        year: Int?
    ): List<PersonsByRecordDateEntity>

    @Query("UPDATE persons_by_record_date SET isFavorite = 1 WHERE id = :id")
    suspend fun addToFavorites(id: Int)

    @Query("UPDATE persons_by_record_date SET isFavorite = 0 WHERE id = :id")
    suspend fun removeFromFavorites(id: Int)

    @Query("SELECT * FROM persons_by_record_date WHERE isFavorite = 1")
    suspend fun getFavorites(): List<PersonsByRecordDateEntity>

    @Query("SELECT * FROM persons_by_record_date WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PersonsByRecordDateEntity?

    @Query("SELECT * FROM persons_by_record_date WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<PersonsByRecordDateEntity?>

    @Query("SELECT * FROM persons_by_record_date WHERE isFavorite = 1")
    fun observeFavorites(): Flow<List<PersonsByRecordDateEntity>>

    @Update
    suspend fun updatePerson(person: PersonsByRecordDateEntity)
} 