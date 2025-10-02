package com.bluestreak.dukaan.ui.product

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.utils.BarCodeScannerIconButton
import com.bluestreak.dukaan.ui.viewmodel.BillAmount
import com.bluestreak.dukaan.ui.viewmodel.SalesBillViewModel
import com.bluestreak.dukaan.ui.viewmodel.SalesProductQty
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

object SalesBillDestination : NavigationDestination {
    override val route = "sales_bill"
    override val titleRes = R.string.sales_bill_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesBillScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: SalesBillViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val productAndQuantityState by viewModel.productAndQuantityState.collectAsState()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(SalesBillDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        SalesBillBody(
            productAndQuantity = viewModel.filteredProductAndQuantity,
            selectedProduct = viewModel.selectedItemQtyState.salesProductQty,
            onSalesValueChange = viewModel::updateSelectedProductQtyState,
            onCostValueChange = viewModel::updateSelectedProductCost,
            salesList = viewModel.salesListState,
            updateSalesList = viewModel::updateSalesList,
            billAmount = viewModel.billAmountState,
            onDateChange = viewModel::updateSalesDateState,
            updateBillAmount = viewModel::updateBillAmount,
            updateList = viewModel::updateList,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveSalesBill()
                }
            },
            navigateBack = navigateBack,
            getBarCodeProduct = viewModel::updateProductFromBarCode,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun SalesBillBody(
    productAndQuantity: Map<Product, QuantityType>,
    selectedProduct: SalesProductQty,
    onSalesValueChange: (SalesProductQty) -> Unit,
    onCostValueChange: (SalesProductQty) -> Unit,
    salesList: List<SalesProductQty>,
    updateSalesList: (List<SalesProductQty>) -> Unit,
    billAmount: BillAmount,
    onDateChange: (Long) -> Unit,
    onSaveClick: () -> Unit,
    updateBillAmount: (BillAmount) -> Unit,
    updateList: (String) -> Unit,
    navigateBack: () -> Unit,
    getBarCodeProduct: (String) -> Unit,
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
    ) {
        ProductSalesSearchBar(
            selectedProduct = selectedProduct,
            onSalesValueChange = onSalesValueChange,
            productAndQuantity = productAndQuantity,
            onSearch = {},
            updateList = updateList
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

        SalesBillEntryForm(
            selectedProduct = selectedProduct,
            onSalesValueChange = onSalesValueChange,
            onCostValueChange = onCostValueChange,
            updateSalesList = updateSalesList,
            salesList = salesList,
        )
        SalesBillTotal(
            salesList = salesList,
            billAmount = billAmount,
            saveSalesBill = onSaveClick,
            updateBillAmount = updateBillAmount
        )
        SalesBillRows(
            salesList = salesList,
            modifier = modifier
        )
    }
}

@Composable
fun SalesBillTotal(
    modifier: Modifier = Modifier,
    salesList: List<SalesProductQty>,
    billAmount: BillAmount,
    updateBillAmount: (BillAmount) -> Unit,
    saveSalesBill: () -> Unit,
){
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
    var showNotification by rememberSaveable { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        //modifier = modifier.padding(4.dp)
    ){
        OutlinedTextField(
            value = billAmount.cash,
            onValueChange = {
                if(it.isNotBlank())
                    updateBillAmount(billAmount.copy(cash = it))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                if(it.isNotBlank())
                    updateBillAmount(billAmount.copy(upi = it))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                saveSalesBill()
                showNotification = true
                      },
            enabled = salesList.isNotEmpty() && billAmount.cash.isNotBlank() && billAmount.upi.isNotBlank(),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
    /*Text(
        text = stringResource(R.string.total) + " "
                + Currency.getInstance(Locale.getDefault()).symbol
                + "   ${billAmount.totalCost}",
        style = MaterialTheme.typography.titleLarge,

        )*/

}


@Composable
fun SalesBillEntryForm(
    selectedProduct: SalesProductQty,
    onSalesValueChange: (SalesProductQty) -> Unit,
    onCostValueChange: (SalesProductQty) -> Unit,
    updateSalesList: (List<SalesProductQty>) -> Unit,
    salesList: List<SalesProductQty>,
){
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
    var qtyTypeStock = 0
    qtyTypeStock = if(selectedProduct.product != null) selectedProduct.product?.qty!! / selectedProduct.qtyType?.piece!! else 0
    var qtyStock = 0
    qtyStock = if(selectedProduct.product != null) selectedProduct.product?.qty!! else 0
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        /*OutlinedTextField(
            value = selectedProduct.productName,
            onValueChange = { onSalesValueChange(selectedProduct.copy(productName = selectedProduct.productName)) },
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
                        onSalesValueChange(selectedProduct.copy(qtyTypeStr = productQty.value.type))
                        expanded = false
                    }
                )
            }
        }*/

        OutlinedTextField(
            value = selectedProduct.qty,
            onValueChange = {
                onSalesValueChange(selectedProduct.copy(qty = it))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = {
                Text(
                    text = if(selectedProduct.qtyTypeStr.isEmpty())
                                stringResource(R.string.qty)
                            else
                                selectedProduct.qtyTypeStr
                )
            },
            colors = colors,
            enabled = true,
            singleLine = true,
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp)
        )
        Text(
            text = "[${qtyTypeStock?:0}][${qtyStock?:0}]"
        )
        OutlinedTextField(
            value = selectedProduct.price,
            onValueChange = {
                onCostValueChange(selectedProduct.copy(price = it) )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                updateSalesList(salesList + selectedProduct)
            },
            enabled = selectedProduct.productName.isNotBlank()
                    && selectedProduct.qty.isNotBlank()
                    && selectedProduct.price.isNotBlank()
                    && selectedProduct.product?.qty!! > 0
                    && selectedProduct.qty.toInt() <= qtyTypeStock,
        ) {
            Icon(
                Icons.Default.AddCircle,
                contentDescription = stringResource(R.string.add_item)
            )
        }
        IconButton(
            onClick = {
                updateSalesList(salesList.dropLast(1))
            },
            enabled = salesList.isNotEmpty(),
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = stringResource(R.string.remove_item)
            )
        }
    }
}

@Composable
fun SalesBillRows(
    modifier: Modifier = Modifier,
    salesList: List<SalesProductQty>
){
    LazyColumn(
        modifier = modifier,
        //contentPadding = contentPadding
    ) {
        item {
            val headTextList = listOf(stringResource(R.string.item), stringResource(R.string.qty), stringResource(R.string.type), stringResource(R.string.mrp))
            SalesCardHead(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small)),
                headTextList = headTextList,
                containerColor = Color.LightGray
            )
        }

        items(items = salesList) { salesProductQty ->
            SalesBillCard(
                item = salesProductQty,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
            )
        }

    }
}


@Composable
fun SalesBillCard(
    item: SalesProductQty,
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
                text = item.price,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSalesSearchBar(
    onSearch: (String) -> Unit,
    productAndQuantity: Map<Product, QuantityType>,
    selectedProduct: SalesProductQty,
    onSalesValueChange: (SalesProductQty) -> Unit,
    updateList: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Create and remember the text field state
    val textFieldState = rememberTextFieldState()

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
                        placeholder = { Text(stringResource(R.string.search_product)) },
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
                                    onSalesValueChange(selectedProduct.copy(qtyTypeStr = value.type))

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

@Preview(showBackground = true)
@Composable
fun SalesBillScreenPreview(
    modifier: Modifier = Modifier
){
    DukaanTheme {
        SalesBillScreen(modifier,{}, {})
    }
}
