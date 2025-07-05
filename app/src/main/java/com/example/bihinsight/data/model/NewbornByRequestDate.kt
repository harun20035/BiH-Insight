package com.example.bihinsight.data.model

data class NewbornByRequestDate(
    val id: Int? = null,
    val entity: String,
    val canton: String?,
    val municipality: String?,
    val institution: String?,
    val year: Int?,
    val month: Int?,
    val dateUpdate: String?,
    val maleTotal: Int?,
    val femaleTotal: Int?,
    val total: Int?
) 