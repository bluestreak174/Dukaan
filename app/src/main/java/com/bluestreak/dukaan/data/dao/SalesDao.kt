package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bluestreak.dukaan.data.entities.Sales
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sales: Sales) : Long

    @Update
    suspend fun update(sales: Sales)

    @Delete
    suspend fun delete(sales: Sales)

    @Query("SELECT * from sales where id = :id")
    fun getSales(id: Int): Flow<Sales>

    @Query("SELECT * from sales ORDER BY id DESC")
    fun getAllSales(): Flow<List<Sales>>

    @Query("SELECT * from sales where sellDate = :sellDate ORDER BY id DESC")
    fun getDaySales(sellDate: Long): Flow<List<Sales>>

    @Query("DELETE from sales WHERE billId = :billId")
    suspend fun deleteSalesByBillId(billId: Int)

    @Query("SELECT * from sales WHERE billId = :billId")
    fun getSalesByBillId(billId: Int): Flow<List<Sales>>
}