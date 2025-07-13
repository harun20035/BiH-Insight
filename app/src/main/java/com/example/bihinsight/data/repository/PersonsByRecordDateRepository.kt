package com.example.bihinsight.data.repository

import com.example.bihinsight.data.local.PersonsByRecordDateDao
import com.example.bihinsight.data.local.PersonsByRecordDateEntity
import com.example.bihinsight.data.model.PersonsByRecordDate
import com.example.bihinsight.data.remote.PersonsByRecordDateApiService
import com.example.bihinsight.data.remote.LanguageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PersonsByRecordDateRepository(
    private val apiService: PersonsByRecordDateApiService,
    private val dao: PersonsByRecordDateDao
) {
    suspend fun fetchAndCachePersonsByRecordDate(token: String? = null, languageId: Int = 1) {
        val response = apiService.getPersonsByRecordDate(token, LanguageRequest(languageId))
        val entities = response.result.map { it.toEntity() }
        dao.deleteAll()
        dao.insertAll(entities)
    }

    suspend fun getAllFromDb(): List<PersonsByRecordDateEntity> =
        withContext(Dispatchers.IO) {
            dao.getAllPersonsByRecordDate()
        }

    suspend fun filterByMunicipality(query: String): List<PersonsByRecordDateEntity> =
        withContext(Dispatchers.IO) {
            dao.filterByMunicipality(query)
        }

    suspend fun filterCombined(municipality: String?, year: Int?): List<PersonsByRecordDateEntity> =
        withContext(Dispatchers.IO) {
            dao.filterCombined(municipality, year)
        }

    suspend fun addToFavorites(id: Int) = withContext(Dispatchers.IO) { dao.addToFavorites(id) }
    suspend fun removeFromFavorites(id: Int) = withContext(Dispatchers.IO) { dao.removeFromFavorites(id) }
    suspend fun getFavorites(): List<PersonsByRecordDateEntity> = withContext(Dispatchers.IO) { dao.getFavorites() }

    suspend fun getById(id: Int): PersonsByRecordDateEntity? = withContext(Dispatchers.IO) { dao.getById(id) }

    fun observeById(id: Int) = dao.observeById(id)

    suspend fun updatePerson(person: PersonsByRecordDateEntity) = withContext(Dispatchers.IO) { dao.updatePerson(person) }

    fun observeFavorites() = dao.observeFavorites()

    fun getPersonsByRecordDateData(token: String? = null, languageId: Int = 1): Flow<List<PersonsByRecordDateEntity>> = flow {
        try {
            val response = apiService.getPersonsByRecordDate(token, LanguageRequest(languageId))
            val entities = response.result.map { it.toEntity() }
            dao.deleteAll()
            dao.insertAll(entities)
            emit(entities)
        } catch (e: Exception) {
            val cached = dao.getAllPersonsByRecordDate()
            emit(cached)
        }
    }
}

// Mapiranje iz PersonsByRecordDate (API model) u PersonsByRecordDateEntity (baza)
fun PersonsByRecordDate.toEntity(): PersonsByRecordDateEntity = PersonsByRecordDateEntity(
    id = this.id ?: 0,
    entity = this.entity,
    canton = this.canton,
    municipality = this.municipality,
    institution = this.institution,
    year = this.year,
    month = this.month,
    dateUpdate = this.dateUpdate,
    withResidenceTotal = this.withResidenceTotal,
    total = this.total
) 