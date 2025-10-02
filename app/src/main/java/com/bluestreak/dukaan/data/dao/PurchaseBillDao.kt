package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bluestreak.dukaan.data.entities.PurchaseBill
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.data.relations.TotalBill
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseBillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchaseBill: PurchaseBill) : Long

    @Update
    suspend fun update(purchaseBill: PurchaseBill)

    @Delete
    suspend fun delete(purchaseBill: PurchaseBill)

    @Query("SELECT * from purchase_bill where id = :id")
    fun getPurchaseBill(id: Int): Flow<PurchaseBill>

    @Query("SELECT * from purchase_bill ORDER BY id DESC")
    fun getAllPurchaseBills(): Flow<List<PurchaseBill>>

    @Query("SELECT * from purchase_bill " +
            "WHERE purchase_bill.billDate >= :startDate and purchase_bill.billDate <= :endDate " +
            "ORDER BY id DESC")
    fun getAllPurchaseBillsByDate(startDate: Long, endDate: Long): Flow<List<PurchaseBill>>

    @Query("SELECT SUM(purchase_bill.total)  as total, SUM(purchase_bill.cash)  as cash, " +
            " SUM(purchase_bill.upi) as upi from purchase_bill " +
    "WHERE purchase_bill.billDate >= :startDate and purchase_bill.billDate <= :endDate"
    )
    fun getBillBetweenDates(startDate: Long, endDate: Long): Flow<TotalBill>

    @Query("SELECT purchase_bill.billAddress as billAddress, purchase_bill.billDate as billDate ,product_master.name as productName, purchases.quantity as qty, " +
            "qty_type_master.type as qtyType, purchases.cost as amount, " +
            "purchases.purchaseDate as date from purchases " +
            "        inner join qty_type_master on purchases.quantityTypeId = qty_type_master.id  \n" +
            "        inner join product_master on purchases.productId = product_master.id  \n" +
            "        inner join purchase_bill on purchases.billId = purchase_bill.id " +
            " where purchase_bill.id = :billId")
    fun getBillDetails(billId: Int): Flow<List<BillDetails>>
}
