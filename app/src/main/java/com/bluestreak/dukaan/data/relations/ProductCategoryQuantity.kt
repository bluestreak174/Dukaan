package com.bluestreak.dukaan.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.bluestreak.dukaan.data.entities.Category
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.QuantityType

data class ProductCategoryQuantity(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category,
    @Relation(
        parentColumn = "qtyTypeId",
        entityColumn = "id"
    )
    val qtyType: QuantityType,
)
