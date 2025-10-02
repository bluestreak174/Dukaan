package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.dao.CategoryDao
import com.bluestreak.dukaan.data.entities.Category
import com.bluestreak.dukaan.data.relations.CategoryWithProducts
import kotlinx.coroutines.flow.Flow

class OfflineCategoryRepository(private val categoryDao: CategoryDao) : CategoryRepository {
    override fun getAllCategoriesStream(): Flow<List<Category>> = categoryDao.getAllCategories()

    override fun getCategoryStream(id: Int): Flow<Category>  = categoryDao.getCategory(id)

    override suspend fun insertCategory(category: Category) = categoryDao.insert(category)

    override suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    override suspend fun updateCategory(category: Category) = categoryDao.update(category)

    override fun getCategoryWithProducts(id: Int): Flow<List<CategoryWithProducts>> = categoryDao.getCategoryWithProducts(id)
}