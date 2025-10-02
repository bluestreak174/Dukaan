package com.bluestreak.dukaan.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.relations.ProductQuantity
import com.bluestreak.dukaan.data.relations.PurchaseSales
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductPurchasesDao {
    @Transaction
   /* @Query( "SELECT purchases.*, product_master.name, qty_type_master.type " +
            ", purchases.quantity as quantity, purchases.cost as cost " +
            " from purchases " +
            "INNER JOIN product_master On purchases.productId = product_master.id " +
            "INNER JOIN qty_type_master On qty_type_master.id = purchases.quantityTypeId " +
            "WHERE purchases.purchaseDate >= :startDate and purchases.purchaseDate <= :endDate"
    )*/
    @Query( "SELECT purchases.id, productId, quantityTypeId, purchases.categoryId, cash, upi, billId , product_master.name, qty_type_master.type " +
            ", SUM(purchases.quantity) as quantity, SUM(purchases.cost) as cost " +
            " from purchases " +
            "INNER JOIN product_master On purchases.productId = product_master.id " +
            "INNER JOIN qty_type_master On qty_type_master.id = purchases.quantityTypeId " +
            "WHERE purchases.purchaseDate >= :startDate and purchases.purchaseDate <= :endDate " +
            "GROUP BY product_master.name, qty_type_master.type"
    )
    fun getProductQtyPurchasesBetweenDates(startDate: Long, endDate: Long): Flow<Map<Purchases, ProductQuantity>>


    /*@Query(
        "select productName, categoryName, stock, piece, qtyType, buyQty, cost, sellQty, price from(" +
                "select * from (" +
                "select product_master.name as productName, product_master.qty as stock, qty_type_master.piece as piece, qty_type_master.type as qtyType, category_master.name as categoryName,   \n" +
                "               sum(purchases.quantity) as buyQty, sum(purchases.cost) as cost \n" +
                "               from purchases  \n" +
                "        inner join qty_type_master on purchases.quantityTypeId = qty_type_master.id  \n" +
                "        inner join product_master on purchases.productId = product_master.id  \n" +
                "        inner join category_master on product_master.categoryId = category_master.id" +
                "               WHERE purchases.purchaseDate >= :startDate and purchases.purchaseDate <= :endDate\n" +
                "        group by product_master.name, product_master.qty, qty_type_master.piece, qty_type_master.type  " +
                ") a\n" +
                "        left join (" +
                " select product_master.name as salesProductName,  qty_type_master.type as salesQtyType,   \n" +
                "                sum(sales.quantity) as sellQty, sum(sales.price) as price \n" +
                "                from sales  \n" +
                "        inner join qty_type_master on sales.quantityTypeId = qty_type_master.id  \n" +
                "        inner join product_master on sales.productId = product_master.id  \n" +
                "                                  WHERE sales.sellDate >= :startDate and sales.sellDate <= :endDate\n" +
                "                           group by product_master.name, qty_type_master.type  " +
                " ) b on a.productName = b.salesProductName \n" +
                " )"
    )*/
    @Query(
        "select buyProductName as productName, buyCategory as categoryName, buyStock as stock, buyPiece as  piece, buyQtyType as qtyType,  buyQty, buyCost as cost, " +
        " ifnull(salesQty, 0) as sellQty, ifnull(salesPrice, 0.0 ) as price from " +
        "(select product_master.name as buyProductName, product_master.qty as buyStock, qty_type_master.piece as buyPiece, qty_type_master.type as buyQtyType,  category_master.name as buyCategory, " +
        "                               sum(purchases.quantity) as buyQty, sum(purchases.cost) as buyCost " +
        "                               from purchases   " +
        "                        inner join qty_type_master on purchases.quantityTypeId = qty_type_master.id   " +
        "                        inner join product_master on purchases.productId = product_master.id   " +
        "                        inner join category_master on product_master.categoryId = category_master.id" +
        "                               WHERE purchases.purchaseDate >= :startDate and purchases.purchaseDate <= :endDate " +
        "                        group by product_master.name, product_master.qty, qty_type_master.piece, qty_type_master.type,  category_master.name) a" +
        "                        left join (select product_master.name as salesProductName, product_master.qty as salesStock, qty_type_master.piece as salesPiece,  qty_type_master.type as salesQtyType,  category_master.name as salesCategory,   " +
        "                                                                   sum(sales.quantity) as salesQty, sum(sales.price) as salesPrice" +
        "                                                                   from sales   " +
        "                                                           inner join qty_type_master on sales.quantityTypeId = qty_type_master.id   " +
        "                                                           inner join product_master on sales.productId = product_master.id   " +
        "                                                            inner join category_master on product_master.categoryId = category_master.id" +
        "                                                                                     WHERE sales.sellDate >= :startDate and sales.sellDate <= :endDate " +
        "                                                                              group by product_master.name, qty_type_master.type,  category_master.name  ) b" +
        "                                                                              on a.buyProductName = b.salesProductName" +
        " union" +
        " select   salesProductName as productName, salesCategory as categoryName, salesStock as stock, salesPiece as piece, salesQtyType as qtyType, ifnull(buyQty, 0) as buyQty, ifnull(buyCost, 0.0) as cost," +
        "  salesQty as sellQty, salesPrice as price from" +
        " (select product_master.name as salesProductName, product_master.qty as salesStock, qty_type_master.piece as salesPiece,  qty_type_master.type as salesQtyType,    category_master.name as salesCategory,  " +
        "                                 sum(sales.quantity) as salesQty, sum(sales.price) as salesPrice" +
        "                                 from sales   " +
        "                         inner join qty_type_master on sales.quantityTypeId = qty_type_master.id   " +
        "                         inner join product_master on sales.productId = product_master.id   " +
        "                         inner join category_master on product_master.categoryId = category_master.id" +
        "                                                   WHERE sales.sellDate >= :startDate and sales.sellDate <= :endDate " +
        "                                            group by product_master.name, qty_type_master.type ,  category_master.name  ) b" +
        "                                            left join (select product_master.name as buyProductName, product_master.qty as buyStock, qty_type_master.piece as buyPiece, qty_type_master.type as buyQtyType,  category_master.name as buyCategory,     " +
        "                                                                                      sum(purchases.quantity) as buyQty, sum(purchases.cost) as buyCost " +
        "                                                                                      from purchases   " +
        "                                                                               inner join qty_type_master on purchases.quantityTypeId = qty_type_master.id   " +
        "                                                                               inner join product_master on purchases.productId = product_master.id   " +
        "                                                                               inner join category_master on product_master.categoryId = category_master.id" +
        "                                                                                      WHERE purchases.purchaseDate >= :startDate and purchases.purchaseDate <= :endDate " +
        "                                                                               group by product_master.name, product_master.qty, qty_type_master.piece, qty_type_master.type,  category_master.name) a" +
        "                                                                               on b.salesProductName = a.buyProductName where a.buyProductName is null"
    )
    fun getPurchasesAndSalesBetweenDates(startDate: Long, endDate: Long): Flow<List<PurchaseSales>>
}

