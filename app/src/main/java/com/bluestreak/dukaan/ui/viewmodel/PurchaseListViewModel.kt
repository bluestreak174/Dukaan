package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.relations.ProductQuantity
import com.bluestreak.dukaan.data.repositories.ProductPurchaseRepository
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
import java.util.Calendar
import java.util.TimeZone


class PurchaseListViewModel(
    private val productPurchaseRepository: ProductPurchaseRepository,
) : ViewModel() {
    //val startTimeStamp: Long = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    //val endTimeStamp: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()


    private val startEndDateState = MutableStateFlow(StartEndDateState(
         startDate = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
         endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )


    private lateinit var purchaseListUiState: Flow<PurchaseListUIState>
    var purchaseUiState: PurchaseListUIState by mutableStateOf(PurchaseListUIState())
            private set
    var totalPurchasesCost: Double = 0.0

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun getPurchaseListDetails(productPurchaseListMap: Map<Purchases, ProductQuantity>) :  List<PurchaseListDetails>{
        val purchaseListDetails: MutableList<PurchaseListDetails> = mutableListOf()
        totalPurchasesCost = 0.0
        productPurchaseListMap.forEach { entry ->
            purchaseListDetails.add(
                PurchaseListDetails(
                    purchase = entry.key,
                    productQuantity = entry.value
                )
            )
            totalPurchasesCost += entry.key.cost
        }

        return purchaseListDetails
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPurchaseListState(productPurchaseRepository: ProductPurchaseRepository) :  Flow<PurchaseListUIState>{
        purchaseListUiState = startEndDateState.flatMapLatest {
                startEndDate -> productPurchaseRepository
                    .getProductQtyPurchasesBetweenDates(startEndDate.startDate, startEndDate.endDate)
            .filterNotNull()
            .map {
                //purchaseListDetails = getPurchaseListDetails(it)
                PurchaseListUIState(
                    purchaseListDetailsList = getPurchaseListDetails(it),
                    totalCost = totalPurchasesCost
                )
                //Log.d("Dukaan","${startEndDate.startDate}")
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                replay = 1
            )
        }
        return purchaseListUiState
    }

    fun updateUiDateState(startTimeStamp: Long, endTimeStamp: Long){
        startEndDateState.value.startDate = getDayStartTimeMillis(startTimeStamp)
        startEndDateState.value.endDate = getDayEndTimeMillis(endTimeStamp)
        viewModelScope.launch {
            purchaseListUiState = getPurchaseListState(productPurchaseRepository)
            purchaseListUiState.collect {
                purchaseUiState = PurchaseListUIState (
                    purchaseListDetailsList = it.purchaseListDetailsList,
                    totalCost = totalPurchasesCost
                )
            }
        }

    }

    init {
        viewModelScope.launch {
            purchaseListUiState = getPurchaseListState(productPurchaseRepository)
            purchaseListUiState.collect {
                purchaseUiState = PurchaseListUIState (
                    purchaseListDetailsList = it.purchaseListDetailsList,
                    totalCost = totalPurchasesCost
                )
            }
        }
    }


}

data class PurchaseListUIState (
    val purchaseListDetailsList: List<PurchaseListDetails> = listOf(),
    val totalCost: Double = 0.0
)

data class PurchaseListDetails (
    val purchase: Purchases? = null,
    val productQuantity: ProductQuantity? = null
)

data class StartEndDateState (
    var startDate: Long = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    var endDate: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
)

fun getDayStartTimeMillis(milliseconds: Long, timeZone: TimeZone = TimeZone.getDefault()): Long {
    val calendar = Calendar.getInstance(timeZone)
    calendar.timeInMillis = milliseconds
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
fun getDayEndTimeMillis(milliseconds: Long, timeZone: TimeZone = TimeZone.getDefault()): Long {
    val calendar = Calendar.getInstance(timeZone)
    calendar.timeInMillis = milliseconds
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}