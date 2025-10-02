package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.Category
import com.bluestreak.dukaan.data.repositories.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CategoryEntryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    var categoryUiState by mutableStateOf(CategoryUiState())
        private set

    val categoryListUiState: StateFlow<CategoryListUiState> = categoryRepository.getAllCategoriesStream()
        .map {
            CategoryListUiState(
                categoryList = it
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CategoryListUiState()
        )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    private fun validateInput(uiState: CategoryDetails = categoryUiState.categoryDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }

    fun updateUiState(categoryDetails: CategoryDetails) {
        categoryUiState = CategoryUiState(
            categoryDetails = categoryDetails,
            isEntryValid = validateInput()
        )
    }


    suspend fun saveCategory() {
        if(validateInput()) {
            categoryRepository.insertCategory(categoryUiState.categoryDetails.toCategory())
        }
    }

}

data class CategoryListUiState(
    val categoryList: List<Category> = listOf()
)

data class CategoryUiState(
    val categoryDetails: CategoryDetails = CategoryDetails(),
    val isEntryValid: Boolean = false
)
data class CategoryDetails(
    val id: Int = 0,
    val name: String = ""
)

fun CategoryDetails.toCategory(): Category = Category(
    id = id,
    name = name,
)

fun Category.toCategoryUiState(isEntryValid: Boolean = false): CategoryUiState =
    CategoryUiState(
        categoryDetails = this.toCategoryDetails(),
        isEntryValid = isEntryValid
    )

fun Category.toCategoryDetails(): CategoryDetails =
    CategoryDetails(
        id = id,
        name = name
    )
