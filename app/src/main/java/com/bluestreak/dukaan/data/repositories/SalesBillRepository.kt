package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.entities.SalesBill
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.data.relations.TotalBill
import kotlinx.coroutines.flow.Flow

interface SalesBillRepository {
    fun getAllSalesBillStream(): Flow<List<SalesBill>>
    fun getSalesBillStream(id: Int): Flow<SalesBill>
    suspend fun insertSalesBill(salesBill: SalesBill) : Long
    suspend fun deleteSalesBill(salesBill: SalesBill)
    suspend fun updateSalesBill(salesBill: SalesBill)
    fun getBillBetweenDates(startDate: Long, endDate: Long): Flow<TotalBill>
    fun getAllSalesBillsByDate(startDate: Long, endDate: Long): Flow<List<SalesBill>>
    fun getBillDetails(billId: Int): Flow<List<BillDetails>>
}