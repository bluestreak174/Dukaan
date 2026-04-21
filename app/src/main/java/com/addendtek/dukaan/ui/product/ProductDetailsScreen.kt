package com.addendtek.dukaan.ui.product

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.addendtek.dukaan.DukaanTopAppBar
import com.addendtek.dukaan.R
import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.relations.CategoryQuantity
import com.addendtek.dukaan.ui.AppViewModelProvider
import com.addendtek.dukaan.ui.navigation.NavigationDestination
import com.addendtek.dukaan.ui.theme.DukaanTheme
import com.addendtek.dukaan.ui.utils.HelpShowCase
import com.addendtek.dukaan.ui.utils.ShowcaseProperty
import com.addendtek.dukaan.ui.viewmodel.AppSettingsViewModel
import com.addendtek.dukaan.ui.viewmodel.ProductDetailsUiState
import com.addendtek.dukaan.ui.viewmodel.ProductDetailsViewModel
import com.addendtek.dukaan.ui.viewmodel.toProduct
import java.math.RoundingMode

object ProductDetailsDestination : NavigationDestination {
    override val route = "product_details"
    override val titleRes = R.string.product_details_title
    const val productIdArg = "productId"
    val routeWithArgs = "$route/{$productIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    navigateToEditProduct: (Int) -> Unit,
    navigateToProductHistory: (Int) -> Unit,
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProductDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appSettingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.uiState.collectAsState()

    val appHelpPrefState by appSettingsViewModel.appHelpPrefState.collectAsStateWithLifecycle()

