package com.example.bihinsight.data.repository

import com.example.bihinsight.data.local.NewbornByRequestDateDao
import com.example.bihinsight.data.local.NewbornByRequestDateEntity
import com.example.bihinsight.data.model.NewbornByRequestDate
import com.example.bihinsight.data.remote.NewbornByRequestDateApiService
import com.example.bihinsight.data.remote.LanguageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewbornByRequestDateRepository(
    private val apiService: NewbornByRequestDateApiService,
    private val dao: NewbornByRequestDateDao
) {
    suspend fun fetchAndCacheNewborns(token: String? = null, languageId: Int = 1) {
        val response = apiService.getNewborns(token, LanguageRequest(languageId))
        val entities = response.result.map { it.toEntity() }
        dao.deleteAll()
        dao.insertAll(entities)
    }

    suspend fun getAllFromDb(): List<NewbornByRequestDateEntity> =
        withContext(Dispatchers.IO) {
            dao.getAllNewborns()
        }

    suspend fun filterByMunicipality(query: String): List<NewbornByRequestDateEntity> =
        withContext(Dispatchers.IO) {
            dao.filterByMunicipality(query)
        }

    suspend fun filterCombined(municipality: String?, year: Int?): List<NewbornByRequestDateEntity> =
        withContext(Dispatchers.IO) {
            dao.filterCombined(municipality, year)
        }

    suspend fun addToFavorites(id: Int) = withContext(Dispatchers.IO) { dao.addToFavorites(id) }
    suspend fun removeFromFavorites(id: Int) = withContext(Dispatchers.IO) { dao.removeFromFavorites(id) }
    suspend fun getFavorites(): List<NewbornByRequestDateEntity> = withContext(Dispatchers.IO) { dao.getFavorites() }

    suspend fun getById(id: Int): NewbornByRequestDateEntity? = withContext(Dispatchers.IO) { dao.getById(id) }

    fun observeById(id: Int) = dao.observeById(id)

    suspend fun updateNewborn(newborn: NewbornByRequestDateEntity) = withContext(Dispatchers.IO) { dao.updateNewborn(newborn) }

    fun observeFavorites() = dao.observeFavorites()
}

fun NewbornByRequestDate.toEntity(): NewbornByRequestDateEntity = NewbornByRequestDateEntity(
    id = this.id ?: 0,
    entity = this.entity,
    canton = this.canton,
    municipality = this.municipality,
    institution = this.institution,
    year = this.year,
    month = this.month,
    dateUpdate = this.dateUpdate,
    maleTotal = this.maleTotal,
    femaleTotal = this.femaleTotal,
    total = this.total
) 