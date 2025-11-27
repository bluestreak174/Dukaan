package com.addendtek.dukaan.data.relations

import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.entities.QuantityType

data class ProductAndQuantity(
    val product: Product,
    val qtyType: QuantityType
)
