package com.addendtek.dukaan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.addendtek.dukaan.data.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var appNameUIState by mutableStateOf(AppNameUIState())
        private set
    val appNamePrefState: StateFlow<AppNameUIState> =
        userPreferencesRepository.appName.map { appName ->
            AppNameUIState(appName = appName, isEntryValid = appName.isNotBlank())
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppNameUIState()
            )

    fun saveAppName() {
        viewModelScope.launch {
            userPreferencesRepository.saveAppNamePreference(appNameUIState.appName)
        }
    }

    fun updateUiState(appName: String) {
        val isEntryValid = appName.isNotEmpty()
        appNameUIState=AppNameUIState(appName = appName, isEntryValid = isEntryValid)
    }



}

data class AppNameUIState(
    val appName: String = "",
    val isEntryValid: Boolean = false
)

