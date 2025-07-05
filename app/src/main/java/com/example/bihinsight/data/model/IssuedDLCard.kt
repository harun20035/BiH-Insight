package com.example.bihinsight.data.model

data class IssuedDLCard(
    val id: Int? = null,
    val entity: String,
    val canton: String?,
    val municipality: String?,
    val institution: String?,
    val year: Int?,
    val month: Int?,
    val dateUpdate: String?,
    val issuedFirstTimeMaleTotal: Int?,
    val replacedMaleTotal: Int?,
    val issuedFirstTimeFemaleTotal: Int?,
    val replacedFemaleTotal: Int?,
    val total: Int?
) 