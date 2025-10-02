package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.Product
import com.bluestreak.dukaan.data.relations.ProductCategoryQuantity
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.home.HomeDestination
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.viewmodel.ProductListViewModel

object ProductListDestination : NavigationDestination {
    override val route = "product_list"
    override val titleRes = R.string.product_list_title
    const val catIdArg = "catId"
    val routeWithArgs = "$route/{$catIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    navigateToProductEntry: (Int) -> Unit,
    navigateToProductUpdate: (Int) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit = {},
    viewModel: ProductListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val productListUiState by viewModel.productListUiState.collectAsState()
    val productCatQtyListUiState by viewModel.productCatQtyListUiState.collectAsState()


    var productList: List<Product> = listOf()
    var catId: Int = 0
    var category: String = ""
    if(productListUiState.catProductList.isNotEmpty()){
        productList = productListUiState.catProductList[0].products
        catId = productListUiState.catProductList[0].category.id
        category = productListUiState.catProductList[0].category.name
    }

    val productCatQtyList = productCatQtyListUiState.productCatQtyList

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(HomeDestination.titleRes) + " - " + category,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToProductEntry(catId) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.product_entry_title)
                )
            }
        },
    ) { innerPadding ->
        ProductListBody(
            productList = productList,
            onItemClick = navigateToProductUpdate,
            productCatQtyList = productCatQtyList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProductListBody(
    productList: List<Product>,
    productCatQtyList: List<ProductCategoryQuantity>,
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    ProductList(
        productList = productList,
        productCatQtyList = productCatQtyList,
        modifier = modifier,
        onItemClick = { onItemClick(it.id) },
        contentPadding = contentPadding
    )
}

@Composable
fun ProductList(
    productList: List<Product>,
    productCatQtyList: List<ProductCategoryQuantity>,
    onItemClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = productList, key = { it.id }) { item ->
            productCatQtyList.find {
                productCatQty -> productCatQty.product.id.equals(item.id)
            }?.let {
                ProductCard(
                    product = item,
                    productCatQty = it,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.card_padding_small))
                        .clickable { onItemClick(item) }
                )
            }
        }

    }
}

@Composable
fun ProductCard(
    product: Product,
    productCatQty: ProductCategoryQuantity,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(
            modifier = modifier
        ) {
            Text(
                text = "${product.name}\n${productCatQty.qtyType.type}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = "${product.qty/productCatQty.qtyType.piece}\n[${product.qty}]",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f)
            )

            Text(
                text = product.cost.toString(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f)
            )
            Text(
                text = product.mrp.toString(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview(modifier: Modifier = Modifier) {
    DukaanTheme {
        ProductListScreen(
            navigateToProductEntry = {},
            navigateToProductUpdate = {},
            navigateBack = {  },
        )
    }

}