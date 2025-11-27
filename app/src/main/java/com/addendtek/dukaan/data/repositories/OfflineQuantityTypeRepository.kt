package com.addendtek.dukaan.data.repositories

import com.addendtek.dukaan.data.dao.QuantityTypeDao
import com.addendtek.dukaan.data.entities.QuantityType
import kotlinx.coroutines.flow.Flow

class OfflineQuantityTypeRepository(private val quantityTypeDao: QuantityTypeDao): QuantityTypeRepository {
    override fun getAllQuantityTypesStream(): Flow<List<QuantityType>> = quantityTypeDao.getAllQuantityTypes()

    override fun getQuantityTypeStream(id: Int): Flow<QuantityType> = quantityTypeDao.getQuantityType(id)

    override suspend fun insertQuantityType(quantityType: QuantityType) = quantityTypeDao.insert(quantityType)

    override suspend fun deleteQuantityType(quantityType: QuantityType) = quantityTypeDao.delete(quantityType)

    override suspend fun updateQuantityType(quantityType: QuantityType) = quantityTypeDao.update(quantityType)
}