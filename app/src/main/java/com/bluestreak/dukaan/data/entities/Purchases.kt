package com.bluestreak.dukaan.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchases(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val productId: Int,
    val quantityTypeId: Int,
    val categoryId: Int,
    val quantity: Int,
    val cost: Double,
    val cash: Double = 0.0,
    val upi: Double = 0.0,
    val billId: Int = 0,
    val purchaseDate: Long?
)