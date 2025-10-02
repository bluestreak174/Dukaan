package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bluestreak.dukaan.data.entities.QuantityType
import kotlinx.coroutines.flow.Flow


@Dao
interface QuantityTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quantityType: QuantityType)

    @Update
    suspend fun update(quantityType: QuantityType)

    @Delete
    suspend fun delete(quantityType: QuantityType)

    @Query("SELECT * from qty_type_master where id = :id")
    fun getQuantityType(id: Int): Flow<QuantityType>

    @Query("SELECT * from qty_type_master ORDER BY type ASC")
    fun getAllQuantityTypes(): Flow<List<QuantityType>>
}