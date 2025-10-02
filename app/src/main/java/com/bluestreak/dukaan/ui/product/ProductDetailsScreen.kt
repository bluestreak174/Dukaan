package com.bluestreak.dukaan.ui.product

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.relations.CategoryQuantity
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.viewmodel.ProductDetailsUiState
import com.bluestreak.dukaan.ui.viewmodel.ProductDetailsViewModel
import com.bluestreak.dukaan.ui.viewmodel.toProduct

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
    viewModel: ProductDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.uiState.collectAsState()



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
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))

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
    modifier: Modifier = Modifier
){
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    var showBuyConfirm by rememberSaveable { mutableStateOf(false) }
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
                modifier = Modifier.padding(8.dp),
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
                modifier = Modifier.padding(8.dp),
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
                fontSize = 20.sp
            )
        }

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
