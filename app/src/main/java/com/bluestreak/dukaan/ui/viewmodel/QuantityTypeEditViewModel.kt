package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import com.bluestreak.dukaan.ui.product.QuantityTypeEditDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuantityTypeEditViewModel(

    savedStateHandle: SavedStateHandle,
    private val quantityTypeRepository: QuantityTypeRepository
) : ViewModel() {
    private val quantityTypeId: Int = checkNotNull(savedStateHandle[QuantityTypeEditDestination.qtyTypeIdArg])
    var qtyUiState by mutableStateOf(QtyUiState())
        private set

    init {
        viewModelScope.launch {
            qtyUiState = quantityTypeRepository.getQuantityTypeStream(quantityTypeId)
                .filterNotNull()
                .first()
                .toQtyUiState()
        }
    }

    private fun validateInput(uiState: QuantityTypeDetails = qtyUiState.qtyDetails) : Boolean{
        return with(uiState) {
            name.isNotBlank() && piece.isNotBlank()
        }
    }

    fun updateUiState(qtyDetails: QuantityTypeDetails) {
        qtyUiState = QtyUiState(
            qtyDetails = qtyDetails,
            isEntryValid = validateInput(qtyDetails)
        )
    }
    suspend fun updateQuantityType(){
        if(validateInput()){
            quantityTypeRepository.updateQuantityType(qtyUiState.qtyDetails.toQuantityType())
        }
    }

}