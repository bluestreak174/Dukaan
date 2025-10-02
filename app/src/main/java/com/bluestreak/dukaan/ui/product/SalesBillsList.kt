package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.SalesBill
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.utils.DateRangePickerDukaan
import com.bluestreak.dukaan.ui.viewmodel.SalesBillsListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

object SalesBillsListDestination : NavigationDestination {
    override val route = "sales_bill_list"
    override val titleRes = R.string.sales_bill_list_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesBillsList(
    modifier: Modifier = Modifier,
    navigateToBillDetails: (Int) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: SalesBillsListViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val billsSalesUiState by viewModel.billsSalesUiState.collectAsState()

    val selectedDateRangeState = viewModel.selectedDateRangeState.collectAsState()
    val selectedDateRange = Pair(selectedDateRangeState.value.startDate, selectedDateRangeState.value.endDate)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(SalesBillsListDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        SalesBIllsListBody(
            salesBillsList = billsSalesUiState.billsSalesList,
            onDeleteBill = viewModel::deleteSalesBill,
            onDateChange = viewModel::updateUiDateState,
            onBillClick = navigateToBillDetails,
            selectedDateRange = selectedDateRange,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun SalesBIllsListBody(
    salesBillsList: List<SalesBill>,
    onDeleteBill: (SalesBill) -> Unit,
    onDateChange: (Long, Long) -> Unit,
    onBillClick: (Int) -> Unit,
    selectedDateRange: Pair<Long, Long>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    Column (modifier = Modifier.padding(top =80.dp)) {
        DateRangePickerDukaan(
            onValChange = onDateChange,
            selectedStartDateMillis = selectedDateRange.first,
            selectedEndDateMillis = selectedDateRange.second,
        )
        SalesBillsListRows(
            salesBillsList = salesBillsList,
            onDeleteBill = onDeleteBill,
            onBillClick = onBillClick,
            modifier = modifier
        )

    }
}

@Composable
fun SalesBillsListRows(
    salesBillsList: List<SalesBill>,
    onDeleteBill: (SalesBill) -> Unit,
    onBillClick: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    val headTextList = listOf(
        stringResource(R.string.bill_address) + "\n" + stringResource(R.string.date),
        stringResource(R.string.cash),
        stringResource(R.string.upi),
        stringResource(R.string.total))
    BillSalesCardHead(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.card_padding_small)),
        headTextList = headTextList,
        containerColor = Color.LightGray
    )
    LazyColumn(
        modifier = modifier,
        //contentPadding = contentPadding
    ) {
        items(items = salesBillsList) { item ->
            SalesBillCard(
                salesBill = item,
                onDeleteBill = onDeleteBill,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
                    .clickable { onBillClick(item.id) },
            )
        }
    }


}

@Composable
fun SalesBillCard(
    salesBill: SalesBill,
    onDeleteBill: (SalesBill) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

        ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        Row(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${salesBill.id}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(2.dp)
                )
                IconButton(
                    onClick = { deleteConfirmationRequired = true },
                    enabled = true,
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        deleteConfirmationRequired = false
                        onDeleteBill(salesBill)
                    },
                    onDeleteCancel = { deleteConfirmationRequired = false },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
            }
            val formattedDate =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(salesBill.billDate)
            val formattedTime =
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(salesBill.billDate)


            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "${salesBill.billAddress} \n" + formattedDate,
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
            }
            Text(
                text = "${salesBill.cash}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = "${salesBill.upi}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = "${salesBill.total}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_bill)) },
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
        })
}

@Composable
fun BillSalesCardHead(
    modifier: Modifier = Modifier,
    headTextList: List<String> = listOf(),
    containerColor: Color
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
       /* colors = CardColors(
            containerColor = containerColor,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )*/
    ) {

        Row(
            modifier = Modifier
        ) {
            Text(
                text = "  --",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = headTextList[0],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
            )
            Text(
                text = headTextList[1],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = headTextList[2],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = headTextList[3],
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalesBillsListPreview(
    modifier: Modifier = Modifier
){
    DukaanTheme {
        SalesBillsList(modifier, {},{},{})
    }

}