package com.addendtek.dukaan.data.repositories

import com.addendtek.dukaan.data.entities.Purchases
import com.addendtek.dukaan.data.relations.ProductQuantity
import com.addendtek.dukaan.data.relations.PurchaseSales
import kotlinx.coroutines.flow.Flow

interface ProductPurchaseRepository {
    fun getProductQtyPurchasesBetweenDates(startDate: Long, endDate: Long): Flow<Map<Purchases, ProductQuantity>>
    fun getPurchasesAndSalesBetweenDates(startDate: Long, endDate: Long): Flow<List<PurchaseSales>>
}