package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.data.relations.CategoryQuantity
import com.bluestreak.dukaan.data.relations.ProductCategoryQuantity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProductsStream(): Flow<List<Product>>
    fun getProductStream(id: Int): Flow<Product>
    suspend fun insertProduct(product: Product) : Long
    suspend fun deleteProduct(product: Product)
    suspend fun updateProduct(product: Product)
    fun getProductAndQuantity(): Flow<Map<Product, QuantityType>>
    fun getProductCatQuantity(id: Int) : Flow<Map<Product, CategoryQuantity>>
    fun getProductsCatQuantity() : Flow<Map<Product, CategoryQuantity>>
    fun getStockValue() : Flow<String>
    fun getProductWithCategoryAndQuantityType(catId: Int) : Flow<List<ProductCategoryQuantity>>
}