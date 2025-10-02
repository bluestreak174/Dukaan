package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Sales
import com.bluestreak.dukaan.data.relations.ProductQuantity
import com.bluestreak.dukaan.data.repositories.ProductSalesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class SalesListViewModel(
    private val productSalesRepository: ProductSalesRepository
) : ViewModel() {
    private val startEndDateState = MutableStateFlow(StartEndDateState(
        startDate = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    )

    private lateinit var salesListUiState: Flow<SalesListUIState>
    var salesUiState: SalesListUIState by mutableStateOf(SalesListUIState())
        private set
    private var totalSalesCost: Double = 0.0

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun getSalesListDetails(productSalesListMap: Map<Sales, ProductQuantity>) :  List<SalesListDetails>{
        val salesListDetails: MutableList<SalesListDetails> = mutableListOf()
        totalSalesCost = 0.0
        productSalesListMap.forEach { entry ->
            salesListDetails.add(
                SalesListDetails(
                    sales = entry.key,
                    productQuantity = entry.value
                )
            )
            totalSalesCost += entry.key.price
        }

        return salesListDetails
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSalesListState(productSalesRepository: ProductSalesRepository) :  Flow<SalesListUIState>{
        salesListUiState = startEndDateState.flatMapLatest {
                startEndDate -> productSalesRepository.getProductQtySalesBetweenDates(startEndDate.startDate, startEndDate.endDate)
            .filterNotNull()
            .map {
                SalesListUIState(
                    salesListDetailsList = getSalesListDetails(it),
                    totalCost = totalSalesCost
                )
                //Log.d("Dukaan","${startEndDate.startDate}")
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                replay = 1
            )
        }
        return salesListUiState
    }

    fun updateUiDateState(startTimeStamp: Long, endTimeStamp: Long){
        startEndDateState.value.startDate = getDayStartTimeMillis(startTimeStamp)
        startEndDateState.value.endDate = getDayEndTimeMillis(endTimeStamp)
        viewModelScope.launch {
            salesListUiState = getSalesListState(productSalesRepository)
            salesListUiState.collect {
                salesUiState = SalesListUIState (
                    salesListDetailsList = it.salesListDetailsList,
                    totalCost = totalSalesCost
                )
            }
        }

    }

    init {
        viewModelScope.launch {
            salesListUiState = getSalesListState(productSalesRepository)
            salesListUiState.collect {
                salesUiState = SalesListUIState (
                    salesListDetailsList = it.salesListDetailsList,
                    totalCost = totalSalesCost
                )
            }
        }
    }

}



data class SalesListUIState (
    val salesListDetailsList: List<SalesListDetails> = listOf(),
    val totalCost: Double = 0.0
)

data class SalesListDetails (
    val sales: Sales? = null,
    val productQuantity: ProductQuantity? = null
)