package com.bluestreak.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class QuantityTypeEntryViewModel(
    private val quantityTypeRepository: QuantityTypeRepository
) : ViewModel() {
    var qtyUiState by mutableStateOf(QtyUiState())
        private set

    var qtyTypeListUiState: StateFlow<QtyTypeListUiState> =
        quantityTypeRepository.getAllQuantityTypesStream()
            .map {
                QtyTypeListUiState(
                    qtyTypeList = it,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = QtyTypeListUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
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

    suspend fun saveQty(){
        if(validateInput()) {
            quantityTypeRepository.insertQuantityType(qtyUiState.qtyDetails.toQuantityType())
        }
    }
}

data class QtyTypeListUiState(
    val qtyTypeList: List<QuantityType> = listOf(),
    val qtyTypeListMap: MutableMap<QuantityType, QuantityTypeDetails> = mutableMapOf()
)

data class QtyUiState(
    val qtyDetails: QuantityTypeDetails = QuantityTypeDetails(),
    val isEntryValid: Boolean = false
)
data class QuantityTypeDetails(
    val id: Int = 0,
    val name: String = "",
    val piece: String = "",
)

fun QuantityTypeDetails.toQuantityType(): QuantityType = QuantityType(
    id = id,
    type = name,
    piece = piece.toIntOrNull() ?: 0
)

fun QuantityType.toQtyUiState(isEntryValid: Boolean = false): QtyUiState = QtyUiState(
    qtyDetails = this.toQuantityTypeDetails(),
    isEntryValid = isEntryValid
)

fun QuantityType.toQuantityTypeDetails(): QuantityTypeDetails = QuantityTypeDetails(
    id = id,
    name = type,
    piece = piece.toString()
)