package com.bluestreak.dukaan.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestreak.dukaan.data.repositories.SalesBillRepository
import com.bluestreak.dukaan.ui.product.SalesBillDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class SalesBillDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    salesBillRepository: SalesBillRepository
) : ViewModel(){
    private val billId: Int = checkNotNull(savedStateHandle[SalesBillDetailsDestination.billIdArg])

    val billDetailsUiState: StateFlow<BillDetailsUiState> = salesBillRepository.getBillDetails(billId)
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

