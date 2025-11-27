package com.addendtek.dukaan.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.addendtek.dukaan.data.entities.Category
import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.entities.QuantityType

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
