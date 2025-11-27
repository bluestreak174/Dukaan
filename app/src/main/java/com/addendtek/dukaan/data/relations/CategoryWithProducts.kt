package com.addendtek.dukaan.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.addendtek.dukaan.data.entities.Category
import com.addendtek.dukaan.data.entities.Product

data class CategoryWithProducts(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val products: List<Product>
)