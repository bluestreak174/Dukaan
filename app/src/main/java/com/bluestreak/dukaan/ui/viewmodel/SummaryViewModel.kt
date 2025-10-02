package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.relations.TotalBill
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.PurchaseBillRepository
import com.bluestreak.dukaan.data.repositories.SalesBillRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class SummaryViewModel(
    private val purchaseBillRepository: PurchaseBillRepository,
    private val salesBillRepository: SalesBillRepository,
    private val productRepository: ProductRepository
)  : ViewModel() {
    private val startEndDateState = MutableStateFlow(StartEndDateState(
        startDate = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    )
    private lateinit var purchaseBillState: Flow<PurchaseBillState>
    var purchaseUiState: PurchaseBillState by mutableStateOf(PurchaseBillState(null))
        private set
    private lateinit var salesBillState: Flow<SalesBillState>
    var salesUiState: SalesBillState by mutableStateOf(SalesBillState(null))
        private set
    /*var purchaseBillState: StateFlow<PurchaseBillState> =
        purchaseBillRepository.getBillBetweenDates(startEndDateState.value.startDate, startEndDateState.value.endDate)
            .filterNotNull()
            .map {
                PurchaseBillState(
                   purchaseBill = it
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PurchaseBillState(null)
            )*/
    /*var salesBillState: StateFlow<SalesBillState> =
        salesBillRepository.getBillBetweenDates(startEndDateState.value.startDate, startEndDateState.value.endDate)
            .filterNotNull()
            .map {
                SalesBillState(
                    salesBill = it
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SalesBillState(null)
            )*/

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var stockUiState = productRepository.getStockValue()
        .map { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPurchaseBillState(purchaseBillRepository: PurchaseBillRepository) : Flow<PurchaseBillState> {
        purchaseBillState =
            startEndDateState.flatMapLatest {
                    startEndDate -> purchaseBillRepository.getBillBetweenDates(
                        startEndDate.startDate,
                        startEndDate.endDate
                    )
                    .filterNotNull()
                    .map {
                        PurchaseBillState(
                            purchaseBill = it
                        )
                    }.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                        initialValue = PurchaseBillState(null)
                    )
            }
        return purchaseBillState
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSalesBillState(salesBillRepository: SalesBillRepository) : Flow<SalesBillState> {
        salesBillState =
            startEndDateState.flatMapLatest {
                    startEndDate -> salesBillRepository.getBillBetweenDates(
                startEndDate.startDate,
                startEndDate.endDate
            )
                .filterNotNull()
                .map {
                    SalesBillState(
                        salesBill = it
                    )
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = SalesBillState(null)
                )
            }
        return salesBillState
    }

    fun updateUiDateState(startTimeStamp: Long, endTimeStamp: Long) {
        startEndDateState.value.startDate = getDayStartTimeMillis(startTimeStamp)
        startEndDateState.value.endDate = getDayEndTimeMillis(endTimeStamp)
        viewModelScope.launch {
           purchaseBillState = getPurchaseBillState(purchaseBillRepository)
            purchaseBillState.collect {
                purchaseUiState = PurchaseBillState(
                    purchaseBill = it.purchaseBill
                )
            }

        }
        viewModelScope.launch {
            salesBillState = getSalesBillState(salesBillRepository)
            salesBillState.collect {
                salesUiState = SalesBillState(
                    salesBill = it.salesBill
                )
            }
        }

    }
    init {
        viewModelScope.launch {
            purchaseBillState = getPurchaseBillState(purchaseBillRepository)
            purchaseBillState.collect {
                purchaseUiState = PurchaseBillState(
                    purchaseBill = it.purchaseBill
                )
            }
        }
        viewModelScope.launch {
            salesBillState = getSalesBillState(salesBillRepository)
            salesBillState.collect {
                salesUiState = SalesBillState(
                    salesBill = it.salesBill
                )
            }
        }
    }
}

data class PurchaseBillState(
    val purchaseBill: TotalBill?
)
data class SalesBillState(
    val salesBill: TotalBill?
)