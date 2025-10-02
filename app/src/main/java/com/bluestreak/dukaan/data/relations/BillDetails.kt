package com.bluestreak.dukaan.data.relations

data class BillDetails (
    val billAddress: String = "",
    val billDate: Long = 0,
    val productName: String = "",
    val qty: Int = 0,
    val qtyType: String = "",
    val amount: Double = 0.0,

)