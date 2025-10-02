package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.dao.ProductPurchasesDao
import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.relations.ProductQuantity
import com.bluestreak.dukaan.data.relations.PurchaseSales
import kotlinx.coroutines.flow.Flow

class OfflineProductPurchaseRepository(private val productPurchasesDao: ProductPurchasesDao) : ProductPurchaseRepository {
    override fun getProductQtyPurchasesBetweenDates(startDate: Long, endDate: Long): Flow<Map<Purchases, ProductQuantity>> = productPurchasesDao.getProductQtyPurchasesBetweenDates(startDate,endDate)
    override fun getPurchasesAndSalesBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<List<PurchaseSales>> = productPurchasesDao.getPurchasesAndSalesBetweenDates(startDate, endDate)
}