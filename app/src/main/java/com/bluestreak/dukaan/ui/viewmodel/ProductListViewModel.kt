package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Category
import com.bluestreak.dukaan.data.relations.CategoryWithProducts
import com.bluestreak.dukaan.data.relations.ProductCategoryQuantity
import com.bluestreak.dukaan.data.repositories.CategoryRepository
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.ui.product.ProductListDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProductListViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val catId = checkNotNull(savedStateHandle[ProductListDestination.catIdArg])
    val productListUiState: StateFlow<ProductListUiState> =
        categoryRepository.getCategoryWithProducts(catId.toString().toInt())
            .filterNotNull()
            .map {
                ProductListUiState(catProductList = it.toList())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProductListUiState()
            )
    val productCatQtyListUiState: StateFlow<ProductCatQtyListUiState> =
        productRepository.getProductWithCategoryAndQuantityType(catId.toString().toInt())
            .filterNotNull()
            .map{ ProductCatQtyListUiState(productCatQtyList = it.toList()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProductCatQtyListUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ProductListUiState(
    val catProductList: List<CategoryWithProducts> = listOf(),
    val category: Category? = null
)

data class ProductCatQtyListUiState(
    val productCatQtyList: List<ProductCategoryQuantity> = listOf()
)