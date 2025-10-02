package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.relations.PurchaseSales
import com.bluestreak.dukaan.data.repositories.ProductPurchaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class PurchaseAndSalesViewModel(
    private val productPurchaseRepository: ProductPurchaseRepository
) : ViewModel(){

    private val startEndDateState = MutableStateFlow(StartEndDateState(
        startDate = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )
    private val _selectedDateRangeState = MutableStateFlow(StartEndDateState())
    val selectedDateRangeState: StateFlow<StartEndDateState> = _selectedDateRangeState.asStateFlow()

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _filteredPurchaseSalesState = MutableStateFlow(FilteredPurchaseAndSalesState())
    val filteredPurchaseSalesState = _filteredPurchaseSalesState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val  purchaseSalesUiState: StateFlow<PurchaseAndSalesState> =
        _selectedDateRangeState.flatMapLatest {
                startEndDate -> productPurchaseRepository
            .getPurchasesAndSalesBetweenDates(startEndDate.startDate, startEndDate.endDate)
            .filterNotNull()
            .map {
                PurchaseAndSalesState(
                    purchaseAndSalesList = it,
                    totalProfitAndLoss = getTotalProfitAndLoss(it)
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PurchaseAndSalesState()
        )

    init {
        viewModelScope.launch {
            purchaseSalesUiState.collect { purchaseSalesState ->
                _filteredPurchaseSalesState.value = FilteredPurchaseAndSalesState(
                    filteredPurchaseSalesList = purchaseSalesState.purchaseAndSalesList,
                    categoryFilterMap = purchaseSalesState.purchaseAndSalesList.map { purchaseSales ->  purchaseSales.categoryName }.toSet().associate { it to true },
                    categoryMapOfProductFilterMap =  purchaseSalesState.purchaseAndSalesList.groupBy { it.categoryName } // Group by outer map key
                    .mapValues { (_, subList) -> // Transform each sub-list into an inner map
                        subList.associateBy { it.productName } // Create inner map with productName as key
                            .mapValues { (_, data) -> true } // Extract value for inner map
                    }
                )
            }
        }
    }


    fun updateUiDateState(startTimeStamp: Long, endTimeStamp: Long) {
        startEndDateState.value.startDate = getDayStartTimeMillis(startTimeStamp)
        startEndDateState.value.endDate = getDayEndTimeMillis(endTimeStamp)
        _selectedDateRangeState.value = StartEndDateState(
            startDate = getDayStartTimeMillis(startTimeStamp),
            endDate = getDayEndTimeMillis(endTimeStamp)
        )
        /*val startDate =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(selectedDateRangeState.value.startDate)
        val endDate =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(selectedDateRangeState.value.endDate)
        */
    }

    fun updateCategoryFilterData(categoryFilterMap: MutableMap<String, Boolean>){
        val filteredList = purchaseSalesUiState.value.purchaseAndSalesList.filter{ purchaseSales ->
            val categoryChecked = categoryFilterMap[purchaseSales.categoryName]
            categoryChecked == true
        }

        val mainMap = _filteredPurchaseSalesState.value.categoryMapOfProductFilterMap

        val mutableMainMap = mainMap.mapValues { it.value.toMutableMap() }.toMutableMap()

        for((key, value) in categoryFilterMap) {
            updateAllValuesOfSubMap(mutableMainMap, key, value)
        }


        _filteredPurchaseSalesState.value = FilteredPurchaseAndSalesState(
            filteredPurchaseSalesList = filteredList,
            categoryFilterMap = categoryFilterMap,
            categoryMapOfProductFilterMap = mutableMainMap
        )
    }

    fun updateCategoryProductFilterData(categoryMapOfProductFilterMap: Map<String, Map<String, Boolean>>) {
        val filteredList: MutableList<PurchaseSales> = mutableListOf()
        for(productFilterMap in categoryMapOfProductFilterMap){
            filteredList.addAll(
                purchaseSalesUiState.value.purchaseAndSalesList.filter{ purchaseSales ->
                    val productChecked = productFilterMap.value[purchaseSales.productName]
                    productChecked == true
                }
            )
        }
        val mutableCategoryMap = _filteredPurchaseSalesState.value.categoryFilterMap.toMutableMap()

        for((key, subMap) in categoryMapOfProductFilterMap) {
            for((subkey, value) in subMap) {
                if(!value) mutableCategoryMap[key] = false
                break
            }
        }

        _filteredPurchaseSalesState.value = FilteredPurchaseAndSalesState(
            filteredPurchaseSalesList = filteredList,
            categoryFilterMap = mutableCategoryMap,
            categoryMapOfProductFilterMap = categoryMapOfProductFilterMap
        )
    }

    private fun updateAllValuesOfSubMap(
        mainMap: MutableMap<String, MutableMap<String, Boolean>>,
        mainMapKey: String,
        newValue: Boolean
    ): MutableMap<String, MutableMap<String, Boolean>> {
        // Get the sub-map
        val subMap = mainMap[mainMapKey]

        // Check if the sub-map exists and is mutable
        if (subMap != null) {
            // Iterate through the sub-map's entries and set all values to newValue
            subMap.keys.forEach { key ->
                subMap[key] = newValue
            }
        }
        return mainMap
    }

    private fun getTotalProfitAndLoss(purchaseAndSalesList: List<PurchaseSales>) : Double{
        var total = 0.0
        purchaseAndSalesList.forEach {
                purchaseSales ->
            val itemCost = if(purchaseSales.buyQty == 0) 0.0 else purchaseSales.cost/purchaseSales.buyQty
            val diffBuyQty = purchaseSales.sellQty
            val tempBuyCost = diffBuyQty * itemCost
            val profitAndLoss = if(purchaseSales.buyQty == purchaseSales.sellQty){
                purchaseSales.price - purchaseSales.cost
            } else {
                purchaseSales.price - tempBuyCost
            }
            total += profitAndLoss
        }

        return total.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }

}

data class PurchaseAndSalesState(
    val purchaseAndSalesList: List<PurchaseSales> = listOf(),
    var totalProfitAndLoss: Double = 0.0
)
data class FilteredPurchaseAndSalesState(
    val filteredPurchaseSalesList: List<PurchaseSales> = listOf(),
    val categoryFilterMap: Map<String, Boolean> = mapOf(),
    val categoryMapOfProductFilterMap: Map<String, Map<String, Boolean>> = mapOf()
)