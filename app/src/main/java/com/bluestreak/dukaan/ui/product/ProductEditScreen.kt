package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.bluestreak.dukaan.ui.viewmodel.ItemDetails
import com.bluestreak.dukaan.ui.viewmodel.ProductEditViewModel
import com.bluestreak.dukaan.ui.viewmodel.ProductUiState
import com.bluestreak.dukaan.ui.viewmodel.QuantityTypeUiState
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

object ProductEditDestination : NavigationDestination {
    override val route = "product_edit"
    override val titleRes = R.string.edit_product_title
    const val productIdArg = "productId"
    val routeWithArgs = "$route/{$productIdArg}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val qtyTypeUiState by viewModel.qtyTypeUiState.collectAsState()
    Scaffold(
        topBar = {
            DukaanTopAppBar(
                title = stringResource(ProductEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ProductEditBody(
            productUiState = viewModel.productUiState,
            qtyTypeUiState = qtyTypeUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateItem()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState()),

        )
    }
}

@Composable
fun ProductEditBody(
    productUiState: ProductUiState,
    qtyTypeUiState: QuantityTypeUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ItemInputForm(
            itemDetails = productUiState.itemDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        /*QuantityTypeInputForm(
            qtyTypeList = qtyTypeUiState.qtyTypeList,
            itemDetails = productUiState.itemDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )*/
        QuantityTypeDisplay(
            itemDetails = productUiState.itemDetails,
        )
        ProductBarCodeDetails(
            itemDetails = productUiState.itemDetails,
            onValueChange = onItemValueChange
        )
        Button(
            onClick = onSaveClick,
            enabled = productUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun ProductBarCodeDetails(
    modifier: Modifier = Modifier,
    itemDetails: ItemDetails,
    onValueChange: (ItemDetails) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        BarCodeScannerIconButton(
            modifier = Modifier.weight(0.5f),
            getBarCodeProduct = { onValueChange(itemDetails.copy(barcode = it)) }
        )
        OutlinedTextField(
            value = itemDetails.barcode,
            onValueChange = { onValueChange(itemDetails.copy(barcode = it)) },
            label = { Text(stringResource(R.string.bar_code)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.weight(1f),
            enabled = true,
            readOnly = true,
            singleLine = true
        )
    }
}

@Composable
fun QuantityTypeDisplay(
    itemDetails: ItemDetails,
){
    OutlinedTextField(
        value = itemDetails.qtyTypeStr,
        onValueChange = { },
        label = { Text(stringResource(R.string.qty_type_name_req)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = true,
        readOnly = true,
        singleLine = true
    )
}

@Composable
fun QuantityTypeInputForm(
    modifier: Modifier = Modifier,
    qtyTypeList: List<QuantityType>,
    itemDetails: ItemDetails,
    onValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Box(
            modifier = Modifier
                .padding(1.dp)
        ) {

            OutlinedTextField(
                value = itemDetails.qtyTypeStr,
                onValueChange = { },
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
                modifier = Modifier.fillMaxWidth(),
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
                            onValueChange(
                                itemDetails.copy(
                                    qtyTypeId = qtyType.id,
                                    qtyTypeStr = qtyType.type
                                )
                            )
                            expanded = false
                        }

                    )
                }
            }
        }


    }
}

@Composable
fun ItemInputForm(
    itemDetails: ItemDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = itemDetails.name,
            onValueChange = { onValueChange(itemDetails.copy(name = it)) },
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

        OutlinedTextField(
            value = itemDetails.qty,
            onValueChange = { onValueChange(itemDetails.copy(qty = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.quantity_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        Row {
            OutlinedTextField(
                value = itemDetails.cost,
                onValueChange = { onValueChange(itemDetails.copy(cost = it)) },
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
                value = itemDetails.mrp,
                onValueChange = { onValueChange(itemDetails.copy(mrp = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.product_price_req)) },
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
        /*if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }*/
    }
}


@Preview(showBackground = true)
@Composable
fun ProductEditScreenPreview(modifier: Modifier = Modifier) {
    DukaanTheme {
        ProductEntryScreen(
            navigateBack = {},
            onNavigateUp = {}
        )
    }
}