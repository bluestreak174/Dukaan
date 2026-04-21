package com.addendtek.dukaan.data.repositories

import com.addendtek.dukaan.data.entities.Category
import com.addendtek.dukaan.data.relations.CategoryWithProducts
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategoriesStream(): Flow<List<Category>>
    fun getCategoryStream(id: Int): Flow<Category>
    suspend fun insertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun updateCategory(category: Category)
    fun getCategoryWithProducts(id: Int): Flow<List<CategoryWithProducts>>
    suspend fun upsertCategory(category: Category): Long
}