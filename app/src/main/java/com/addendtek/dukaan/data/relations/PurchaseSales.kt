package com.addendtek.dukaan.data.relations

data class PurchaseSales (
    val productId: Int = 0,
    val productName: String = "",
    val categoryName: String = "",
    val stock: Int = 0,
    val piece: Int = 0,
    val qtyType: String = "",
    val buyQty: Int = 0,
    val cost: Double = 0.0,
    val sellQty: Int = 0,
    val price: Double = 0.0,
    var profitAndLoss: Double = 0.0

)

val PurchaseSales.stockBalance: Int
    get() = calculateStockBalance()

fun PurchaseSales.calculateStockBalance(): Int{
    return stock/piece
}
