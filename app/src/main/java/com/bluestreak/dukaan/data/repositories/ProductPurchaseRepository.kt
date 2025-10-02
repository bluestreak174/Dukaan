package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.relations.ProductQuantity
import com.bluestreak.dukaan.data.relations.PurchaseSales
import kotlinx.coroutines.flow.Flow

interface ProductPurchaseRepository {
    fun getProductQtyPurchasesBetweenDates(startDate: Long, endDate: Long): Flow<Map<Purchases, ProductQuantity>>
    fun getPurchasesAndSalesBetweenDates(startDate: Long, endDate: Long): Flow<List<PurchaseSales>>
}