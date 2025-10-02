package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.Sales
import com.bluestreak.dukaan.data.relations.ProductQuantity
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.utils.DateRangePickerDukaan
import com.bluestreak.dukaan.ui.viewmodel.SalesListDetails
import com.bluestreak.dukaan.ui.viewmodel.SalesListViewModel

object SalesListDestination : NavigationDestination {
    override val route = "sales_list"
    override val titleRes = R.string.sales_list_title
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesListScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SalesListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val salesListDetailsList: List<SalesListDetails> = viewModel.salesUiState.salesListDetailsList
    val totalCost = viewModel.salesUiState.totalCost

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(SalesListDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },

        ) { innerPadding ->
        SalesListBody(
            salesListDetailsList = salesListDetailsList,
            totalCost = totalCost,
            modifier = modifier.fillMaxSize(),
            onItemValueChange = viewModel::updateUiDateState,
            contentPadding = innerPadding,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SalesListBody(
    modifier: Modifier = Modifier,
    salesListDetailsList: List<SalesListDetails>,
    totalCost: Double = 0.0,
    onItemValueChange: (Long, Long) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column (modifier = Modifier.padding(top =80.dp)){
        DateRangePickerDukaan(
            onValChange = onItemValueChange,
        )
        TotalCard(
            totalCost = totalCost.toString(),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.card_padding_small)),
        )
        SalesList(
            salesListDetailsList = salesListDetailsList,
            modifier = modifier,
            contentPadding = contentPadding
        )

    }
}

@Composable
fun SalesList(
    salesListDetailsList: List<SalesListDetails>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues  = PaddingValues(0.dp),
){
    val headTextList = listOf(stringResource(R.string.item), stringResource(R.string.qty), stringResource(R.string.type), stringResource(R.string.price))
    SalesCardHead(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.card_padding_small)),
        headTextList = headTextList,
        containerColor = Color.LightGray,
        filterEnabled = false
    )
    LazyColumn(
        modifier = modifier,
        //contentPadding = contentPadding
    ) {
        items(items = salesListDetailsList) { item ->
            SalesCard(
                productQuantity = item.productQuantity,
                sales = item.sales,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
            )
        }

    }
}

@Composable
fun SalesCard(
    productQuantity: ProductQuantity?,
    sales: Sales?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(
            modifier = Modifier
        ) {
            Text(
                text = "${productQuantity?.name}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = sales?.quantity.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = "${productQuantity?.type}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = sales?.price.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
        }
    }
}

@Composable
fun SalesCardHead(
    modifier: Modifier = Modifier,
    headTextList: List<String> = listOf(),
    containerColor: Color,
    filterEnabled: Boolean = false
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        /*colors = CardColors(
            containerColor = containerColor,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )*/
    ) {

        Row(
            modifier = Modifier
        ) {
            if(filterEnabled) {
                IconButton(
                    onClick = { },
                    enabled = true
                ) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.product)
                    )
                }
            }
            Text(
                text = headTextList[0],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = headTextList[1],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = headTextList[2],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = headTextList[3],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
        }
    }
}