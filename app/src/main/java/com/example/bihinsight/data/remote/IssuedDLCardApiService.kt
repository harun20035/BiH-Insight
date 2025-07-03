package com.example.bihinsight.data.remote

import com.example.bihinsight.data.model.IssuedDLCard
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Request body model
data class LanguageRequest(val languageId: Int)

data class IssuedDLCardResponse(
    val errors: List<String>,
    val result: List<IssuedDLCard>
)

interface IssuedDLCardApiService {
    @POST("api/IssuedDLCards/list")
    suspend fun getIssuedDLCards(
        @Header("Authorization") token: String? = null,
        @Body body: LanguageRequest
    ): IssuedDLCardResponse
} 