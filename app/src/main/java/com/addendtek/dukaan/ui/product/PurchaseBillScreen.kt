package com.addendtek.dukaan.ui.product

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.addendtek.dukaan.DukaanTopAppBar
import com.addendtek.dukaan.R
import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.entities.QuantityType
import com.addendtek.dukaan.ui.AppViewModelProvider
import com.addendtek.dukaan.ui.navigation.NavigationDestination
import com.addendtek.dukaan.ui.theme.DukaanTheme
import com.addendtek.dukaan.ui.utils.BarCodeScannerIconButton
import com.addendtek.dukaan.ui.utils.HelpShowCase
import com.addendtek.dukaan.ui.utils.ShowcaseProperty
import com.addendtek.dukaan.ui.viewmodel.AppSettingsViewModel
import com.addendtek.dukaan.ui.viewmodel.BillAmount
import com.addendtek.dukaan.ui.viewmodel.PurchaseBillViewModel
import com.addendtek.dukaan.ui.viewmodel.PurchaseProductQty
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

object PurchaseBillDestination : NavigationDestination {
    override val route = "purchase_bill"
    override val titleRes = R.string.purchase_bill_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseBillScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: PurchaseBillViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appSettingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val productAndQuantityState by viewModel.productAndQuantityState.collectAsState()
    val purchaseList = viewModel.purchaseListState

    val appHelpPrefState by appSettingsViewModel.appHelpPrefState.collectAsStateWithLifecycle()

    val targets = remember {
        mutableStateMapOf<String, ShowcaseProperty>()
    }
    var showHelp by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(PurchaseBillDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        PurchaseBillBody(
            productAndQuantity = viewModel.filteredProductAndQuantity,
            selectedProduct = viewModel.selectedProductQtyState.purchaseProductQty,
            onPurchaseValueChange = viewModel::updateSelectedProductQtyState,
            onCostValueChange = viewModel::updateSelectedProductCost,
            purchaseList = purchaseList,
            updatePurchaseList = viewModel::updatePurchaseList,
            billAmount = viewModel.billAmountState,
            onDateChange = viewModel::updatePurchaseDateState,
            updateBillAmount = viewModel::updateBillAmount,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.savePurchaseBill()
                }
            },
            updateList = viewModel::updateList,
            navigateBack = navigateBack,
            getBarCodeProduct = viewModel::updateProductFromBarCode,
            targets = targets,
            onShowCaseCompleted = { showHelp = false},
            showHelp = appHelpPrefState.isAppHelpEnabled,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }

}


