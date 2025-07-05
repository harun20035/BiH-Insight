package com.example.bihinsight.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "newborn_by_request_date")
data class NewbornByRequestDateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val entity: String?,
    val canton: String?,
    val municipality: String?,
    val institution: String?,
    val year: Int?,
    val month: Int?,
    val dateUpdate: String?,
    val maleTotal: Int?,
    val femaleTotal: Int?,
    val total: Int?,
    val isFavorite: Boolean = false
) 