package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.entities.Sales
import com.bluestreak.dukaan.data.relations.ProductHistory
import com.bluestreak.dukaan.data.relations.ProductQuantity
import kotlinx.coroutines.flow.Flow

interface ProductSalesRepository {
    fun getProductQtySalesBetweenDates(startDate: Long, endDate: Long): Flow<Map<Sales, ProductQuantity>>
    fun getProductBuySell(productId: Int): Flow<List<ProductHistory>>
}