@Composable
fun PurchaseBillBody(
    productAndQuantity: Map<Product, QuantityType>,
    selectedProduct: PurchaseProductQty,
    onPurchaseValueChange: (PurchaseProductQty) -> Unit,
    onCostValueChange: (PurchaseProductQty) -> Unit,
    purchaseList: List<PurchaseProductQty>,
    updatePurchaseList: (List<PurchaseProductQty>) -> Unit,
    billAmount: BillAmount,
    onDateChange: (Long) -> Unit,
    onSaveClick: () -> Unit,
    updateBillAmount: (BillAmount) -> Unit,
    updateList: (String) -> Unit,
    navigateBack: () -> Unit,
    getBarCodeProduct: (String) -> Unit,
    targets: SnapshotStateMap<String, ShowcaseProperty>,
    onShowCaseCompleted: () -> Unit,
    showHelp: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    var showConfirmDialog by remember { mutableStateOf(false) }
    BackHandler(enabled = true) {
        showConfirmDialog = true
    }
    if (showConfirmDialog) {
        ConfirmDialog(
            onDismissRequest = { showConfirmDialog = false },
            navigateBack = navigateBack
        )
    }
    Column(
        modifier = modifier.padding(top = 60.dp)
       /* modifier = Modifier
            .padding(
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                top = contentPadding.calculateTopPadding()
            )
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()*/
    ) {

        ProductPurchaseSearchBar(
            selectedProduct = selectedProduct,
            onPurchaseValueChange = onPurchaseValueChange,
            productAndQuantity = productAndQuantity,
            onSearch = {},
            updateList = updateList,
            targets = targets
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BarCodeScannerIconButton(
                getBarCodeProduct = getBarCodeProduct
            )
            BillAddressText(
                billAmount = billAmount,
                updateBillAmount = updateBillAmount
            )
            DatePickerModal(
                onDateChange = onDateChange,
                modifier = Modifier.padding(8.dp)
            )

        }

        PurchaseBillEntryForm(
            selectedProduct = selectedProduct,
            onPurchaseValueChange = onPurchaseValueChange,
            onCostValueChange = onCostValueChange,
            updatePurchaseList = updatePurchaseList,
            purchaseList = purchaseList,
            targets = targets,
        )

        PurchaseBillTotal(
            purchaseList = purchaseList,
            billAmount = billAmount,
            savePurchaseBill = onSaveClick,
            updateBillAmount = updateBillAmount,
            targets = targets
        )
        PurchaseBillRows(
            purchaseList = purchaseList,
            modifier = modifier,
            contentPadding = contentPadding
        )

    }

    if(showHelp){
        val uniqueTargets = targets.values.sortedBy { it.index }
        var currentTargetIndex by remember { mutableStateOf(0) }

        val currentTarget =
            if (uniqueTargets.isNotEmpty() && currentTargetIndex < uniqueTargets.size) uniqueTargets[currentTargetIndex] else null

        currentTarget?.let {
            HelpShowCase(
                target = it,
                dismissOnClickOutside = true,
            ) {
                if (++currentTargetIndex >= uniqueTargets.size) {
                    onShowCaseCompleted()
                }
            }
        }
    }


}



@Composable
fun BillAddressText(
    modifier: Modifier = Modifier,
    billAmount: BillAmount,
    updateBillAmount: (BillAmount) -> Unit,
){
    OutlinedTextField(
        value = billAmount.billAddress,
        onValueChange = {
            updateBillAmount(billAmount.copy(billAddress = it))
        },
        label = { Text(stringResource(R.string.bill_address)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = Modifier.width(dimensionResource(R.dimen.address_text_width)),
        enabled = true,
        singleLine = true
    )
}

@Composable
fun PurchaseBillTotal(
    purchaseList: List<PurchaseProductQty>,
    billAmount: BillAmount,
    updateBillAmount: (BillAmount) -> Unit,
    savePurchaseBill: () -> Unit,
    targets: SnapshotStateMap<String, ShowcaseProperty>,
){
    val defaultAddress = stringResource(R.string.market)
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
    var showNotification by rememberSaveable { mutableStateOf(false) }

    val saveHelpTitle = stringResource(R.string.help_save_bill_title)
    val saveHelpSubTitle = stringResource(R.string.help_save_bill_sub_title)
    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            //modifier = Modifier.padding(4.dp)
        ) {
            OutlinedTextField(
                value = billAmount.cash,
                onValueChange = {
                    if(it.toDoubleOrNull() != null){
                        updateBillAmount(billAmount.copy(cash = it))
                    }

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text( text = stringResource(R.string.cash) + " " + Currency.getInstance(Locale.getDefault()).symbol)  },
                colors = colors,
                enabled = true,
                singleLine = true,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(2.dp)
            )
            OutlinedTextField(
                value = billAmount.upi,
                onValueChange = {
                    if(it.toDoubleOrNull() != null){
                        updateBillAmount(billAmount.copy(upi = it))
                    }

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text( text = stringResource(R.string.upi) + " " + Currency.getInstance(Locale.getDefault()).symbol)  },
                colors = colors,
                enabled = true,
                singleLine = true,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(2.dp)
            )
            OutlinedTextField(
                value = "${billAmount.totalCost}",
                onValueChange = {},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text( text = stringResource(R.string.total) + " " + Currency.getInstance(Locale.getDefault()).symbol)  },
                colors = colors,
                enabled = true,
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(2.dp)
            )
            if(showNotification){
                NotificationDialog(
                    onDismissRequest = { showNotification = false }
                )
            }
            Button(
                onClick = {
                    if(billAmount.billAddress.isBlank()) {
                        billAmount.billAddress = defaultAddress
                    }
                    savePurchaseBill()
                    showNotification = true
                          },
                enabled = purchaseList.isNotEmpty() && billAmount.cash.isNotBlank()
                        && billAmount.upi.isNotBlank(),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_action),
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        targets["Save Bill"] = ShowcaseProperty(
                            index = 4,
                            coordinates = coordinates,
                            title = saveHelpTitle,
                            subTitle = saveHelpSubTitle
                        )
                    },)
            }

        }

    }

}


