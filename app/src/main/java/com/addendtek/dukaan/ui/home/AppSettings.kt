package com.addendtek.dukaan.ui.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.addendtek.dukaan.DukaanTopAppBar
import com.addendtek.dukaan.R
import com.addendtek.dukaan.ui.AppViewModelProvider
import com.addendtek.dukaan.ui.navigation.NavigationDestination
import com.addendtek.dukaan.ui.viewmodel.AppNameUIState
import com.addendtek.dukaan.ui.viewmodel.AppSettingsViewModel
import com.addendtek.dukaan.ui.viewmodel.ExportImportSettingsState
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

    val appHelpPrefState by viewModel.appHelpPrefState.collectAsStateWithLifecycle()

    val exportImportSettingsState: ExportImportSettingsState = viewModel.exportImportSettingsState
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
    ){ uri ->
        coroutineScope.launch{
            if (uri != null) {
                // Trigger the export in the ViewModel using the obtained URI and data
                val exportMsg = viewModel.exportCSV(context, uri)
                Toast.makeText(
                    context,
                    exportMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        }



    // Launcher to pick any content type using ACTION_OPEN_DOCUMENT (or GetContent)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ){ uri: Uri? ->
        coroutineScope.launch{
            if (uri != null) {
                val importMsg = viewModel.importCSV(context, uri)
                Toast.makeText(
                    context,
                    importMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        }


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
            selectedOption = exportImportSettingsState.selectedOption,
            onExpImportChange = viewModel::updateExportImport,
            tableNames = exportImportSettingsState.tableNames,
            onTablesChange = viewModel::updateTablesSelection,
            selectedTable = exportImportSettingsState.selectedTable,
            onExportClick = {
                exportLauncher.launch("dukaan_${exportImportSettingsState.selectedTable}.csv")
                            },
            onImportClick = {
                 importLauncher.launch("*/*")
            },
            isAppHelpEnabled = appHelpPrefState.isAppHelpEnabled,
            updateAppHelpState = {
                coroutineScope.launch {
                    viewModel.saveAppHelpState(it)
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
    onSaveClick: () -> Unit,
    onExpImportChange: (String) -> Unit,
    tableNames: List<String>,
    selectedOption: String,
    onTablesChange: (String) -> Unit,
    selectedTable: String,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    isAppHelpEnabled: Boolean,
    updateAppHelpState: (Boolean) -> Unit
) {
    Column(
    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
    modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppNameSettings(
            appNameUIState = appNameUIState,
            appName = appName,
            onValueChange = onValueChange,
            onSaveClick = onSaveClick
        )
        HorizontalDivider()
        ExportImportSettings(
            onExpImportChange = onExpImportChange,
            tableNames = tableNames,
            selectedOption = selectedOption,
            onTablesChange = onTablesChange,
            selectedTable = selectedTable,
            onExportClick = onExportClick,
            onImportClick = onImportClick
        )
        HorizontalDivider()
        ShowHelpSettings(
            isAppHelpEnabled = isAppHelpEnabled,
            updateAppHelpState = updateAppHelpState
        )
    }
}

@Composable
fun ShowHelpSettings(
    isAppHelpEnabled: Boolean,
    updateAppHelpState: (Boolean) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.enable_help),
            modifier = Modifier.weight(1f)
        )
        // The Switch composable
        Switch(
            checked = isAppHelpEnabled,
            onCheckedChange = { isChecked ->
                updateAppHelpState(isChecked)
            },
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun AppNameSettings(
    appNameUIState: AppNameUIState,
    appName: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit
){
        var expanded by remember { mutableStateOf(false) }
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { expanded  = !expanded },
                modifier = modifier
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Expand for Store Name settings.",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = stringResource(R.string.application_name) + " : " + appName
            )
        }
        if(expanded) {
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

@Composable
fun ExportImportSettings(
    modifier: Modifier = Modifier,
    onExpImportChange: (String) -> Unit,
    tableNames: List<String>,
    selectedOption: String,
    selectedTable: String,
    onTablesChange: (String) -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
){
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { expanded  = !expanded },
            modifier = modifier
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = stringResource(R.string.expand_for_export_import_settings),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            text = stringResource(R.string.export_import_action)
        )
    }
    if(expanded) {
        val radioOptions = listOf("Export", "Import")
        RadioButtonExportImportSelection(
            radioOptions = radioOptions,
            onOptionSelected = onExpImportChange,
            selectedOption = selectedOption
        )
        RadioButtonTableSelection(
            tableNames = tableNames,
            onTableChange = onTablesChange,
            selectedTable = selectedTable
        )

        if(selectedOption == "Export"){
            Button(
                onClick = onExportClick,
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.export) )
            }
        }else {
            Button(
                onClick = onImportClick,
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.import_txt) )
            }
        }

    }

}

@Composable
fun RadioButtonTableSelection(
    modifier: Modifier = Modifier,
    tableNames: List<String> = listOf(),
    onTableChange: (String) -> Unit,
    selectedTable: String
) {
    val tblDispMap = mapOf(
        "Categories" to stringResource(R.string.category_list_title),
        "QtyTypes" to stringResource(R.string.quantity_type_list_title),
        "Products" to stringResource(R.string.product_list_title),
        "Purchases" to stringResource(R.string.purchase_list_title),
        "Sales" to stringResource(R.string.sales_list_title),
        "Purchase Bills" to stringResource(R.string.purchase_bill_list_title),
        "Sales Bills" to stringResource(R.string.sales_bill_list_title))
    Column(
        modifier = modifier.selectableGroup()
    ) {
        tableNames.forEach { text ->
            Row(
                Modifier
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedTable),
                        onClick = { onTableChange(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedTable),
                    onClick = null // null recommended for accessibility with screen readers
                )

                Text(
                    text = tblDispMap[text]?:text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun RadioButtonExportImportSelection(
    modifier: Modifier = Modifier,
    radioOptions: List<String> = listOf(),
    onOptionSelected: (String) -> Unit,
    selectedOption: String
) {
    val radioLangTxt = mapOf("Export" to stringResource(R.string.export),
        "Import" to stringResource(R.string.import_txt)
    )
    Row(
        modifier = modifier.selectableGroup()
    ) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = radioLangTxt[text]?:text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}