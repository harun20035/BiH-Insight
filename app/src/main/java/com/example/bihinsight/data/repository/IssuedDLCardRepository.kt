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
}

// Mapiranje iz IssuedDLCard (API model) u IssuedDLCardEntity (baza)
fun IssuedDLCard.toEntity(): IssuedDLCardEntity = IssuedDLCardEntity(
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