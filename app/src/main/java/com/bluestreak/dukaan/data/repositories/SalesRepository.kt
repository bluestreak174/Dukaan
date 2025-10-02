package com.bluestreak.dukaan.data.repositories


import com.bluestreak.dukaan.data.entities.Sales
import kotlinx.coroutines.flow.Flow

interface SalesRepository {
    fun getAllSalesStream(): Flow<List<Sales>>
    fun getSalesStream(id: Int): Flow<Sales>

    suspend fun insertSales(sales: Sales) : Long
    suspend fun updateSales(sales: Sales)
    suspend fun deleteSales(sales: Sales)

    fun getDaySales(sellDate: Long): Flow<List<Sales>>
    suspend fun deleteSalesByBillId(billId: Int)
    fun getSalesByBillId(billId: Int): Flow<List<Sales>>
}