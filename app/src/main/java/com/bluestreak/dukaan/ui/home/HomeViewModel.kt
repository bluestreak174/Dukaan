package com.bluestreak.dukaan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Category
import com.bluestreak.dukaan.data.relations.TotalBill
import com.bluestreak.dukaan.data.repositories.CategoryRepository
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.PurchaseBillRepository
import com.bluestreak.dukaan.data.repositories.SalesBillRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel(
    categoryRepository: CategoryRepository,
    purchaseBillRepository: PurchaseBillRepository,
    salesBillRepository: SalesBillRepository,
    productRepository: ProductRepository
): ViewModel() {
    private val initial: LocalDate = LocalDate.now(ZoneId.systemDefault())
    private val startOfMonth: LocalDate = initial.withDayOfMonth(1)
    val endOfMonth: LocalDate = initial.withDayOfMonth(initial.month.length(initial.isLeapYear))

    private val startTimeStamp: Long = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val endTimeStamp: Long = endOfMonth.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val homeUiState: StateFlow<HomeUiState> =
        categoryRepository.getAllCategoriesStream().map{ HomeUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val purchasesUiState: StateFlow<PurchasesUiState> =
        purchaseBillRepository.getBillBetweenDates(startTimeStamp, endTimeStamp)
            .map { PurchasesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PurchasesUiState()
            )

    val salesUiState: StateFlow<SalesUiState> =
        salesBillRepository.getBillBetweenDates(startTimeStamp, endTimeStamp)
            .map { SalesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SalesUiState()
            )
    val stockValueUiState: StateFlow<String> =
        productRepository.getStockValue().map{it}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ""
            )

}
/**
 * Ui State for HomeScreen
 */
data class HomeUiState(
    val categoryList: List<Category> = listOf()
)

data class PurchasesUiState(
    val totalBill: TotalBill = TotalBill()
)

data class SalesUiState(
    val totalBill: TotalBill = TotalBill()
)
