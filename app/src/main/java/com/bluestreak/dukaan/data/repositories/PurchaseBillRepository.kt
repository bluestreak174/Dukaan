package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.entities.PurchaseBill
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.data.relations.TotalBill
import kotlinx.coroutines.flow.Flow

interface PurchaseBillRepository {
    fun getAllPurchaseBillStream(): Flow<List<PurchaseBill>>
    fun getPurchaseBillStream(id: Int): Flow<PurchaseBill>
    suspend fun insertPurchaseBill(purchaseBill: PurchaseBill) : Long
    suspend fun deletePurchaseBill(purchaseBill: PurchaseBill)
    suspend fun updatePurchaseBill(purchaseBill: PurchaseBill)
    fun getBillBetweenDates(startDate: Long, endDate: Long): Flow<TotalBill>
    fun getAllPurchaseBillsByDate(startDate: Long, endDate: Long): Flow<List<PurchaseBill>>
    fun getBillDetails(billId: Int): Flow<List<BillDetails>>
}