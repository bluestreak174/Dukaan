package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bluestreak.dukaan.data.entities.Purchases
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchasesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchases: Purchases)

    @Update
    suspend fun update(purchases: Purchases)

    @Delete
    suspend fun delete(purchases: Purchases)

    @Query("SELECT * from purchases where id = :id")
    fun getPurchase(id: Int): Flow<Purchases>

    @Query("SELECT * from purchases ORDER BY id DESC")
    fun getAllPurchases(): Flow<List<Purchases>>

    @Query("DELETE from purchases WHERE billId = :billId")
    suspend fun deletePurchasesByBillId(billId: Int)

    @Query("SELECT * from purchases WHERE billId = :billId")
    fun getPurchasesByBillId(billId: Int): Flow<List<Purchases>>
}