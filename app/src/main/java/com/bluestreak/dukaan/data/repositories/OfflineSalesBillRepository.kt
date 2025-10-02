package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.dao.SalesBillDao
import com.bluestreak.dukaan.data.entities.SalesBill
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.data.relations.TotalBill
import kotlinx.coroutines.flow.Flow

class OfflineSalesBillRepository(private val salesBillDao: SalesBillDao) : SalesBillRepository {
    override fun getAllSalesBillStream(): Flow<List<SalesBill>> = salesBillDao.getAllSalesBills()

    override fun getSalesBillStream(id: Int): Flow<SalesBill> = salesBillDao.getSalesBill(id)

    override suspend fun insertSalesBill(salesBill: SalesBill) : Long = salesBillDao.insert(salesBill)

    override suspend fun deleteSalesBill(salesBill: SalesBill) = salesBillDao.delete(salesBill)

    override suspend fun updateSalesBill(salesBill: SalesBill) = salesBillDao.update(salesBill)

    override fun getBillBetweenDates(startDate: Long, endDate: Long): Flow<TotalBill> =
        salesBillDao.getBillBetweenDates(startDate, endDate)

    override fun getAllSalesBillsByDate(startDate: Long, endDate: Long): Flow<List<SalesBill>> =
        salesBillDao.getAllSalesBillsByDate(startDate, endDate)

    override fun getBillDetails(billId: Int): Flow<List<BillDetails>> = salesBillDao.getBillDetails(billId)
}