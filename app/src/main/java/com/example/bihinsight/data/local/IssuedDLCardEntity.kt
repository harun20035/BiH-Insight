package com.example.bihinsight.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issued_dl_cards")
data class IssuedDLCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val entity: String?,
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
    val total: Int?,
    val isFavorite: Boolean = false
) 