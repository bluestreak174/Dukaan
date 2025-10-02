package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.data.repositories.PurchaseBillRepository
import com.bluestreak.dukaan.ui.product.PurchaseBillDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PurchaseBillDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    purchaseBillRepository: PurchaseBillRepository
) : ViewModel(){
    private val billId: Int = checkNotNull(savedStateHandle[PurchaseBillDetailsDestination.billIdArg])

    val billDetailsUiState: StateFlow<BillDetailsUiState> = purchaseBillRepository.getBillDetails(billId)
        .map{
            BillDetailsUiState(
                billDetailsList = it
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = BillDetailsUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}

data class BillDetailsUiState(
    val billDetailsList: List<BillDetails> = listOf()
)