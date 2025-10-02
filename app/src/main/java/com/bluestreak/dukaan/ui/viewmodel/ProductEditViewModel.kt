package com.bluestreak.dukaan.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import com.bluestreak.dukaan.ui.product.ProductEditDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    quantityTypeRepository: QuantityTypeRepository
) : ViewModel() {
    var productUiState by mutableStateOf(ProductUiState())
        private set
    private val productId: Int = checkNotNull(savedStateHandle[ProductEditDestination.productIdArg])

    val qtyTypeUiState: StateFlow<QuantityTypeUiState> =
        quantityTypeRepository.getAllQuantityTypesStream()
            .filterNotNull()
            .map {
                QuantityTypeUiState(qtyTypeList = it.toList())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = QuantityTypeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    private fun validateInput(uiState: ItemDetails = productUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && mrp.isNotBlank() && qty.isNotBlank()
        }
    }
    fun updateUiState(itemDetails: ItemDetails) {

        productUiState =
            ProductUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }
    suspend fun updateItem() {
        if (validateInput(productUiState.itemDetails)) {
            productRepository.updateProduct(productUiState.itemDetails.toProduct())
        }
    }

    init {
        viewModelScope.launch {
            productUiState = productRepository.getProductStream(productId)
                .filterNotNull()
                .first()
                .toProductUiState(true)

            val qtyType = qtyTypeUiState.value.qtyTypeList.filter{ it.id == productUiState.itemDetails.qtyTypeId }

            updateUiState(productUiState.itemDetails.copy(qtyTypeStr = qtyType[0].type))

        }
    }
}