package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.PurchaseBill
import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.PurchaseBillRepository
import com.bluestreak.dukaan.data.repositories.PurchaseRepository
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class PurchaseBillsListViewModel(
    private val purchaseBillRepository: PurchaseBillRepository,
    private val purchaseRepository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val quantityTypeRepository: QuantityTypeRepository
) : ViewModel() {
    private val startEndDateState = MutableStateFlow(
        StartEndDateState(
            startDate = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )

    private val _selectedDateRangeState = MutableStateFlow(StartEndDateState())
    val selectedDateRangeState: StateFlow<StartEndDateState> = _selectedDateRangeState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val billsPurchaseUiState: StateFlow<BillsPurchaseState> =
        _selectedDateRangeState.flatMapLatest {
            startEndDate -> purchaseBillRepository.getAllPurchaseBillsByDate(startEndDate.startDate, startEndDate.endDate)
            .filterNotNull()
            .map {
                BillsPurchaseState(
                    billsPurchaseList = it,
                )
            }
        } .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = BillsPurchaseState()
        )

    private val _filteredBillsPurchaseState = MutableStateFlow(FilteredBillsPurchaseState())
    val filteredBillsPurchaseState: StateFlow<FilteredBillsPurchaseState> = _filteredBillsPurchaseState.asStateFlow()


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    init {
        viewModelScope.launch {
            billsPurchaseUiState.collect{ billsPurchaseState ->
                _filteredBillsPurchaseState.value = FilteredBillsPurchaseState(
                    filteredBillsList = billsPurchaseState.billsPurchaseList,
                    billAddressFilterMap = billsPurchaseState.billsPurchaseList.map { purchaseBill ->  purchaseBill.billAddress }.toSet().associate { it to true }
                )
            }
        }
    }

    fun updateUiDateState(startTimeStamp: Long, endTimeStamp: Long){
        _filteredBillsPurchaseState.value = FilteredBillsPurchaseState(
            filteredBillsList = billsPurchaseUiState.value.billsPurchaseList,
            billAddressFilterMap = billsPurchaseUiState.value.billsPurchaseList.map { purchaseBill ->  purchaseBill.billAddress }.toSet().associate { it to true }
        )

        startEndDateState.value.startDate = getDayStartTimeMillis(startTimeStamp)
        startEndDateState.value.endDate = getDayEndTimeMillis(endTimeStamp)
        _selectedDateRangeState.value = StartEndDateState(
            startDate = getDayStartTimeMillis(startTimeStamp),
            endDate = getDayEndTimeMillis(endTimeStamp)
        )

    }

    fun updateFilterData(billAddressMap: Map<String, Boolean>){
        val filteredList: List<PurchaseBill> = billsPurchaseUiState.value.billsPurchaseList.filter { purchaseBill ->
            val addressChecked = billAddressMap[purchaseBill.billAddress]
            addressChecked == true
        }

        _filteredBillsPurchaseState.value = FilteredBillsPurchaseState(
            filteredBillsList = filteredList,
            billAddressFilterMap = billAddressMap
        )

    }

    private suspend fun getProduct(productId: Int): ProductUiState{
        val productUiState = productRepository.getProductStream(productId)
            .filterNotNull()
            .first()
            .toProductUiState(true)
        return productUiState
    }
    suspend fun getQtyType(qtyTypeId: Int): QtyUiState{
        val qtyTypeUiState = quantityTypeRepository.getQuantityTypeStream(qtyTypeId)
            .filterNotNull()
            .first()
            .toQtyUiState()
        return qtyTypeUiState
    }
    fun deletePurchaseBill(purchaseBill: PurchaseBill){
        viewModelScope.launch {
            val purchaseState: Flow<List<Purchases>> = purchaseRepository.getPurchasesByBillId(purchaseBill.id)

            purchaseState.collect{
                val purchases = it
                purchases.forEach {
                        purchase ->
                    var productUiState = getProduct(purchase.productId)
                    //decrease quantity in product_master before deleting purchase
                    var qty = productUiState.itemDetails.qty.toInt()

                    val qtyTypeUiState = getQtyType(purchase.quantityTypeId)
                    qty -= purchase.quantity * qtyTypeUiState.qtyDetails.piece.toInt()

                    productUiState =
                        ProductUiState(itemDetails = productUiState.itemDetails.copy(qty = qty.toString()), isEntryValid = true)

                    productRepository.updateProduct(productUiState.itemDetails.toProduct())
                }

                //delete purchase  before deleting purchase bill
                purchaseRepository.deletePurchasesByBillId(purchaseBill.id)
                purchaseBillRepository.deletePurchaseBill(purchaseBill)
            }

        }
    }

}

data class BillsPurchaseState(
    val billsPurchaseList: List<PurchaseBill> = listOf(),
)

data class FilteredBillsPurchaseState(
    val filteredBillsList: List<PurchaseBill> = listOf(),
    val billAddressFilterMap: Map<String, Boolean> = mapOf()
)