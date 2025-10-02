package com.bluestreak.dukaan.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qty_type_master")
data class QuantityType (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: String,
    val piece: Int,
)