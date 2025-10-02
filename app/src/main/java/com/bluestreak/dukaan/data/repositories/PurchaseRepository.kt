package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.entities.Purchases
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    fun getAllPurchasesStream(): Flow<List<Purchases>>
    fun getPurchaseStream(id: Int): Flow<Purchases>
    suspend fun insertPurchases(purchases: Purchases)
    suspend fun deletePurchases(purchases: Purchases)
    suspend fun updatePurchases(purchases: Purchases)
    suspend fun deletePurchasesByBillId(billId: Int)

    fun getPurchasesByBillId(billId: Int): Flow<List<Purchases>>
}