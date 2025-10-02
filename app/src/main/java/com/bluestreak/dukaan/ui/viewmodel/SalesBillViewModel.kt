package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.data.entities.Sales
import com.bluestreak.dukaan.data.entities.SalesBill
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.SalesBillRepository
import com.bluestreak.dukaan.data.repositories.SalesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.RoundingMode
import kotlin.math.round

class SalesBillViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository,
    private val salesBillRepository: SalesBillRepository,
) : ViewModel() {
    val productAndQuantityState: StateFlow<ProductAndQuantityUiState> =
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

    var filteredProductAndQuantity: Map<Product,QuantityType> by mutableStateOf(productAndQuantityState.value.productAndQuantity)

    var selectedItemQtyState: SelectedItemQtyState by  mutableStateOf(SelectedItemQtyState())
    var salesListState: List<SalesProductQty> by mutableStateOf(listOf())
    var billAmountState: BillAmount by mutableStateOf(BillAmount())
    private val sellDate: Long = System.currentTimeMillis()
    private var salesDateState by mutableLongStateOf(sellDate)

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

    fun updateSelectedProductQtyState(selectedProduct: SalesProductQty){

        if(selectedProduct.qty.isNotBlank()  && selectedProduct.product?.qty!! > 0) {
            val mrp: Double = selectedProduct.product?.mrp ?: 0.0
            val piece: Int = selectedProduct.qtyType?.piece ?: 0
            val qty: Int = selectedProduct.qty.toInt()
            var typePrice = mrp * piece * qty
            typePrice = round(typePrice.toBigDecimal().setScale(2, RoundingMode.UP).toDouble())
            selectedProduct.price = typePrice.toString()

        }
        selectedItemQtyState = SelectedItemQtyState(
            salesProductQty = selectedProduct
        )

    }

    fun updateSelectedProductCost(selectedProduct: SalesProductQty){
        selectedItemQtyState = SelectedItemQtyState(
            salesProductQty = selectedProduct
        )
    }
    fun updateSalesList(salesList: List<SalesProductQty>){
        salesListState = salesList
        selectedItemQtyState = SelectedItemQtyState()
        updateBillTotal(salesList)
    }

    fun updateProductFromBarCode(barCode: String){
        val product = productAndQuantityState.value.productAndQuantity.keys.find {it.barCode.toString() == barCode}
        val qtyType = productAndQuantityState.value.productAndQuantity.get(product)
        val salesProductQty = SalesProductQty(
            productName = product?.name ?: "",
            qtyTypeStr = "",
            qty = "1",
            cost = "",
            product = product,
            qtyType = qtyType
        )
        //Log.d("Dukaan", product.toString())
        updateSelectedProductQtyState(salesProductQty)
    }

    private fun updateBillTotal(salesList: List<SalesProductQty>){
        var cost = 0.0
        salesList.forEach {
                selectedProduct ->
            if(selectedProduct.price.isNotBlank() && selectedProduct.qty.toInt() > 0)
                cost += selectedProduct.price.toDouble()
        }
        billAmountState.totalCost = cost
        billAmountState.cash = cost.toString()
        billAmountState.upi = "0.0"
    }

    fun updateBillAmount(billAmount: BillAmount){
        val cash = billAmount.cash
        var upi = billAmount.upi
        if(cash.isNotBlank() && cash != "." && upi.isNotBlank() && upi != "."){
            if(
                cash.toDouble() >= 0.0
                && cash.toDouble() + upi.toDouble() != billAmountState.totalCost
            ) upi = "${billAmountState.totalCost - cash.toDouble()}"
        }
        billAmountState = BillAmount(
            totalCost = billAmountState.totalCost,
            cash = cash,
            upi = upi,
            billAddress = billAmount.billAddress
        )
    }

    fun updateSalesDateState(purchaseDate: Long){
        salesDateState = purchaseDate
    }

    suspend fun saveSalesBill() {
        var cash = 0.0
        var upi = 0.0
        val total = billAmountState.totalCost
        if(billAmountState.cash.isNotBlank() && billAmountState.upi.isNotBlank()){
            cash = billAmountState.cash.toDouble()
            upi = billAmountState.upi.toDouble()
        }
        val salesBill = SalesBill(
            id = 0,
            cash = cash,
            upi = upi,
            total = total,
            billDate = salesDateState,
            isDraft = false,
            billAddress = billAmountState.billAddress
        )
        //Log.d("Dukaan","Before inserting bill total ${salesBill.total}")
        val insertedBillId = salesBillRepository.insertSalesBill(salesBill)
        //Log.d("Dukaan","After inserting total $total")
       // Log.d("Dukaan","After inserting inserted id $insertedBillId")
        salesListState.forEach { salesProductQty ->
            val product = getProductFromSalesBillState(salesProductQty)
            val sales = getSalesFromSalesBillState(salesProductQty, insertedBillId)
            productRepository.updateProduct(product)
            salesRepository.insertSales(sales)
        }
        salesListState = listOf()
        selectedItemQtyState = SelectedItemQtyState()
        billAmountState = BillAmount()
    }

    private fun getProductFromSalesBillState(salesProductQty: SalesProductQty) : Product{
        val qTypePiece = salesProductQty.qtyType?.piece?:1
        val pieceQty = salesProductQty.qty.toInt() * qTypePiece
        var piecePrice = salesProductQty.price.toDouble() / pieceQty
        piecePrice = piecePrice.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        val qtyBal = salesProductQty.product?.qty!!
        val totalQty = qtyBal - pieceQty


        return Product(
            id = salesProductQty.product?.id?:0,
            name = salesProductQty.productName,
            categoryId = salesProductQty.product?.categoryId?:0,
            qtyTypeId = salesProductQty.product?.qtyTypeId?:0,
            qty = totalQty,
            cost = salesProductQty.product?.cost!!,
            mrp = salesProductQty.product?.mrp?:piecePrice
        )
    }

    private fun getSalesFromSalesBillState(salesProductQty: SalesProductQty, billId: Long) : Sales {
        return Sales(
            id = 0,
            productId = salesProductQty.product?.id?:0,
            quantityTypeId = salesProductQty.product?.qtyTypeId?:0,
            categoryId = salesProductQty.product?.categoryId?:0,
            quantity = salesProductQty.qty.toInt(),
            price = salesProductQty.price.toDouble(),
            cash = 0.0,
            upi = 0.0,
            billId = billId.toInt(),
            sellDate = salesDateState,
        )

    }

}

data class SelectedItemQtyState(
    var salesProductQty: SalesProductQty  = SalesProductQty(),
)

data class SalesProductQty(
    var productName: String = "",
    var qtyTypeStr: String = "",
    var qty: String = "1",
    var cost: String = "",
    var price: String = "",
    var product: Product? = null,
    var qtyType: QuantityType? = null
)