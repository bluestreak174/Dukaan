package com.bluestreak.dukaan.data.relations

import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.QuantityType

data class ProductAndQuantity(
    val product: Product,
    val qtyType: QuantityType
)
