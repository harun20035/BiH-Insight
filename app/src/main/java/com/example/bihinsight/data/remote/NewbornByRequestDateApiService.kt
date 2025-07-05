package com.example.bihinsight.data.remote

import com.example.bihinsight.data.model.NewbornByRequestDate
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class NewbornByRequestDateResponse(
    val errors: List<String>,
    val result: List<NewbornByRequestDate>
)

interface NewbornByRequestDateApiService {
    @POST("api/NewbornByRequestDate/list")
    suspend fun getNewborns(
        @Header("Authorization") token: String? = null,
        @Body body: LanguageRequest
    ): NewbornByRequestDateResponse
} 