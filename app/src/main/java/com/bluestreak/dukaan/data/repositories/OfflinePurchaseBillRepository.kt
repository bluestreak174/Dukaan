package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.dao.PurchaseBillDao
import com.bluestreak.dukaan.data.entities.PurchaseBill
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.data.relations.TotalBill
import kotlinx.coroutines.flow.Flow

class OfflinePurchaseBillRepository(private val purchaseBillDao: PurchaseBillDao) : PurchaseBillRepository {
    override fun getAllPurchaseBillStream(): Flow<List<PurchaseBill>> = purchaseBillDao.getAllPurchaseBills()

    override fun getPurchaseBillStream(id: Int): Flow<PurchaseBill> = purchaseBillDao.getPurchaseBill(id)

    override suspend fun insertPurchaseBill(purchaseBill: PurchaseBill) = purchaseBillDao.insert(purchaseBill)
    override suspend fun deletePurchaseBill(purchaseBill: PurchaseBill) = purchaseBillDao.delete(purchaseBill)

    override suspend fun updatePurchaseBill(purchaseBill: PurchaseBill) = purchaseBillDao.update(purchaseBill)
    override fun getBillBetweenDates(startDate: Long, endDate: Long): Flow<TotalBill> =
        purchaseBillDao.getBillBetweenDates(startDate, endDate)

    override fun getAllPurchaseBillsByDate(
        startDate: Long,
        endDate: Long
    ): Flow<List<PurchaseBill>> =
        purchaseBillDao.getAllPurchaseBillsByDate(startDate, endDate)

    override fun getBillDetails(billId: Int): Flow<List<BillDetails>> =
        purchaseBillDao.getBillDetails(billId)


}