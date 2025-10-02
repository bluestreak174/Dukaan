package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.relations.ProductHistory
import com.bluestreak.dukaan.data.repositories.ProductSalesRepository
import com.bluestreak.dukaan.ui.product.ProductHistoryDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProductHistoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val productSalesRepository: ProductSalesRepository
): ViewModel() {
    private val productId = checkNotNull(savedStateHandle[ProductHistoryDestination.productIdArg])
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    val productHistoryUIState: StateFlow<ProductHistoryUIState> =
        productSalesRepository.getProductBuySell(productId.toString().toInt())
            .map {
                ProductHistoryUIState(
                    productHistoryList = it
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProductHistoryUIState()
            )
}

data class ProductHistoryUIState(
    val productHistoryList: List<ProductHistory> = listOf()
)