package com.bluestreak.dukaan.data.relations

data class ProductHistory(
    val name: String = "",
    val buySell: String = "",
    val date: Long = 0,
    val qtyType: String = "",
    val quantity: Int = 0,
    val amount: Double = 0.0,
    val billId: Int = 0
)
