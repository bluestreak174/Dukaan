package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.dao.SalesDao
import com.bluestreak.dukaan.data.entities.Sales
import kotlinx.coroutines.flow.Flow

class OfflineSalesRepository(private val salesDao: SalesDao): SalesRepository {
    override fun getAllSalesStream(): Flow<List<Sales>> = salesDao.getAllSales()

    override fun getSalesStream(id: Int): Flow<Sales> = salesDao.getSales(id)

    override suspend fun insertSales(sales: Sales): Long = salesDao.insert(sales)

    override suspend fun updateSales(sales: Sales) = salesDao.update(sales)

    override suspend fun deleteSales(sales: Sales) = salesDao.delete(sales)

    override fun getDaySales(sellDate: Long): Flow<List<Sales>> = salesDao.getDaySales(sellDate)

    override suspend fun deleteSalesByBillId(billId: Int) = salesDao.deleteSalesByBillId(billId)
    override fun getSalesByBillId(billId: Int): Flow<List<Sales>> = salesDao.getSalesByBillId(billId)

}