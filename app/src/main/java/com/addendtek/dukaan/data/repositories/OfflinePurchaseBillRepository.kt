package com.addendtek.dukaan.data.repositories

import com.addendtek.dukaan.data.dao.PurchaseBillDao
import com.addendtek.dukaan.data.entities.PurchaseBill
import com.addendtek.dukaan.data.relations.BillDetails
import com.addendtek.dukaan.data.relations.TotalBill
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

    override suspend fun upsertPurchaseBill(purchaseBill: PurchaseBill): Long = purchaseBillDao.upsertPurchaseBill(purchaseBill)
}