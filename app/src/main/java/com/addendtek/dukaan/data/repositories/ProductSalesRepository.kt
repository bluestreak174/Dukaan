package com.addendtek.dukaan.data.repositories

import com.addendtek.dukaan.data.entities.Sales
import com.addendtek.dukaan.data.relations.ProductHistory
import com.addendtek.dukaan.data.relations.ProductQuantity
import kotlinx.coroutines.flow.Flow

interface ProductSalesRepository {
    fun getProductQtySalesBetweenDates(startDate: Long, endDate: Long): Flow<Map<Sales, ProductQuantity>>
    fun getProductBuySell(productId: Int): Flow<List<ProductHistory>>
}