@Composable
fun PurchaseBillEntryForm(
    modifier: Modifier = Modifier,
    updatePurchaseList: (List<PurchaseProductQty>) -> Unit,
    purchaseList: List<PurchaseProductQty>,
    selectedProduct: PurchaseProductQty,
    onPurchaseValueChange: (PurchaseProductQty) -> Unit,
    onCostValueChange: (PurchaseProductQty) -> Unit,
    targets: SnapshotStateMap<String, ShowcaseProperty>,
){
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
    val addRowHelpTitle = stringResource(R.string.help_add_item_to_bill)
    val addRowHelpSubTitle = stringResource(R.string.help_bill_add_to_list)
    val remRowHelpTitle = stringResource(R.string.help_remove_item_from_bill)
    val remRowHelpSubTitle = stringResource(R.string.help_bill_remove_from_list)

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        //modifier = Modifier.padding(4.dp)
    ) {

        /*OutlinedTextField(
            value = selectedProduct.productName,
            onValueChange = { onPurchaseValueChange(selectedProduct.copy(productName = selectedProduct.productName)) },
            label = { Text(stringResource(R.string.product_name_req)) },
            leadingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Add Item")
                }
            },
            colors = colors,
            enabled = true,
            readOnly = true,
            singleLine = true,
            modifier = Modifier.weight(1f)
        )


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            productAndQuantity.forEach { productQty ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = productQty.key.name,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    onClick = {
                        selectedProduct.productName = productQty.key.name
                        selectedProduct.product = productQty.key
                        selectedProduct.qtyType = productQty.value
                        onPurchaseValueChange(selectedProduct.copy(qtyTypeStr = productQty.value.type))
                        expanded = false
                    }
                )
            }
        }*/

        OutlinedTextField(
            value = selectedProduct.qty,
            onValueChange = {
                    if(it.toIntOrNull() != null || it.isBlank()){
                        onPurchaseValueChange(selectedProduct.copy(qty = it))
                    }
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = {
                Text(
                    text = selectedProduct.qtyTypeStr.ifEmpty { stringResource(R.string.qty) }
                )
                    },
            colors = colors,
            enabled = true,
            singleLine = true,
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp)
        )
        OutlinedTextField(
            value = selectedProduct.cost,
            onValueChange = {
                    if(it.toDoubleOrNull() != null){
                        onCostValueChange(selectedProduct.copy(cost = it) )
                    }
                 },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text( text = stringResource(R.string.cost) + " " + Currency.getInstance(Locale.getDefault()).symbol)  },
            colors = colors,
            enabled = true,
            singleLine = true,
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp)
        )

        IconButton(
            onClick = {
                val listItem = purchaseList.find { it.product?.id == selectedProduct.product?.id}
                if(listItem == null) {
                    updatePurchaseList(purchaseList + selectedProduct)
                }

            },
            modifier = Modifier
                .padding(0.dp)
                .onGloballyPositioned { coordinates ->
                    targets["Add Product To Bill"] = ShowcaseProperty(
                        index = 2,
                        coordinates = coordinates,
                        title = addRowHelpTitle,
                        subTitle = addRowHelpSubTitle
                    )
                },
            enabled = selectedProduct.productName.isNotBlank() && selectedProduct.qty.isNotBlank() && selectedProduct.cost.isNotBlank(),
        ) {
            Icon(
                Icons.Default.AddCircle,
                contentDescription = stringResource(R.string.add_item),

            )
        }
        IconButton(
            onClick = {
                updatePurchaseList(purchaseList.dropLast(1))
            },
            modifier = Modifier.onGloballyPositioned { coordinates ->
                targets["Remove Product From Bill"] = ShowcaseProperty(
                    index = 3,
                    coordinates = coordinates,
                    title = remRowHelpTitle,
                    subTitle = remRowHelpSubTitle
                )
            },
            enabled = purchaseList.isNotEmpty(),
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = stringResource(R.string.remove_item),
            )
        }



    }


}

