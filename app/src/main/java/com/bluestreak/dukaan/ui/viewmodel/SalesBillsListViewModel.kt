package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Sales
import com.bluestreak.dukaan.data.entities.SalesBill
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import com.bluestreak.dukaan.data.repositories.SalesBillRepository
import com.bluestreak.dukaan.data.repositories.SalesRepository
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

class SalesBillsListViewModel(
    private val salesBillRepository: SalesBillRepository,
    private val salesRepository: SalesRepository,
    private val productRepository: ProductRepository,
    private val quantityTypeRepository: QuantityTypeRepository
) : ViewModel()  {
    private val startEndDateState = MutableStateFlow(
        StartEndDateState(
            startDate = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )

    private val _selectedDateRangeState = MutableStateFlow(StartEndDateState())
    val selectedDateRangeState: StateFlow<StartEndDateState> = _selectedDateRangeState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    var billsSalesUiState: StateFlow<BillsSalesState> =
        _selectedDateRangeState.flatMapLatest {
            startEndDate -> salesBillRepository.getAllSalesBillsByDate(startEndDate.startDate, startEndDate.endDate)
        .filterNotNull()
        .map {
            BillsSalesState(
                billsSalesList = it
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = BillsSalesState()
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiDateState(startTimeStamp: Long, endTimeStamp: Long) {
        startEndDateState.value.startDate = getDayStartTimeMillis(startTimeStamp)
        startEndDateState.value.endDate = getDayEndTimeMillis(endTimeStamp)
        _selectedDateRangeState.value = StartEndDateState(
            startDate = getDayStartTimeMillis(startTimeStamp),
            endDate = getDayEndTimeMillis(endTimeStamp)
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

    fun deleteSalesBill(salesBill: SalesBill){
        viewModelScope.launch {
            val salesState: Flow<List<Sales>> = salesRepository.getSalesByBillId(salesBill.id)

            salesState.collect {
                val sales = it
                sales.forEach { sale ->
                    var productUiState = getProduct(sale.productId)
                    //decrease quantity in product_master before deleting purchase
                    var qty = productUiState.itemDetails.qty.toInt()

                    val qtyTypeUiState = getQtyType(sale.quantityTypeId)
                    qty += sale.quantity * qtyTypeUiState.qtyDetails.piece.toInt()

                    productUiState =
                        ProductUiState(
                            itemDetails = productUiState.itemDetails.copy(qty = qty.toString()),
                            isEntryValid = true
                        )

                    productRepository.updateProduct(productUiState.itemDetails.toProduct())
                }
                salesRepository.deleteSalesByBillId(salesBill.id)
                salesBillRepository.deleteSalesBill(salesBill)
            }

        }
    }

}


data class BillsSalesState(
    val billsSalesList: List<SalesBill> = listOf()
)