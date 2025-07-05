package com.example.bihinsight.data.model

data class PersonsByRecordDate(
    val id: Int? = null,
    val entity: String,
    val canton: String?,
    val municipality: String?,
    val institution: String?,
    val year: Int?,
    val month: Int?,
    val dateUpdate: String?,
    val withResidenceTotal: Int?,
    val total: Int?
) 