@Composable
fun PurchaseBillRows(
    modifier: Modifier = Modifier,
    purchaseList: List<PurchaseProductQty>,
    contentPadding: PaddingValues,
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            val headTextList = listOf(stringResource(R.string.item), stringResource(R.string.qty), stringResource(R.string.type), stringResource(R.string.cost))
            PurchasesCardHead(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small)),
                headTextList = headTextList,
                containerColor = Color.LightGray
            )
        }

        items(items = purchaseList) { purchaseProductQty ->
            PurchasesBillCard(
                item = purchaseProductQty,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPurchaseSearchBar(
    onSearch: (String) -> Unit,
    productAndQuantity: Map<Product, QuantityType>,
    selectedProduct: PurchaseProductQty,
    onPurchaseValueChange: (PurchaseProductQty) -> Unit,
    updateList: (String) -> Unit,
    targets: SnapshotStateMap<String, ShowcaseProperty>,
    modifier: Modifier = Modifier
) {

    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Create and remember the text field state
    val textFieldState = rememberTextFieldState()
    val searchHelpTitle = stringResource(R.string.search_product)
    val searchHelpSubTitle = stringResource(R.string.help_bill_search_product)
    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = textFieldState.text.toString(),
                        onQueryChange = {
                            textFieldState.edit { replace(0, length, it) }
                            updateList(it)
                        },
                        onSearch = {
                            onSearch(textFieldState.text.toString())
                            expanded = false
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = {
                            Text(
                                text = if(selectedProduct.product?.name?.isNotBlank() == true) "${selectedProduct.product?.name}" else stringResource(R.string.search_product),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                    targets["Search"] = ShowcaseProperty(
                                        index = 1,
                                        coordinates = coordinates,
                                        title = searchHelpTitle,
                                        subTitle = searchHelpSubTitle
                                    )
                                }
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    textFieldState.edit { replace(0, length, "") }
                                    expanded = false
                                }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.product)
                                )
                            }
                        },
                        modifier = Modifier.height(60.dp)
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                // Display search results in a scrollable column
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    productAndQuantity.forEach { (key, value) ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "${key.name}  ${value.type} ${key.mrp}",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    textFieldState.edit { replace(0, length, key.name) }
                                    selectedProduct.productName = key.name
                                    selectedProduct.product = key
                                    selectedProduct.qtyType = value
                                    onPurchaseValueChange(selectedProduct.copy(qtyTypeStr = value.type))

                                    expanded = false
                                }
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }


    }
}
@Composable
fun PurchasesBillCard(
    item: PurchaseProductQty,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),

        ) {

        Row(
            modifier = Modifier
        ) {
            Text(
                text = item.productName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = item.qty,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = item.qtyTypeStr,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = item.cost,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    navigateBack: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp, alignment = Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                Text(
                    text = stringResource(R.string.back_button_confirm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )

                Row {
                    Button(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                    Button(onClick = navigateBack) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }

    }
}

@Composable
fun NotificationDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.update_success),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.ok),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PurchaseBillScreenPreview(
    modifier: Modifier = Modifier
){
    DukaanTheme {
        PurchaseBillScreen(
            modifier = Modifier,
            {},
            {}
        )
    }
}

