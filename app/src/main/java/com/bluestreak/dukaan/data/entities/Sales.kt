package com.bluestreak.dukaan.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sales(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val productId: Int,
    val quantityTypeId: Int,
    val categoryId: Int,
    val quantity: Int,
    val price: Double,
    val cash: Double = 0.0,
    val upi: Double = 0.0,
    val billId: Int = 0,
    val sellDate: Long?,
)