package com.addendtek.dukaan.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.addendtek.dukaan.DukaanTopAppBar
import com.addendtek.dukaan.R
import com.addendtek.dukaan.ui.AppViewModelProvider
import com.addendtek.dukaan.ui.navigation.NavigationDestination
import com.addendtek.dukaan.ui.viewmodel.AppNameUIState
import com.addendtek.dukaan.ui.viewmodel.AppSettingsViewModel
import kotlinx.coroutines.launch

object AppSettingsDestination : NavigationDestination {
    override val route = "app_settings"
    override val titleRes = R.string.app_settings
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: AppSettingsViewModel =  viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    val appNamePreferencesState by viewModel.appNamePrefState.collectAsState()
    val appNameUIState = viewModel.appNameUIState
    val appName = appNamePreferencesState.appName
    Scaffold(
        topBar = {
            DukaanTopAppBar(
                title = stringResource(AppSettingsDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
            )
        }
    ) { innerPadding ->
        SettingsScreenBody(
            appNameUIState = appNameUIState,
            appName = appName,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveAppName()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun SettingsScreenBody(
    appNameUIState: AppNameUIState,
    appName: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = appName,
            )

        OutlinedTextField(
            value = appNameUIState.appName,
            onValueChange = { onValueChange(it) },
            label = { Text(stringResource(R.string.application_name)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )

        Button(
            onClick = onSaveClick,
            enabled = appNameUIState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}