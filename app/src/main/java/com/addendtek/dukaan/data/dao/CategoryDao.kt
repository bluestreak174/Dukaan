package com.addendtek.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.addendtek.dukaan.data.entities.Category
import com.addendtek.dukaan.data.relations.CategoryWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * from category_master where id = :id")
    fun getCategory(id: Int): Flow<Category>

    @Query("SELECT * from category_master ORDER BY id ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM  category_master where id = :id")
    fun getCategoryWithProducts(id: Int): Flow<List<CategoryWithProducts>>
}