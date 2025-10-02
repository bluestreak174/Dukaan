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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.relations.TotalBill
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.utils.DateRangePickerDukaan
import com.bluestreak.dukaan.ui.viewmodel.PurchaseBillState
import com.bluestreak.dukaan.ui.viewmodel.SalesBillState
import com.bluestreak.dukaan.ui.viewmodel.SummaryViewModel

object SummaryScreenDestination : NavigationDestination {
    override val route = "summary"
    override val titleRes = R.string.summary_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val purchaseBillState = viewModel.purchaseUiState
    val salesBillState = viewModel.salesUiState
    val stockUiState by viewModel.stockUiState.collectAsState()
    Scaffold(
        topBar = {
            DukaanTopAppBar(
                title = stringResource(SummaryScreenDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        SummaryDetailsBody(
            onDateChange = viewModel::updateUiDateState,
            purchaseBillState = purchaseBillState,
            salesBillState = salesBillState,
            stockUiState = stockUiState,
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
fun SummaryDetailsBody(
    modifier: Modifier = Modifier,
    onDateChange: (Long, Long) -> Unit,
    purchaseBillState: PurchaseBillState,
    salesBillState: SalesBillState,
    stockUiState: String,
) {

    val profitAndLoss = (salesBillState.salesBill?.total?.times(10) ?: 0.0) /100
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        DateRangePickerDukaan(
            onValChange = onDateChange,
            isFinYear = true
        )
        SummaryDetails(
            modifier = Modifier.fillMaxWidth(),
            purchaseBill = purchaseBillState.purchaseBill,
            salesBill = salesBillState.salesBill
        )
        StockDetails(
            stockUiState = stockUiState,
            profitAndLoss = profitAndLoss
        )
    }
}

@Composable
fun StockDetails(
    modifier: Modifier = Modifier,
    stockUiState: String,
    profitAndLoss: Double? = 0.0
){
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
            Row(modifier = modifier) {
                Text(
                    text = stringResource(R.string.stock_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stockUiState,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )

            }
            Row(modifier = modifier) {
                Text(
                    text = stringResource(R.string.profit_loss),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$profitAndLoss",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )

            }

        }
    }

}

@Composable
fun SummaryDetails(
    modifier: Modifier = Modifier,
    purchaseBill: TotalBill?,
    salesBill: TotalBill?
){
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

            SummaryDetailsRowHead(
                labelResID = R.string.purchase_list_title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            SummaryDetailsBody(
                bill = purchaseBill,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            SummaryDetailsRowHead(
                labelResID = R.string.sales_list_title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            SummaryDetailsBody(
                bill = salesBill,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )

        }
    }

}
@Composable
fun SummaryDetailsBody(
    bill: TotalBill?,
    modifier: Modifier = Modifier
){
    SummaryDetailsRow(
        labelResID = R.string.cash,
        summaryDetail = "${bill?.cash?:0.0}",
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.padding_medium)
        )
    )
    SummaryDetailsRow(
        labelResID = R.string.upi,
        summaryDetail =  "${bill?.upi?:0.0}",
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.padding_medium)
        )
    )
    SummaryDetailsRow(
        labelResID = R.string.total,
        summaryDetail = "${bill?.total?:0.0}",
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.padding_medium)
        )
    )


}
@Composable
private fun SummaryDetailsRowHead(
    @StringRes labelResID: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(labelResID),
            style = MaterialTheme.typography.titleLarge,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SummaryDetailsRow(
    @StringRes labelResID: Int,
    summaryDetail: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(labelResID),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = summaryDetail,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )

    }
}
@Preview(showBackground = true)
@Composable
fun SummaryScreenPreview(){
    DukaanTheme {
        SummaryScreen({},{})
    }
}