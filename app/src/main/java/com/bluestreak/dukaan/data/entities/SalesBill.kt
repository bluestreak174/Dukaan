package com.bluestreak.dukaan.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_bill")
data class SalesBill(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cash: Double = 0.0,
    val upi: Double = 0.0,
    val total: Double = 0.0,
    val billDate: Long,
    val isDraft: Boolean = true,
    val billAddress: String = ""
)