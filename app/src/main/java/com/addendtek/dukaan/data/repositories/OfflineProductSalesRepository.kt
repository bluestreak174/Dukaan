package com.addendtek.dukaan.data.repositories

import com.addendtek.dukaan.data.dao.ProductSalesDao
import com.addendtek.dukaan.data.entities.Sales
import com.addendtek.dukaan.data.relations.ProductHistory
import com.addendtek.dukaan.data.relations.ProductQuantity
import kotlinx.coroutines.flow.Flow

class OfflineProductSalesRepository(private val productSalesDao: ProductSalesDao) : ProductSalesRepository {
    override fun getProductQtySalesBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<Map<Sales, ProductQuantity>> = productSalesDao.getProductQtyPurchasesBetweenDates(startDate, endDate)

    override fun getProductBuySell(productId: Int): Flow<List<ProductHistory>> = productSalesDao.getProductBuySell(productId)
}