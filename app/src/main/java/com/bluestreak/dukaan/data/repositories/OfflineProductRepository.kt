package com.bluestreak.dukaan.data.repositories

import com.bluestreak.dukaan.data.dao.ProductDao
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.data.relations.CategoryQuantity
import com.bluestreak.dukaan.data.relations.ProductCategoryQuantity
import kotlinx.coroutines.flow.Flow

class OfflineProductRepository(private val productDao: ProductDao) : ProductRepository {
    override fun getAllProductsStream(): Flow<List<Product>> = productDao.getAllProducts()

    override fun getProductStream(id: Int): Flow<Product> = productDao.getProduct(id)

    override suspend fun insertProduct(product: Product) = productDao.insert(product)
    override suspend fun deleteProduct(product: Product) = productDao.delete(product)
    override suspend fun updateProduct(product: Product) = productDao.update(product)
    override fun getProductAndQuantity(): Flow<Map<Product, QuantityType>> =
        productDao.getProductAndQuantity()

    override fun getProductCatQuantity(id: Int): Flow<Map<Product, CategoryQuantity>> =
        productDao.getProductCatQuantity(id)
    override fun getProductsCatQuantity() : Flow<Map<Product, CategoryQuantity>> =
        productDao.getProductsCatQuantity()

    override fun getStockValue(): Flow<String> = productDao.getStockValue()
    override fun getProductWithCategoryAndQuantityType(catId: Int): Flow<List<ProductCategoryQuantity>> =
        productDao.getProductWithCategoryAndQuantityType(catId)


}