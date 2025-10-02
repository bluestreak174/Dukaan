package com.bluestreak.dukaan.data.relations

import java.math.RoundingMode

data class PurchaseSales (
    val productName: String = "",
    val categoryName: String = "",
    val stock: Int = 0,
    val piece: Int = 0,
    val qtyType: String = "",
    val buyQty: Int = 0,
    val cost: Double = 0.0,
    val sellQty: Int = 0,
    val price: Double = 0.0,
)

val PurchaseSales.stockBalance: Int
    get() = calculateStockBalance()

val PurchaseSales.profitAndLoss: Double
    get() = calculateProfitAndLoss()

fun PurchaseSales.calculateStockBalance(): Int{
    return stock/piece
}

fun PurchaseSales.calculateProfitAndLoss(): Double {
    /*val itemCost = if(buyQty == 0 ) 0.0 else cost/buyQty
    val diffBuyQty = sellQty
    val tempBuyCost = diffBuyQty * itemCost
    val profitLoss = if(buyQty == sellQty){
        price - cost
    } else {
        price - tempBuyCost
    }*/
    var profitLoss = price * 10/100
    if(buyQty > 0) {
        if(buyQty == sellQty) {
            profitLoss = price - cost
        } else {
            val diffBuyQty = sellQty
            val itemCost = cost/buyQty
            val tempBuyCost = diffBuyQty * itemCost
            profitLoss = price - tempBuyCost
        }
    }
    return profitLoss.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
}