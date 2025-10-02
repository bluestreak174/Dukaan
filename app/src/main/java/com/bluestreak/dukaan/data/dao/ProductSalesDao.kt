package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.bluestreak.dukaan.data.entities.Sales
import com.bluestreak.dukaan.data.relations.ProductHistory
import com.bluestreak.dukaan.data.relations.ProductQuantity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductSalesDao {
    @Transaction
   /* @Query( "SELECT sales.*, product_master.name, qty_type_master.type " +
            ", sales.quantity as quantity, sales.price as price " +
            " from sales " +
            "INNER JOIN product_master On sales.productId = product_master.id " +
            "INNER JOIN qty_type_master On qty_type_master.id = sales.quantityTypeId " +
            "WHERE sales.sellDate >= :startDate and sales.sellDate <= :endDate"
    )*/
    @Query( "SELECT sales.id, productId, quantityTypeId, sales.categoryId, cash, upi, billId , product_master.name, qty_type_master.type " +
            ", SUM(sales.quantity) as quantity, SUM(sales.price) as price " +
            " from sales " +
            "INNER JOIN product_master On sales.productId = product_master.id " +
            "INNER JOIN qty_type_master On qty_type_master.id = sales.quantityTypeId " +
            "WHERE sales.sellDate >= :startDate and sales.sellDate <= :endDate " +
            "GROUP BY product_master.name, qty_type_master.type"
    )
    fun getProductQtyPurchasesBetweenDates(startDate: Long, endDate: Long): Flow<Map<Sales, ProductQuantity>>


    @Query("select purchases.billId as billId, product_master.name as name, \"B\" as buySell, purchases.purchaseDate as date, " +
            " qty_type_master.type as qtyType, purchases.quantity as quantity, purchases.cost as amount  from purchases\n" +
            "    inner join product_master on purchases.productId = product_master.id\n" +
            "    inner join qty_type_master on purchases.quantityTypeId = qty_type_master.id\n" +
            "    where product_master.id = :productId \n" +
            "    union all\n" +
            "    select sales.billId as billId, product_master.name as name, \"S\" as buySell, sales.sellDate as date, " +
            " qty_type_master.type as qtyType, sales.quantity as quantity, sales.price as amount  from sales\n" +
            "        inner join product_master on sales.productId = product_master.id\n" +
            "        inner join qty_type_master on sales.quantityTypeId = qty_type_master.id\n" +
            "     where product_master.id = :productId \n" +
            "     order by date desc")
    fun getProductBuySell(productId: Int): Flow<List<ProductHistory>>
}