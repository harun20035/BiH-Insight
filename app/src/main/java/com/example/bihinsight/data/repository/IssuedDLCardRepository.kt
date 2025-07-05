package com.example.bihinsight.data.repository

import com.example.bihinsight.data.local.IssuedDLCardDao
import com.example.bihinsight.data.local.IssuedDLCardEntity
import com.example.bihinsight.data.model.IssuedDLCard
import com.example.bihinsight.data.remote.IssuedDLCardApiService
import com.example.bihinsight.data.remote.LanguageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IssuedDLCardRepository(
    private val apiService: IssuedDLCardApiService,
    private val dao: IssuedDLCardDao
) {
    suspend fun fetchAndCacheIssuedDL(token: String? = null, languageId: Int = 1) {
        val response = apiService.getIssuedDLCards(token, LanguageRequest(languageId))
        val entities = response.result.map { it.toEntity() }
        dao.deleteAll()
        dao.insertAll(entities)
    }

    suspend fun getAllFromDb(): List<IssuedDLCardEntity> =
        withContext(Dispatchers.IO) {
            dao.getAllIssuedDL()
        }

    suspend fun filterByMunicipality(query: String): List<IssuedDLCardEntity> =
        withContext(Dispatchers.IO) {
            dao.filterByMunicipality(query)
        }

    suspend fun filterCombined(municipality: String?, year: Int?): List<IssuedDLCardEntity> =
        withContext(Dispatchers.IO) {
            dao.filterCombined(municipality, year)
        }

    suspend fun addToFavorites(id: Int) = withContext(Dispatchers.IO) { dao.addToFavorites(id) }
    suspend fun removeFromFavorites(id: Int) = withContext(Dispatchers.IO) { dao.removeFromFavorites(id) }
    suspend fun getFavorites(): List<IssuedDLCardEntity> = withContext(Dispatchers.IO) { dao.getFavorites() }

    suspend fun getById(id: Int): IssuedDLCardEntity? = withContext(Dispatchers.IO) { dao.getById(id) }

    fun observeById(id: Int) = dao.observeById(id)

    suspend fun updateCard(card: IssuedDLCardEntity) = withContext(Dispatchers.IO) { dao.updateCard(card) }

    fun observeFavorites() = dao.observeFavorites()
}

// Mapiranje iz IssuedDLCard (API model) u IssuedDLCardEntity (baza)
fun IssuedDLCard.toEntity(): IssuedDLCardEntity = IssuedDLCardEntity(
    id = this.id ?: 0,
    entity = this.entity,
    canton = this.canton,
    municipality = this.municipality,
    institution = this.institution,
    year = this.year,
    month = this.month,
    dateUpdate = this.dateUpdate,
    issuedFirstTimeMaleTotal = this.issuedFirstTimeMaleTotal,
    replacedMaleTotal = this.replacedMaleTotal,
    issuedFirstTimeFemaleTotal = this.issuedFirstTimeFemaleTotal,
    replacedFemaleTotal = this.replacedFemaleTotal,
    total = this.total
) 