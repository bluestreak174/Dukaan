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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.RoundingMode
import kotlin.math.round

class PurchaseBillViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val purchasesRepository: PurchaseRepository,
    private val purchaseBillRepository: PurchaseBillRepository,
) : ViewModel() {
    var productAndQuantityState: StateFlow<ProductAndQuantityUiState> =
        productRepository.getProductAndQuantity().filterNotNull()
            .map {
                ProductAndQuantityUiState(
                    productAndQuantity = it
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProductAndQuantityUiState(
                    productAndQuantity = mapOf()
                )
            )

    var selectedProductQtyState: SelectedProductQtyState by  mutableStateOf(SelectedProductQtyState())
    var purchaseListState: List<PurchaseProductQty> by mutableStateOf(listOf())
    var billAmountState: BillAmount by mutableStateOf(BillAmount())
    private val purchaseDate: Long = System.currentTimeMillis()
    private var purchaseDateState by mutableLongStateOf(purchaseDate)

    var filteredProductAndQuantity: Map<Product,QuantityType> by mutableStateOf(productAndQuantityState.value.productAndQuantity)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateList(searchText: String){
        if(searchText.isEmpty()){
            filteredProductAndQuantity = productAndQuantityState.value.productAndQuantity
        }else{
            filteredProductAndQuantity = productAndQuantityState.value.productAndQuantity.filter { (key,value) -> key.name.contains(searchText, ignoreCase = true) }
        }

    }

    fun updateSelectedProductQtyState(selectedProduct: PurchaseProductQty){
        if(selectedProduct.qty.isNotBlank() ) {
            val cost: Double = selectedProduct.product?.cost ?: 0.0
            val piece: Int = selectedProduct.qtyType?.piece ?: 0
            val qty: Int = selectedProduct.qty.toInt()
            var typeCost = cost * piece * qty
            typeCost = round(typeCost.toBigDecimal().setScale(2, RoundingMode.UP).toDouble())
            selectedProduct.cost = typeCost.toString()

        }
        selectedProductQtyState = SelectedProductQtyState(
            purchaseProductQty = selectedProduct
        )

    }
    fun updateSelectedProductCost(selectedProduct: PurchaseProductQty){
        selectedProductQtyState = SelectedProductQtyState(
            purchaseProductQty = selectedProduct
        )
    }
    fun updatePurchaseList(purchaseList: List<PurchaseProductQty>){
        purchaseListState = purchaseList
        selectedProductQtyState = SelectedProductQtyState()
        updateBillTotal(purchaseList)
    }

    fun updateProductFromBarCode(barCode: String){
        val product = productAndQuantityState.value.productAndQuantity.keys.find {it.barCode.toString() == barCode}
        val qtyType = productAndQuantityState.value.productAndQuantity.get(product)
        val purchaseProductQty = PurchaseProductQty(
            productName = product?.name ?: "",
            qtyTypeStr = "",
            qty = "1",
            cost = "",
            product = product,
            qtyType = qtyType
        )
        //Log.d("Dukaan", product.toString())
        updateSelectedProductQtyState(purchaseProductQty)
    }

    private fun updateBillTotal(purchaseList: List<PurchaseProductQty>){
        var cost = 0.0
        purchaseList.forEach {
            selectedProduct ->
            cost += selectedProduct.cost.toDouble()
        }
        billAmountState.totalCost = cost
        billAmountState.cash = cost.toString()
        billAmountState.upi = "0.0"
    }

    fun updateBillAmount(billAmount: BillAmount){
        var cash = billAmount.cash
        var upi = billAmount.upi
        /*if(cash.isNotBlank() && cash != "." && upi.isNotBlank() && upi != "."){
            if(
                cash.toDouble() >= 0.0
                && cash.toDouble() + upi.toDouble() != billAmountState.totalCost
                ) upi = "${billAmountState.totalCost - cash.toDouble()}"
        }*/
        if(billAmountState.totalCost > 0
            && cash.toDoubleOrNull() != null
            && upi.toDoubleOrNull() != null) {
            //default cash value is edited so calculate upi
            if (cash != billAmountState.cash) {
                upi = "${billAmountState.totalCost - cash.toDouble()}"
            }
            //default upi value is edited so calculate cash
            if (upi != billAmountState.upi) {
                cash = "${billAmountState.totalCost - upi.toDouble()}"
            }
        }else {
            cash = billAmountState.cash
            upi = billAmountState.upi
        }
        billAmountState = BillAmount(
            totalCost = billAmountState.totalCost,
            cash = cash,
            upi = upi,
            billAddress = billAmount.billAddress
        )
    }

    fun updatePurchaseDateState(purchaseDate: Long){
        purchaseDateState = purchaseDate
    }

    suspend fun savePurchaseBill() {
        var cash = 0.0
        var upi = 0.0
        val total = billAmountState.totalCost
        if(billAmountState.cash.isNotBlank() && billAmountState.upi.isNotBlank()){
            cash = billAmountState.cash.toDouble()
            upi = billAmountState.upi.toDouble()
        }
        val purchaseBill = PurchaseBill(
            id = 0,
            cash = cash,
            upi = upi,
            total = total,
            billDate = purchaseDateState,
            isDraft = false,
            billAddress = billAmountState.billAddress
        )
        //Log.d("Dukaan","Before inserting bill total ${purchaseBill.total}")
        val insertedBillId = purchaseBillRepository.insertPurchaseBill(purchaseBill)
        //Log.d("Dukaan","After inserting total $total")
        //Log.d("Dukaan","After inserting inserted id $insertedBillId")
        purchaseListState.forEach { purchaseProductQty ->
            val product = getProductFromPurchaseBillState(purchaseProductQty)
            val purchases = getPurchasesFromPurchaseBillState(purchaseProductQty, insertedBillId)
            productRepository.updateProduct(product)
            purchasesRepository.insertPurchases(purchases)
        }
        purchaseListState = listOf()
        selectedProductQtyState = SelectedProductQtyState()
        billAmountState = BillAmount()
    }

    private fun getProductFromPurchaseBillState(purchaseProductQty: PurchaseProductQty) : Product{
        val qTypePiece = purchaseProductQty.qtyType?.piece?:1
        val pieceQty = purchaseProductQty.qty.toInt() * qTypePiece
        var pieceCost = purchaseProductQty.cost.toDouble() / pieceQty
        pieceCost = pieceCost.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        var mrp = pieceCost*(10/100) + pieceCost
        mrp = mrp.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

        val qtyBal = purchaseProductQty.product?.qty?:0
        val totalQty = qtyBal + pieceQty

        return Product(
            id = purchaseProductQty.product?.id?:0,
            name = purchaseProductQty.productName,
            categoryId = purchaseProductQty.product?.categoryId?:0,
            qtyTypeId = purchaseProductQty.product?.qtyTypeId?:0,
            qty = totalQty,
            cost = pieceCost,
            mrp = purchaseProductQty.product?.mrp?:mrp
        )
    }
    private fun getPurchasesFromPurchaseBillState(purchaseProductQty: PurchaseProductQty, billId: Long) : Purchases{
        return Purchases(
            id = 0,
            productId = purchaseProductQty.product?.id?:0,
            quantityTypeId = purchaseProductQty.product?.qtyTypeId?:0,
            categoryId = purchaseProductQty.product?.categoryId?:0,
            quantity = purchaseProductQty.qty.toInt(),
            cost = purchaseProductQty.cost.toDouble(),
            cash = 0.0,
            upi = 0.0,
            billId = billId.toInt(),
            purchaseDate = purchaseDateState,
        )

    }

}

data class ProductAndQuantityUiState(
    var productAndQuantity: Map<Product, QuantityType>
)
data class SelectedProductQtyState(
    var purchaseProductQty: PurchaseProductQty  = PurchaseProductQty(),
)

data class PurchaseProductQty(
    var productName: String = "",
    var qtyTypeStr: String = "",
    var qty: String = "1",
    var cost: String = "",
    var product: Product? = null,
    var qtyType: QuantityType? = null
)

data class BillAmount(
    var totalCost: Double = 0.0,
    var cash: String ="0.0",
    var upi: String = "0.0",
    var billAddress: String = "Market"
)


