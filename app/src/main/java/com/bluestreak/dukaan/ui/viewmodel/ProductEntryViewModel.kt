package com.bluestreak.dukaan.ui.viewmodel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.PurchaseBill
import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.PurchaseBillRepository
import com.bluestreak.dukaan.data.repositories.PurchaseRepository
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import com.bluestreak.dukaan.ui.product.ProductEntryDestination
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.RoundingMode
import java.text.NumberFormat

class ProductEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val purchasesRepository: PurchaseRepository,
    private val quantityTypeRepository: QuantityTypeRepository,
    private val purchaseBillRepository: PurchaseBillRepository
) : ViewModel() {
    private val catId: Int = checkNotNull(savedStateHandle[ProductEntryDestination.catIdArg])

    val qtyTypeUiState: StateFlow<QuantityTypeUiState> =
        quantityTypeRepository.getAllQuantityTypesStream()
            .filterNotNull()
            .map {
                QuantityTypeUiState(qtyTypeList = it.toList())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = QuantityTypeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    var productUiState by mutableStateOf(ProductUiState())
        private set

    private val purchaseDate: Long = System.currentTimeMillis()
    private var purchaseDateState by mutableLongStateOf(purchaseDate)

    private fun validateProductInput(uiState: ItemDetails = productUiState.itemDetails) : Boolean{
        return with(uiState) {
            name.isNotBlank()
        }
    }

    private fun validatePurchaseInput(uiState: PurchaseEntryDetails = productUiState.purchaseEntryDetails) : Boolean{

        return with(uiState) {
            cost.isNotBlank() && mrp.isNotBlank() && qty.isNotBlank()
                    && cash.isNotBlank() && upi.isNotBlank() && qty.toInt() > 0
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        productUiState = ProductUiState(
            itemDetails = itemDetails,
            purchaseEntryDetails = productUiState.purchaseEntryDetails,
            isEntryValid = validateProductInput(itemDetails) && validatePurchaseInput(productUiState.purchaseEntryDetails)
        )

    }
    fun updatePurchaseUiState(purchaseEntryDetails: PurchaseEntryDetails) {
        //calculate cash and upi values
        val cash = purchaseEntryDetails.cash
        var upi = purchaseEntryDetails.upi
        //if cash is modified update upi value
        if( productUiState.purchaseEntryDetails.cost.toDoubleOrNull() != null &&
            productUiState.purchaseEntryDetails.cost.toDouble() > 0.0 &&
            purchaseEntryDetails.upi.toDoubleOrNull() != null &&
            purchaseEntryDetails.cash.toDoubleOrNull() != null &&
            purchaseEntryDetails.cash.toDouble() <= productUiState.purchaseEntryDetails.cost.toDouble()
        ) {
            upi = "${productUiState.purchaseEntryDetails.cost.toDouble() - purchaseEntryDetails.cash.toDouble()}"
        } else {
            upi = "0.0"
        }
        productUiState = ProductUiState(
            itemDetails = productUiState.itemDetails,
            purchaseEntryDetails = purchaseEntryDetails.copy(cash = cash, upi = upi),
            isEntryValid = validateProductInput(productUiState.itemDetails) && validatePurchaseInput(purchaseEntryDetails)
        )

    }
    fun updatePurchaseDateState(purchaseDate: Long){
        purchaseDateState = purchaseDate
    }

    suspend fun saveProduct() {
        try {
                val qTypePiece = productUiState.purchaseEntryDetails.qtyType!!.piece
                val pieceQty = productUiState.purchaseEntryDetails.qty.toInt() * qTypePiece
                var pieceCost = productUiState.purchaseEntryDetails.cost.toDouble() / pieceQty
                pieceCost = pieceCost.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                var pieceMrp = productUiState.purchaseEntryDetails.mrp.toDouble() / pieceQty
                pieceMrp = pieceMrp.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                val qtyTypeId = productUiState.purchaseEntryDetails.qtyType!!.id

                updateUiState(
                    ItemDetails(
                        id = 0,
                        name = productUiState.itemDetails.name,
                        cost = pieceCost.toString(),
                        mrp = pieceMrp.toString(),
                        qty = pieceQty.toString(),
                        qtyTypeId = qtyTypeId,
                        category = catId,
                        qtyTypeStr = productUiState.purchaseEntryDetails.qtyType!!.type,
                        barcode = productUiState.itemDetails.barcode
                    )
                )

                if (validateProductInput() && validatePurchaseInput()) {
                    val product = productUiState.itemDetails.toProduct()
                    val insertedProductId = productRepository.insertProduct(product)
                    val cash = productUiState.purchaseEntryDetails.cash.toDouble()
                    val upi = productUiState.purchaseEntryDetails.upi.toDouble()
                    val cost = productUiState.purchaseEntryDetails.cost.toDouble()
                    val purchaseBill = PurchaseBill(
                        id = 0,
                        total = cost,
                        cash = cash,
                        upi = upi,
                        billDate = purchaseDateState,
                        isDraft = false,
                        billAddress = productUiState.purchaseEntryDetails.address
                    )
                    val insertedBillId = purchaseBillRepository.insertPurchaseBill(purchaseBill)
                    val purchases = productUiState.purchaseEntryDetails.toPurchases(
                        insertedProductId,
                        qtyTypeId,
                        purchaseDateState,
                        insertedBillId.toInt()
                    )
                    purchasesRepository.insertPurchases(purchases)
                }

        }catch(exception: NumberFormatException){
            Log.d("Dukaan",exception.toString())
        }
    }

    init {
        updateUiState(
            ItemDetails(
                id = 0,
                name = "",
                cost = "0.0",
                mrp = "0.0",
                qty = "1",
                qtyTypeId = 0,
                category = catId,
                qtyTypeStr = "",
                barcode = ""
            )
        )

        updatePurchaseUiState(
            PurchaseEntryDetails(
                id = 0,
                productId = 0,
                qtyTypeId = "",
                catId = catId.toString(),
                qty = "1",
                cost = "0.0",
                mrp = "0.0",
                cash = "0.0",
                upi = "0.0",
                purchaseDate = "",
            )
        )
    }

}

data class QuantityTypeUiState(
    val qtyTypeList: List<QuantityType> = listOf()
)

data class ProductUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val purchaseEntryDetails: PurchaseEntryDetails = PurchaseEntryDetails(),
    val isEntryValid: Boolean = false,
)
data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val cost: String = "",
    val mrp: String = "",
    val qty: String = "",
    val qtyTypeId: Int = 0,
    val category: Int = 0,
    val qtyTypeStr: String = "",
    val barcode: String = ""
)

