package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.repositories.CategoryRepository
import com.bluestreak.dukaan.ui.product.CategoryEditDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CategoryEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val categoryId: Int = checkNotNull(savedStateHandle[CategoryEditDestination.catIdArg])
    var categoryUiState by mutableStateOf(CategoryUiState())
        private set
    init {
        viewModelScope.launch {
            categoryUiState = categoryRepository.getCategoryStream(categoryId)
                .filterNotNull()
                .first()
                .toCategoryUiState()
        }
    }

    private fun validateInput(uiState: CategoryDetails = categoryUiState.categoryDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }

    fun updateUiState(categoryDetails: CategoryDetails) {
        categoryUiState =
            CategoryUiState(categoryDetails = categoryDetails, isEntryValid = validateInput(categoryDetails))
    }

    suspend fun updateCategory(){
        if(validateInput()) {
            categoryRepository.updateCategory(categoryUiState.categoryDetails.toCategory())
        }

    }
}