    val targets = remember {
        mutableStateMapOf<String, ShowcaseProperty>()
    }
    var showHelp by remember {
        mutableStateOf(false)
    }
    val editHelpTitle = stringResource(R.string.edit_product_title)
    val editHelpSubTitle = stringResource(R.string.help_edit_product)
    Scaffold(
        topBar = {
            DukaanTopAppBar(
                title = stringResource(ProductDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditProduct(uiState.value.productDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        dimensionResource(id = R.dimen.padding_large)
                    )
                    .onGloballyPositioned { coordinates ->
                        targets["Edit Product"] = ShowcaseProperty(
                            index = 4,
                            coordinates = coordinates,
                            title = editHelpTitle,
                            subTitle = editHelpSubTitle
                        )
                    },

            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_product_title),
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        ProductDetailsBody(
            productDetailsUiState = uiState.value,
            onSellItem = { viewModel.reduceQuantityByOne() },
            onBuyItem = { viewModel.increaseQuantityByOne() },
            navigateToHistory = navigateToProductHistory,
            catQty = uiState.value.categoryQty,
            targets = targets,
            onShowCaseCompleted = { showHelp = false },
            showHelp = appHelpPrefState.isAppHelpEnabled,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
    }

}

@Composable
fun ProductDetailsBody(
    productDetailsUiState: ProductDetailsUiState,
    onSellItem: () -> Unit,
    onBuyItem:  () -> Unit,
    navigateToHistory: (Int) -> Unit,
    catQty: CategoryQuantity,
    modifier: Modifier = Modifier,
    targets: SnapshotStateMap<String, ShowcaseProperty>,
    onShowCaseCompleted: () -> Unit,
    showHelp: Boolean,
){
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    var showBuyConfirm by rememberSaveable { mutableStateOf(false) }
    val buyHelpTitle = stringResource(R.string.help_buy_btn)
    val buyHelpSubTitle = stringResource(R.string.help_buy_product)
    val sellHelpTitle = stringResource(R.string.help_sell_btn)
    val sellHelpSubTitle = stringResource(R.string.help_sell_product)
    val historyHelpTitle = stringResource(R.string.product_history_title)
    val historyHelpSubTitle = stringResource(R.string.help_product_history)
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        //var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        ProductDetails(
            product = productDetailsUiState.productDetails.toProduct(),
            catQty = catQty,
            modifier = Modifier.fillMaxWidth()
        )
        if(showConfirm) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    onSellItem()
                    showConfirm = !showConfirm
                },
                onDeleteCancel = { showConfirm = !showConfirm}
            )
        }
        if(showBuyConfirm) {
            AddConfirmationDialog(
                onAddConfirm = {
                    onBuyItem()
                    showBuyConfirm = !showBuyConfirm
                },
                onAddCancel = { showBuyConfirm = !showBuyConfirm}
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { showBuyConfirm = !showBuyConfirm },
                modifier = Modifier
                    .padding(8.dp)
                    .onGloballyPositioned { coordinates ->
                        targets["Buy Product"] = ShowcaseProperty(
                            index = 1,
                            coordinates = coordinates,
                            title = buyHelpTitle,
                            subTitle = buyHelpSubTitle
                        )
                    },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black
                ),
                enabled = true
            ) {
                Text(
                    text = stringResource(R.string.buy),
                    fontSize = 20.sp
                )
            }
            Button(
                onClick = { showConfirm = !showConfirm },
                modifier = Modifier
                    .padding(8.dp)
                    .onGloballyPositioned { coordinates ->
                        targets["Sell Product"] = ShowcaseProperty(
                            index = 2,
                            coordinates = coordinates,
                            title = sellHelpTitle,
                            subTitle = sellHelpSubTitle
                        )
                    },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                enabled = !productDetailsUiState.outOfStock
            ) {
                Text(
                    text = stringResource(R.string.sell),
                    fontSize = 20.sp
                )
            }
        }
        OutlinedButton(
            onClick = { navigateToHistory(productDetailsUiState.productDetails.id) },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.history),
                fontSize = 20.sp,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    targets["Product History"] = ShowcaseProperty(
                        index = 3,
                        coordinates = coordinates,
                        title = historyHelpTitle,
                        subTitle = historyHelpSubTitle
                    )
                }
            )
        }

        val cost = productDetailsUiState.productDetails.toProduct().cost
        PercentageCalculator(
            cost = cost,
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
fun PercentageCalculator(
    modifier: Modifier = Modifier,
    cost: Double,
) {
    var expanded by remember { mutableStateOf(false) }

    if(expanded) {
        var costPercentage by remember { mutableDoubleStateOf(5.0) }
        var sellValue  by remember { mutableDoubleStateOf(0.0) }
        var finalPercentage by remember { mutableDoubleStateOf(5.0) }
        var finalSellValue  by remember { mutableDoubleStateOf( 0.0 )}

        sellValue = cost + (cost * costPercentage/100).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        finalSellValue = sellValue + (sellValue * finalPercentage/100).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

        Row (
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = "$costPercentage",
                onValueChange = { newValue ->
                    costPercentage = newValue.toDoubleOrNull()!!
                    sellValue = cost + (cost * costPercentage/100).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(stringResource(R.string.percentage)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier
                    .weight(0.25F)
                    .height(60.dp),
                enabled = true,
                singleLine = true
            )
            OutlinedTextField(
                value = "$sellValue",
                onValueChange = {},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(stringResource(R.string.price)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = modifier
                    .weight(0.25F)
                    .height(60.dp),
                enabled = true,
                readOnly = true,
                singleLine = true
            )
            OutlinedTextField(
                value = "$finalPercentage",
                onValueChange = { newValue ->
                    finalPercentage = newValue.toDoubleOrNull()!!
                    finalSellValue = sellValue + (sellValue * finalPercentage/100).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(stringResource(R.string.percentage)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = modifier
                    .weight(0.25F)
                    .height(60.dp),
                enabled = true,
                singleLine = true
            )
            OutlinedTextField(
                value = "$finalSellValue",
                onValueChange = {},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(stringResource(R.string.price)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = modifier
                    .weight(0.25F)
                    .height(60.dp),
                enabled = true,
                readOnly = true,
                singleLine = true
            )
        }
    }

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
                contentDescription = stringResource(R.string.percent_expand_button_content_description),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            text = stringResource(R.string.percentage_calculator_for_above_cost)
        )
    }


}

@Composable
fun ProductDetails(
    product: Product,
    catQty: CategoryQuantity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            ProductDetailsRow(
                labelResID = R.string.product,
                productDetail = product.name,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ProductDetailsRow(
                labelResID = R.string.cost,
                productDetail = product.cost.toString(),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ProductDetailsRow(
                labelResID = R.string.mrp,
                productDetail = product.mrp.toString(),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ProductDetailsRow(
                labelResID = R.string.quantity_in_stock,
                productDetail = product.qty.toString(),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )

            ProductDetailsRow(
                labelResID = R.string.qty_type_name,
                productDetail = catQty.qtyType,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ProductDetailsRow(
                labelResID = R.string.category,
                productDetail = " ${catQty.categoryName}",
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ProductDetailsRow(
                labelResID = R.string.bar_code,
                productDetail = " ${product.barCode}",
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )

        }
    }
}

@Composable
private fun ProductDetailsRow(
    @StringRes labelResID: Int,
    productDetail: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(labelResID),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = productDetail,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )

    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {  },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.sell_product)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        }
    )
}

@Composable
private fun AddConfirmationDialog(
    onAddConfirm: () -> Unit,
    onAddCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {  },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.buy_product)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onAddCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onAddConfirm) {
                Text(stringResource(R.string.yes))
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun ProductDetailsScreenPreview(modifier: Modifier = Modifier){
    DukaanTheme {
        ProductDetailsScreen(
            navigateToEditProduct = {},
            navigateToProductHistory = {},
            navigateBack = {}
        )
    }
}
