package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.dao.PurchasesDao
import com.bluestreak.dukaan.data.entities.Purchases
import kotlinx.coroutines.flow.Flow

class OfflinePurchasesRepository(private val purchasesDao: PurchasesDao): PurchaseRepository {
    override fun getAllPurchasesStream(): Flow<List<Purchases>> = purchasesDao.getAllPurchases()

    override fun getPurchaseStream(id: Int): Flow<Purchases> = purchasesDao.getPurchase(id)

    override suspend fun insertPurchases(purchases: Purchases) = purchasesDao.insert(purchases)
    override suspend fun deletePurchases(purchases: Purchases) = purchasesDao.delete(purchases)
    override suspend fun updatePurchases(purchases: Purchases) = purchasesDao.update(purchases)
    override suspend fun deletePurchasesByBillId(billId: Int) = purchasesDao.deletePurchasesByBillId(billId)
    override fun getPurchasesByBillId(billId: Int): Flow<List<Purchases>> = purchasesDao.getPurchasesByBillId(billId)


}