package com.addendtek.dukaan.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.relations.CategoryQuantity
import com.addendtek.dukaan.data.repositories.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProductSearchViewModel(
    private val productRepository: ProductRepository,
) : ViewModel()  {
    var productCategoryState: StateFlow<ProductCategoryUiState> =
        productRepository.getProductsCatQuantity().filterNotNull()
            .map {
                ProductCategoryUiState(
                    productCategory = it
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ProductCategoryUiState(
                    productCategory = mapOf()
                )
            )
    var filteredProductAndCategory: Map<Product, CategoryQuantity> by mutableStateOf(productCategoryState.value.productCategory)
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    fun updateList(searchText: String){
        //Log.d("Dukaan", productCategoryState.value.productCategory.toString())
        if(searchText.isEmpty()){
            filteredProductAndCategory = productCategoryState.value.productCategory
        }else{
            filteredProductAndCategory = productCategoryState.value.productCategory.filter { (key,value) -> key.name.contains(searchText, ignoreCase = true) }
        }
        //Log.d("Dukaan",filteredProductAndCategory.toString())
    }
}

data class ProductCategoryUiState(
    var productCategory: Map<Product, CategoryQuantity>
)