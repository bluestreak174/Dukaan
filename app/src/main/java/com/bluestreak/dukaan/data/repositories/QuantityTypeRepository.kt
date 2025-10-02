package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.entities.QuantityType
import kotlinx.coroutines.flow.Flow

interface QuantityTypeRepository{
    fun getAllQuantityTypesStream(): Flow<List<QuantityType>>
    fun getQuantityTypeStream(id: Int): Flow<QuantityType>
    suspend fun insertQuantityType(quantityType: QuantityType)
    suspend fun deleteQuantityType(quantityType: QuantityType)
    suspend fun updateQuantityType(quantityType: QuantityType)
}