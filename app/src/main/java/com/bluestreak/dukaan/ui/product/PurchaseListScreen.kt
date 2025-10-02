package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.bluestreak.dukaan.data.entities.Purchases
import com.bluestreak.dukaan.data.relations.ProductQuantity
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.utils.DateRangePickerDukaan
import com.bluestreak.dukaan.ui.viewmodel.PurchaseListDetails
import com.bluestreak.dukaan.ui.viewmodel.PurchaseListViewModel

object PurchaseListDestination : NavigationDestination {
    override val route = "purchase_list"
    override val titleRes = R.string.purchase_list_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseListScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PurchaseListViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val purchaseListDetailsList: List<PurchaseListDetails> = viewModel.purchaseUiState.purchaseListDetailsList
    val totalCost = viewModel.purchaseUiState.totalCost

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(PurchaseListDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },

    ) { innerPadding ->
        PurchaseListBody(
            purchaseListDetailsList = purchaseListDetailsList,
            totalCost = totalCost,
            modifier = modifier.fillMaxSize(),
            onItemValueChange = viewModel::updateUiDateState,
            contentPadding = innerPadding,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PurchaseListBody(
    modifier: Modifier = Modifier,
    purchaseListDetailsList: List<PurchaseListDetails>,
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


        PurchaseList(
            purchaseListDetailsList = purchaseListDetailsList,
            modifier = modifier,
            contentPadding = contentPadding
        )


    }
}

@Composable
fun TotalCard(
    totalCost: String,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        /*colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )*/
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),

        ) {
            Text(
                text = stringResource(R.string.total)
            )
            Text(
                text = " : "
            )
            Text(
                text = totalCost
            )
        }
    }
}

@Composable
fun PurchaseList(
    purchaseListDetailsList: List<PurchaseListDetails>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues  = PaddingValues(0.dp),
){
    val headTextList = listOf(stringResource(R.string.item), stringResource(R.string.qty), stringResource(R.string.type), stringResource(R.string.cost))
    PurchasesCardHead(
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
        items(items = purchaseListDetailsList) { item ->
            PurchasesCard(
                productQuantity = item.productQuantity,
                purchases = item.purchase,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
            )
        }

    }
}

@Composable
fun PurchasesCard(
    productQuantity: ProductQuantity?,
    purchases: Purchases?,
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
                text = "${productQuantity?.name}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = purchases?.quantity.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = "${productQuantity?.type}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = purchases?.cost.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
        }
    }
}

@Composable
fun PurchasesCardHead(
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
            if(filterEnabled){
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

