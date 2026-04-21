package com.addendtek.dukaan.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.addendtek.dukaan.DukaanTopAppBar
import com.addendtek.dukaan.R
import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.relations.CategoryQuantity
import com.addendtek.dukaan.ui.AppViewModelProvider
import com.addendtek.dukaan.ui.navigation.NavigationDestination

object ProductSearchDestination : NavigationDestination {
    override val route = "product_search"
    override val titleRes = R.string.product_search_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateToProductDetails: (Int) -> Unit,
    viewModel: ProductSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    //collect state once so that flow is available for filtered data
    val productAndCategoryState by viewModel.productCategoryState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(ProductSearchDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        ProductSearchBody(
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            productAndCategory = viewModel.filteredProductAndCategory,
            updateList = viewModel::updateList,
            navigateToProductDetails = navigateToProductDetails
        )
    }

}

@Composable
fun ProductSearchBody(
    productAndCategory: Map<Product, CategoryQuantity>,
    updateList: (String) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    Column(
        modifier = modifier.padding(top = 60.dp)
    ){
        ProductSearchBar(
            productAndCategory = productAndCategory,
            onSearch = {},
            updateList = updateList,
            navigateToProductDetails = navigateToProductDetails
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchBar(
    onSearch: (String) -> Unit,
    productAndCategory: Map<Product, CategoryQuantity>,
    updateList: (String) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
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
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_product),
                                style = MaterialTheme.typography.titleLarge,
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
                    productAndCategory.forEach { (key, value) ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "${key.name} ${value.qtyType} ${key.mrp} [${value.categoryName}]",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    textFieldState.edit { replace(0, length, key.name) }
                                    navigateToProductDetails(key.id)
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