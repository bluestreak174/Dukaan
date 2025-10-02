package com.bluestreak.dukaan.ui.product

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.relations.PurchaseSales
import com.bluestreak.dukaan.data.relations.profitAndLoss
import com.bluestreak.dukaan.data.relations.stockBalance
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.utils.DateRangePickerDukaan
import com.bluestreak.dukaan.ui.utils.FilterButton
import com.bluestreak.dukaan.ui.utils.RowsMultiMapFilter
import com.bluestreak.dukaan.ui.utils.TotalAndFilteredRowsCount
import com.bluestreak.dukaan.ui.viewmodel.PurchaseAndSalesViewModel
import java.math.RoundingMode
import java.util.Currency
import java.util.Locale

object PurchaseSaleScreenDestination : NavigationDestination {
    override val route = "purchase_sale_list"
    override val titleRes = R.string.profit_and_loss_title
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSaleScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PurchaseAndSalesViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
   /* val context = LocalContext.current
    val activity = context as? Activity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE*/

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val purchaseSalesUiState by viewModel.purchaseSalesUiState.collectAsState()
    val filteredPurchaseSalesState by viewModel.filteredPurchaseSalesState.collectAsState()
    val selectedDateRangeState by viewModel.selectedDateRangeState.collectAsState()
    val purchaseAndSalesList = purchaseSalesUiState.purchaseAndSalesList
    val filteredPurchaseSalesList = filteredPurchaseSalesState.filteredPurchaseSalesList
    val categoryProductFilterMap: MutableMap<String, MutableMap<String, Boolean>> = mutableMapOf()
    for ((outerKey, innerMap) in filteredPurchaseSalesState.categoryMapOfProductFilterMap) {
        categoryProductFilterMap[outerKey] = innerMap.toMutableMap()
    }

    val totalProfitLoss = purchaseSalesUiState.totalProfitAndLoss
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(PurchaseSaleScreenDestination.titleRes) + "\n" +
                        Currency.getInstance(Locale.getDefault()).symbol + " $totalProfitLoss " ,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },

        ) { innerPadding ->
        PurchaseSaleBody(
            purchaseSalesList = purchaseAndSalesList,
            filteredPurchaseSalesList = filteredPurchaseSalesList,
            categoryFilterMap = filteredPurchaseSalesState.categoryFilterMap.toMutableMap(),
            categoryProductFilterMap = categoryProductFilterMap,
            onDateValueChange = viewModel::updateUiDateState,
            updateOuterFilterData = viewModel::updateCategoryFilterData,
            updateCategoryProductFilterData = viewModel::updateCategoryProductFilterData,
            contentPadding = innerPadding,
        )

    }
}

@Composable
fun PurchaseSaleBody(
    purchaseSalesList: List<PurchaseSales>,
    filteredPurchaseSalesList: List<PurchaseSales>,
    categoryFilterMap: MutableMap<String, Boolean>,
    categoryProductFilterMap: MutableMap<String,MutableMap<String, Boolean>>,
    updateOuterFilterData: (MutableMap<String, Boolean>) -> Unit,
    updateCategoryProductFilterData: (MutableMap<String, MutableMap<String, Boolean>>) -> Unit,
    onDateValueChange: (Long, Long) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    Column (modifier = Modifier.padding(top =80.dp)) {
        DateRangePickerDukaan(
            onValChange = onDateValueChange,
            isFinYear = true
        )

        PurchaseSalesList(
            purchaseSalesList = purchaseSalesList,
            filteredPurchaseSalesList = filteredPurchaseSalesList,
            categoryFilterMap = categoryFilterMap,
            categoryProductFilterMap = categoryProductFilterMap,
            updateOuterFilterData = updateOuterFilterData,
            updateCategoryProductFilterData = updateCategoryProductFilterData,
        )
    }
}

