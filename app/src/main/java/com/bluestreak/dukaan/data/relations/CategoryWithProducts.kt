package com.bluestreak.dukaan.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.bluestreak.dukaan.data.entities.Category
import com.bluestreak.dukaan.data.entities.Product

data class CategoryWithProducts(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val products: List<Product>
)