data class PurchaseEntryDetails(
    val id: Int = 0,
    val productId: Int = 0,
    val qtyTypeId: String = "",
    val catId: String = "",
    val qty: String = "",
    val cost: String = "",
    val mrp: String = "",
    val cash: String = "0.0",
    val upi: String = "0.0",
    val purchaseDate: String = "",
    val qtyType: QuantityType? = null,
    val address: String = "Market"
)

fun ItemDetails.toProduct(): Product = Product(
    id = id,
    name = name,
    cost = cost.toDoubleOrNull() ?: 0.0,
    mrp = mrp.toDoubleOrNull() ?: 0.0,
    qty = qty.toIntOrNull() ?: 0,
    qtyTypeId = qtyTypeId,
    categoryId = category,
    barCode = barcode.toLongOrNull() ?: 0
)

fun PurchaseEntryDetails.toPurchases(productId: Long,qTypeId: Int,purchaseDateState: Long, billId: Int): Purchases = Purchases(
    id = 0,
    productId = productId.toInt(),
    quantityTypeId = qTypeId,
    categoryId = catId.toInt(),
    quantity = qty.toIntOrNull() ?: 0,
    cost = cost.toDoubleOrNull() ?: 0.0,
    cash = cash.toDoubleOrNull() ?: 0.0,
    upi = upi.toDoubleOrNull() ?: 0.0,
    billId = billId,
    purchaseDate = purchaseDateState,
)

fun Product.formatedPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

fun Product.toProductUiState(isEntryValid: Boolean = false): ProductUiState = ProductUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

fun Product.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    cost = cost.toString(),
    mrp = mrp.toString(),
    qty = qty.toString(),
    qtyTypeId = qtyTypeId,
    category = categoryId,
    barcode = barCode.toString()
)