@Composable
fun PurchaseSalesList(
    purchaseSalesList: List<PurchaseSales>,
    filteredPurchaseSalesList: List<PurchaseSales>,
    categoryFilterMap: MutableMap<String, Boolean>,
    categoryProductFilterMap: MutableMap<String,MutableMap<String, Boolean>>,
    updateOuterFilterData: (MutableMap<String, Boolean>) -> Unit,
    updateCategoryProductFilterData: (MutableMap<String, MutableMap<String, Boolean>>) -> Unit,
    modifier: Modifier = Modifier,
){
    val headTextList = listOf(stringResource(R.string.product),
        stringResource(R.string.qty_type),
        stringResource(R.string.buy_qty),
        stringResource(R.string.cost), stringResource(R.string.sell_qty),
        stringResource(R.string.price), stringResource(R.string.bal),
        stringResource(R.string.profit_loss) )

    LazyColumn(
        modifier = modifier,
        //contentPadding = contentPadding
    ) {
        item {
            PurchasesSalesCardHead(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small)),
                purchaseSalesList = purchaseSalesList,
                filteredList = filteredPurchaseSalesList,
                categoryFilterMap = categoryFilterMap,
                categoryProductFilterMap = categoryProductFilterMap,
                onOuterFilterChanged = updateOuterFilterData,
                onNestedFilterChanged = updateCategoryProductFilterData,
                headTextList = headTextList,
                containerColor = Color.LightGray,
            )
        }
        items(items = filteredPurchaseSalesList) { item ->
            PurchaseSalesCard(
                purchaseSales = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
            )
        }
    }

}



@Composable
fun PurchaseSalesCard(
    purchaseSales: PurchaseSales,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),

        ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = purchaseSales.productName + "/",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = purchaseSales.qtyType,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = "${purchaseSales.buyQty}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.3f)
            )
            Text(
                text = "${purchaseSales.cost}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f)
            )
            Text(
                text = "${purchaseSales.sellQty}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.3f)
            )
            Text(
                text = "${purchaseSales.price}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f)
            )

            Text(
                text = "${purchaseSales.stockBalance}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.3f)
            )
            val profitLossPercentage = if(purchaseSales.price > 0.0)
                                            ((purchaseSales.profitAndLoss * 100)/purchaseSales.price).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                                        else
                                            0
            Text(
                text = "${purchaseSales.profitAndLoss} [${profitLossPercentage}%]",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f)
            )
        }
    }
}

@Composable
fun PurchasesSalesCardHead(
    modifier: Modifier = Modifier,
    purchaseSalesList: List<PurchaseSales>,
    filteredList: List<PurchaseSales>,
    categoryFilterMap: MutableMap<String, Boolean>,
    categoryProductFilterMap: MutableMap<String,MutableMap<String, Boolean>>,
    onOuterFilterChanged: (MutableMap<String, Boolean>) -> Unit,
    onNestedFilterChanged: (MutableMap<String, MutableMap<String, Boolean>>) -> Unit,
    headTextList: List<String> = listOf(),
    containerColor: Color,
) {
    var expanded by remember { mutableStateOf(false) }
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
        Column() {
            Row(){
                if(expanded){
                    RowsMultiMapFilter(
                        filterMap = categoryFilterMap,
                        filterSubFilterMap = categoryProductFilterMap,
                        onCheckedChange = onNestedFilterChanged,
                        onOuterCheckedChange = onOuterFilterChanged
                    )
                } else {
                    TotalAndFilteredRowsCount(
                        totalRowsCount = purchaseSalesList.size,
                        filteredRowsCount = filteredList.size
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                    //.padding(2.dp)
                    .weight(1f)
                ){
                    Text(
                        text = headTextList[0] + "/" + headTextList[1],
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                    FilterButton(
                        expanded = expanded,
                        onClick = { expanded = !expanded },
                    )
                }
                Text(
                    text = headTextList[2] + "\n ( " + filteredList.sumOf { it.buyQty } + " )",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.5f)
                )
                Text(
                    text = headTextList[3] + "\n ( " + filteredList.sumOf { it.cost } + " )",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.5f)
                )
                Text(
                    text = headTextList[4] + "\n ( " + filteredList.sumOf { it.sellQty } + " )",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.5f)
                )
                Text(
                    text = headTextList[5] + "\n ( " + filteredList.sumOf { it.price } + " )",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.5f)
                )
                Text(
                    text = headTextList[6] + "\n ( " + filteredList.sumOf { it.stockBalance } + " )",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.5f)
                )
                val profitLoss = filteredList.sumOf { it.profitAndLoss }.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                val totalPrice = filteredList.sumOf { it.price }
                val profitLossPercentage = if( totalPrice > 0.0)
                                                ((profitLoss * 100)/totalPrice).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                                            else
                                                0
                Text(
                    text = headTextList[7] + "\n ( " + profitLoss + " ) [" + profitLossPercentage +"%]",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.5f)
                )

            }
        }

    }
}
