package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.PurchaseBill
import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.entities.Sales
import com.bluestreak.dukaan.data.entities.SalesBill
import com.bluestreak.dukaan.data.relations.CategoryQuantity
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.PurchaseBillRepository
import com.bluestreak.dukaan.data.repositories.PurchaseRepository
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import com.bluestreak.dukaan.data.repositories.SalesBillRepository
import com.bluestreak.dukaan.data.repositories.SalesRepository
import com.bluestreak.dukaan.ui.product.ProductDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository,
    private val purchaseRepository: PurchaseRepository,
    private val quantityTypeRepository: QuantityTypeRepository,
    private val salesBillRepository: SalesBillRepository,
    private val purchaseBillRepository: PurchaseBillRepository
) : ViewModel() {
    private val productId: Int = checkNotNull(savedStateHandle[ProductDetailsDestination.productIdArg])
   // val uiState: ProductDetailsUiState by mutableStateOf(ProductDetailsUiState())
    /*val uiState: StateFlow<ProductDetailsUiState> =
        productRepository.getProductStream(productId)
            .filterNotNull()
            .map{
                ProductDetailsUiState(outOfStock = it.qty<=0, productDetails = it.toItemDetails() )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProductDetailsUiState()
            )*/
    fun getProductDetailsUiState(productQtyTypeMap: Map<Product, CategoryQuantity>): ProductDetailsUiState{
        var productDetailsUiState: ProductDetailsUiState = ProductDetailsUiState()
       if(productQtyTypeMap.isNotEmpty()) {
           productDetailsUiState = ProductDetailsUiState(
               productDetails = productQtyTypeMap.keys.first().toItemDetails(),
               outOfStock = productQtyTypeMap.keys.first().qty <= 0,
               categoryQty = productQtyTypeMap.values.first()
           )
       }
       return productDetailsUiState
    }
    var uiState: StateFlow<ProductDetailsUiState> =
        productRepository.getProductCatQuantity(productId)
            .filterNotNull()
            .map {
                getProductDetailsUiState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProductDetailsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun increaseQuantityByOne() {
        viewModelScope.launch {
            val currentItem = uiState.value.productDetails.toProduct()
            val pieceQty = uiState.value.categoryQty.qtyPiece
            val totalPrice = (uiState.value.productDetails.mrp.toDouble() * pieceQty)
            val purchaseBill = PurchaseBill(
                id = 0,
                total = totalPrice,
                cash = totalPrice,
                upi = 0.0,
                billDate = System.currentTimeMillis(),
                isDraft = false,
                billAddress = "Market"
            )
            val insertedBillId = purchaseBillRepository.insertPurchaseBill(purchaseBill)
            val purchases = Purchases(
                id = 0,
                productId = uiState.value.productDetails.id,
                quantityTypeId = uiState.value.productDetails.qtyTypeId,
                categoryId = uiState.value.productDetails.category,
                quantity = 1,
                billId = insertedBillId.toInt(),
                cost = totalPrice,
                purchaseDate = System.currentTimeMillis(),
            )
            val buyQty = uiState.value.categoryQty.qtyPiece
            productRepository.updateProduct(currentItem.copy(qty = currentItem.qty + buyQty))
            purchaseRepository.insertPurchases(purchases)
        }
    }

    fun reduceQuantityByOne() {
        viewModelScope.launch {
            val currentItem = uiState.value.productDetails.toProduct()
            if (currentItem.qty > 0) {
                val pieceQty = uiState.value.categoryQty.qtyPiece
                val totalPrice = (uiState.value.productDetails.mrp.toDouble() * pieceQty)
                val salesBill = SalesBill(
                    id = 0,
                    total = totalPrice,
                    cash = totalPrice,
                    upi = 0.0,
                    billDate = System.currentTimeMillis(),
                    isDraft = false,
                    billAddress = "Market"
                )
                val insertedBillId = salesBillRepository.insertSalesBill(salesBill)
                val sales = Sales(
                    id = 0,
                    productId = uiState.value.productDetails.id,
                    quantityTypeId = uiState.value.productDetails.qtyTypeId,
                    categoryId = uiState.value.productDetails.category,
                    quantity = 1,
                    billId = insertedBillId.toInt(),
                    price = totalPrice,
                    sellDate = System.currentTimeMillis(),
                )
                val sellQty = uiState.value.categoryQty.qtyPiece
                productRepository.updateProduct(currentItem.copy(qty = currentItem.qty - sellQty))
                salesRepository.insertSales(sales)


            }
        }

    }

    suspend fun deleteItem() {
        //productRepository.deleteProduct(uiState.value.productDetails.toProduct())
    }

}

/**
 * UI state for ProductDetailsScreen
 */
data class ProductDetailsUiState(
    val outOfStock: Boolean = true,
    val productDetails: ItemDetails = ItemDetails(),
    val reduceQty: Int = 1,
    val categoryQty: CategoryQuantity = CategoryQuantity("","",0,0)
)

data class ProductQtyTypeState(
    val productQtyTypeMap: Map<Product, CategoryQuantity> = mapOf()
)
