package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bluestreak.dukaan.data.entities.SalesBill
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.data.relations.TotalBill
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesBillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(salesBill: SalesBill) : Long

    @Update
    suspend fun update(salesBill: SalesBill)

    @Delete
    suspend fun delete(salesBill: SalesBill)

    @Query("SELECT * from sales_bill where id = :id")
    fun getSalesBill(id: Int): Flow<SalesBill>

    @Query("SELECT * from sales_bill ORDER BY id DESC")
    fun getAllSalesBills(): Flow<List<SalesBill>>

    @Query("SELECT * from sales_bill " +
            "WHERE sales_bill.billDate >= :startDate and sales_bill.billDate <= :endDate " +
            "ORDER BY id DESC")
    fun getAllSalesBillsByDate(startDate: Long, endDate: Long): Flow<List<SalesBill>>

    @Query("SELECT SUM(sales_bill.total)  as total, SUM(sales_bill.cash)  as cash, " +
            " SUM(sales_bill.upi) as upi from sales_bill " +
            "WHERE sales_bill.billDate >= :startDate and sales_bill.billDate <= :endDate"
    )
    fun getBillBetweenDates(startDate: Long, endDate: Long): Flow<TotalBill>

    @Query("SELECT sales_bill.billAddress as billAddress, sales_bill.billDate as billDate, product_master.name as productName, sales.quantity as qty, " +
            "qty_type_master.type as qtyType, sales.price as amount, " +
            "sales.sellDate as date from sales " +
            "        inner join qty_type_master on sales.quantityTypeId = qty_type_master.id  \n" +
            "        inner join product_master on sales.productId = product_master.id  \n" +
            "        inner join sales_bill on sales.billId = sales_bill.id " +
            " where sales_bill.id = :billId")
    fun getBillDetails(billId: Int): Flow<List<BillDetails>>
}