package com.example.bihinsight.data.remote

import com.example.bihinsight.data.model.PersonsByRecordDate
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class PersonsByRecordDateResponse(
    val errors: List<String>,
    val result: List<PersonsByRecordDate>
)

interface PersonsByRecordDateApiService {
    @POST("api/PersonsByRecordDate/list")
    suspend fun getPersonsByRecordDate(
        @Header("Authorization") token: String? = null,
        @Body body: LanguageRequest
    ): PersonsByRecordDateResponse
} 