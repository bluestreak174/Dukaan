package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.relations.ProductHistory
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.viewmodel.ProductHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

object ProductHistoryDestination : NavigationDestination {
    override val route = "product_history"
    override val titleRes = R.string.product_history_title
    const val productIdArg = "productId"
    val routeWithArgs = "$route/{$productIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductHistoryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToBillDetails: (Int) -> Unit,
    navigateToSalesBillDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductHistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val productHistoryUIState = viewModel.productHistoryUIState.collectAsState()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(ProductHistoryDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },

    ) { innerPadding ->
        ProductHistoryBody(
            productHistoryList = productHistoryUIState.value.productHistoryList,
            onBillIdClick =  navigateToBillDetails ,
            onSalesBillIdClick = navigateToSalesBillDetails,
            contentPadding = innerPadding,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProductHistoryBody(
    productHistoryList: List<ProductHistory>,
    onBillIdClick: (Int) -> Unit,
    onSalesBillIdClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    ProductHistoryList(
        productHistoryList = productHistoryList,
        onBillIdClick = onBillIdClick,
        onSalesBillIdClick = onSalesBillIdClick,
        contentPadding = contentPadding
    )

}
@Composable
fun ProductHistoryList(
    productHistoryList: List<ProductHistory>,
    onBillIdClick: (Int) -> Unit,
    onSalesBillIdClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            var title = ""
            if(productHistoryList.isNotEmpty()) title = productHistoryList[0].name
            TitleCard(
                title = title,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small)),
            )
        }
        items(items = productHistoryList) { item ->
            ProductHistoryCard(
                productHistory = item,
                onBillIdClick = onBillIdClick,
                onSalesBillIdClick = onSalesBillIdClick,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
            )
        }

    }
}
@Composable
fun TitleCard(
    title: String,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Center
            ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun ProductHistoryCard(
    productHistory: ProductHistory,
    onBillIdClick: (Int) -> Unit,
    onSalesBillIdClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(
            modifier = modifier
        ) {
            val formattedDate =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(productHistory.date)
            val formattedTime =
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(productHistory.date)
            Column (
                modifier = Modifier.padding(4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ){
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(2.dp)
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(2.dp)
                )

                    Text(
                        text = "Bill " + productHistory.billId.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                if(productHistory.buySell == "B") {
                                    onBillIdClick(productHistory.billId)
                                } else {
                                    onSalesBillIdClick(productHistory.billId)
                                }
                            }
                    )

            }
            var buySellColor = Color.Red
            if(productHistory.buySell.equals("S")) buySellColor = Color.Blue
            Text(
                text = productHistory.buySell,
                color = buySellColor,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.3f)
            )
            Text(
                text = productHistory.quantity.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = productHistory.qtyType,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = productHistory.amount.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
        }
    }
}