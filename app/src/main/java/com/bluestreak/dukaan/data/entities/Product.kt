package com.bluestreak.dukaan.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_master",)
data class Product (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val cost: Double = 0.0,
    val mrp: Double = 0.0,
    val qty: Int = 0,
    val qtyTypeId: Int = 0,
    val categoryId: Int = 0,
    val barCode: Long = 0
)
