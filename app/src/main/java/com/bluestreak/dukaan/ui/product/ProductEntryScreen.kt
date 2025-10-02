package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.utils.BarCodeScannerIconButton
import com.bluestreak.dukaan.ui.utils.DatePickerScreen
import com.bluestreak.dukaan.ui.viewmodel.ItemDetails
import com.bluestreak.dukaan.ui.viewmodel.ProductEntryViewModel
import com.bluestreak.dukaan.ui.viewmodel.ProductUiState
import com.bluestreak.dukaan.ui.viewmodel.PurchaseEntryDetails
import com.bluestreak.dukaan.ui.viewmodel.QuantityTypeUiState
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Currency
import java.util.Locale

object ProductEntryDestination : NavigationDestination {
    override val route = "product_entry"
    override val titleRes = R.string.product_entry_title
    const val catIdArg = "catId"
    val routeWithArgs = "${route}/{$catIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ProductEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val qtyTypeUiState by viewModel.qtyTypeUiState.collectAsState()
    Scaffold(
        topBar = {
            DukaanTopAppBar(
                title = stringResource(ProductEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ProductEntryBody(
            productUiState = viewModel.productUiState,
            qtyTypeUiState = qtyTypeUiState,
            onItemValueChange = viewModel::updateUiState,
            onPurchaseValueChange = viewModel::updatePurchaseUiState,
            onDateChange = viewModel::updatePurchaseDateState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveProduct()
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
fun ProductEntryBody(
    productUiState: ProductUiState,
    qtyTypeUiState: QuantityTypeUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onPurchaseValueChange: (PurchaseEntryDetails) -> Unit,
    onDateChange: (Long) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ProductInputForm(
            productDetails = productUiState.itemDetails,
            onItemValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        QuantityTypeInputForm(
            purchaseEntryDetails = productUiState.purchaseEntryDetails,
            qtyTypeList = qtyTypeUiState.qtyTypeList,
            onValueChange = onPurchaseValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        PurchaseInputForm(
            purchaseEntryDetails = productUiState.purchaseEntryDetails,
            productDetails = productUiState.itemDetails,
            onPurchaseValueChange = onPurchaseValueChange,
            onItemValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        DatePickerModal(
            onDateChange = onDateChange
        )
        Button(
            onClick = onSaveClick,
            enabled = productUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DatePickerModal(
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    val defaultDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val selectedTimeMillis by remember  { mutableLongStateOf(defaultDateMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    //purchaseDetails.copy(purchaseDate = selectedTimeMillis)
    DatePickerScreen(
        selectedDateInMillis = selectedTimeMillis,
        showDatePicker = showDatePicker,
        showDatePickerChange = { showDatePicker = it },
        onDateChange =  onDateChange,
        modifier = modifier
    )
}

@Composable
fun ProductInputForm(
    productDetails: ItemDetails,
    modifier: Modifier = Modifier,
    onItemValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        OutlinedTextField(
            value = productDetails.name,
            onValueChange = { onItemValueChange(productDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.product_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

    }
}

@Composable
fun QuantityTypeInputForm(
    purchaseEntryDetails: PurchaseEntryDetails,
    qtyTypeList: List<QuantityType>,
    modifier: Modifier = Modifier,
    onValueChange: (PurchaseEntryDetails) -> Unit = {},
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var qtyTypeSelected by remember { mutableStateOf("") }
        Row {
            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .weight((1f))
            ) {

                OutlinedTextField(
                    value = qtyTypeSelected,
                    onValueChange = {
                        onValueChange(purchaseEntryDetails.copy(qtyTypeId = it))
                    },
                    label = { Text(stringResource(R.string.qty_type_name_req)) },
                    leadingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "More options")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier,
                    enabled = enabled,
                    readOnly = true,
                    singleLine = true
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    qtyTypeList.forEach { qtyType ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = qtyType.type,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            },
                            onClick = {
                                onValueChange(purchaseEntryDetails.copy(qtyType = qtyType))
                                qtyTypeSelected = qtyType.type
                                expanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = purchaseEntryDetails.qty,
                onValueChange = { onValueChange(purchaseEntryDetails.copy(qty = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(stringResource(R.string.qty)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.weight(0.5f),
                enabled = enabled,
                singleLine = true
            )
        }
}

@Composable
fun PurchaseInputForm(
    purchaseEntryDetails: PurchaseEntryDetails,
    productDetails: ItemDetails,
    modifier: Modifier = Modifier,
    onPurchaseValueChange: (PurchaseEntryDetails) -> Unit = {},
    onItemValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {

        Row {
            OutlinedTextField(
                value = purchaseEntryDetails.cost,
                onValueChange = { onPurchaseValueChange(purchaseEntryDetails.copy(cost = it, cash = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.product_cost_req)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
                modifier = Modifier.weight(0.5f),
                enabled = enabled,
                singleLine = true
            )
            OutlinedTextField(
                value = purchaseEntryDetails.mrp,
                onValueChange = { onPurchaseValueChange(purchaseEntryDetails.copy(mrp = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.mrp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
                modifier = Modifier.weight(0.5f),
                enabled = enabled,
                singleLine = true
            )
        }
        Row {
            OutlinedTextField(
                value = purchaseEntryDetails.cash,
                onValueChange = { onPurchaseValueChange(purchaseEntryDetails.copy(cash = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.cash_req)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
                modifier = Modifier.weight(0.5f),
                enabled = enabled,
                singleLine = true
            )
            OutlinedTextField(
                value = purchaseEntryDetails.upi,
                onValueChange = {
                    // onPurchaseValueChange(purchaseEntryDetails.copy(upi = it))
                                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.upi_req)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
                modifier = Modifier.weight(0.5f),
                enabled = enabled,
                readOnly = true,
                singleLine = true
            )
        }

        Row {
            OutlinedTextField(
                value = purchaseEntryDetails.address,
                onValueChange = { onPurchaseValueChange(purchaseEntryDetails.copy(address = it)) },
                label = { Text(stringResource(R.string.bill_address)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.weight(0.7f),
                enabled = enabled,
                singleLine = true
            )
            OutlinedTextField(
                value = productDetails.barcode,
                onValueChange = { onItemValueChange(productDetails.copy(barcode = it)) },
                label = { Text(stringResource(R.string.bar_code)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true
            )
            BarCodeScannerIconButton(
                modifier = Modifier.weight(0.3f),
                getBarCodeProduct = { onItemValueChange(productDetails.copy(barcode = it)) }
            )
        }

        /*
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }*/
    }
}


@Preview(showBackground = true)
@Composable
fun ProductEntryScreenPreview(modifier: Modifier = Modifier){
    DukaanTheme {
        ProductEntryScreen(
            navigateBack = {},
            onNavigateUp = {}
        )
    }
}