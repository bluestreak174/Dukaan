package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.data.relations.CategoryQuantity
import com.bluestreak.dukaan.data.relations.ProductCategoryQuantity
import kotlinx.coroutines.flow.Flow

@Dao
interface  ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product) : Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * from product_master where id = :id")
    fun getProduct(id: Int): Flow<Product>

    @Query("SELECT * from product_master ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Transaction
    @Query( "SELECT * " +
            " from product_master " +
            "INNER JOIN qty_type_master On qty_type_master.id = product_master.qtyTypeId" +
            " ORDER BY product_master.name "
    )
    fun getProductAndQuantity() : Flow<Map<Product, QuantityType>>

    @Transaction
    @Query( "SELECT product_master.*" +
            ", category_master.name as categoryName, qty_type_master.type as qtyType, " +
            " qty_type_master.id as qtyTypeId,  qty_type_master.piece as qtyPiece" +
            " from product_master " +
            " INNER JOIN qty_type_master On qty_type_master.id = product_master.qtyTypeId" +
            " INNER JOIN category_master On category_master.id = product_master.categoryId" +
            " WHERE product_master.id = :id" +
            " ORDER BY product_master.name "
    )
    fun getProductCatQuantity(id: Int) : Flow<Map<Product, CategoryQuantity>>
    @Transaction
    @Query( "SELECT * " +
            ", category_master.name as categoryName, qty_type_master.type as qtyType, " +
            " qty_type_master.id as qtyTypeId,  qty_type_master.piece as qtyPiece" +
            " from product_master " +
            " INNER JOIN qty_type_master On qty_type_master.id = product_master.qtyTypeId" +
            " INNER JOIN category_master On category_master.id = product_master.categoryId" +
            " ORDER BY product_master.name "
    )
    fun getProductsCatQuantity() : Flow<Map<Product, CategoryQuantity>>


    @Query("select sum(cost*qty) as stock_value from product_master order by name asc")
    fun getStockValue() : Flow<String>

    @Transaction
    @Query("select * from product_master where categoryId = :catId")
    fun getProductWithCategoryAndQuantityType(catId: Int): Flow<List<ProductCategoryQuantity>>
}