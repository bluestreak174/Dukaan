package com.bluestreak.dukaan.ui.product

import android.util.Log
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
import androidx.compose.runtime.remember
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
import com.bluestreak.dukaan.data.entities.PurchaseBill
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.utils.DateRangePickerDukaan
import com.bluestreak.dukaan.ui.utils.FilterButton
import com.bluestreak.dukaan.ui.utils.RowsFilter
import com.bluestreak.dukaan.ui.utils.TotalAndFilteredRowsCount
import com.bluestreak.dukaan.ui.viewmodel.PurchaseBillsListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

object PurchaseBillsListDestination : NavigationDestination {
    override val route = "purchase_bill_list"
    override val titleRes = R.string.purchase_bill_list_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseBillsList(
    modifier: Modifier = Modifier,
    navigateToBillDetails: (Int) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: PurchaseBillsListViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val billsPurchaseState by viewModel.billsPurchaseUiState.collectAsState()
    val filteredBillsPurchaseState by viewModel.filteredBillsPurchaseState.collectAsState()
    val selectedDateRangeState by viewModel.selectedDateRangeState.collectAsState()
    val selectedDateRange = Pair(selectedDateRangeState.startDate, selectedDateRangeState.endDate)


    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(PurchaseBillsListDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->

        PurchaseBIllsListBody(
            purchaseBillsList = billsPurchaseState.billsPurchaseList,
            filteredBillsList = filteredBillsPurchaseState.filteredBillsList,
            addressFilterMap = filteredBillsPurchaseState.billAddressFilterMap.toMutableMap(),
            onDateChange = viewModel::updateUiDateState,
            onDeleteBill = viewModel::deletePurchaseBill,
            updateFilterData = viewModel::updateFilterData,
            onBillClick = navigateToBillDetails,
            modifier = modifier.fillMaxSize(),
            selectedDateRange = selectedDateRange,
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun PurchaseBIllsListBody(
    purchaseBillsList: List<PurchaseBill>,
    filteredBillsList: List<PurchaseBill>,
    addressFilterMap: MutableMap<String, Boolean>,
    onDateChange: (Long, Long) -> Unit,
    onDeleteBill: (PurchaseBill) -> Unit,
    onBillClick: (Int) -> Unit,
    updateFilterData: (Map<String, Boolean>) -> Unit,
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
        PurchaseBillsListRows(
            purchaseBillsList = purchaseBillsList,
            filteredPurchaseBillsList = filteredBillsList,
            billAddressMap = addressFilterMap,
            onDeleteBill = onDeleteBill,
            onBillClick = onBillClick,
            updateFilterData = updateFilterData,
            modifier = modifier
        )

    }
}


@Composable
fun PurchaseBillsListRows(
    purchaseBillsList: List<PurchaseBill>,
    filteredPurchaseBillsList: List<PurchaseBill>,
    billAddressMap: MutableMap<String, Boolean>,
    onDeleteBill: (PurchaseBill) -> Unit,
    onBillClick: (Int) -> Unit,
    updateFilterData: (Map<String, Boolean>) -> Unit,
    modifier: Modifier = Modifier
){

    val headTextList = listOf(
        stringResource(R.string.bill_address) + "\n" + stringResource(R.string.date),
        stringResource(R.string.cash) + "\n" ,
        stringResource(R.string.upi) + "\n" ,
        stringResource(R.string.total) + "\n"
    )

    LazyColumn(
        modifier = modifier,
        //contentPadding = contentPadding
    ) {
        item {
            BillPurchasesCardHead(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small)),
                headTextList = headTextList,
                billAddressMap = billAddressMap,
                onFilterChanged = updateFilterData,
                filteredList = filteredPurchaseBillsList,
                purchaseBillsList = purchaseBillsList,
                containerColor = Color.LightGray
            )
        }
        items(items = filteredPurchaseBillsList){ item ->
            PurchaseBillCard(
                purchaseBill = item,
                onDeleteBill = onDeleteBill,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
                    .clickable { onBillClick(item.id) },
            )
        }

    }



}

@Composable
fun PurchaseBillCard(
    purchaseBill: PurchaseBill,
    onDeleteBill: (PurchaseBill) -> Unit,
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
                    text = "${purchaseBill.id}",
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
                        onDeleteBill(purchaseBill)
                    },
                    onDeleteCancel = { deleteConfirmationRequired = false },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
            }
            val formattedDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(purchaseBill.billDate)
            val formattedTime =
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(purchaseBill.billDate)

            Column (
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "${purchaseBill.billAddress} \n" + formattedDate,
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
                text = "${purchaseBill.cash}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = "${purchaseBill.upi}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.5f)
            )
            Text(
                text = "${purchaseBill.total}",
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
fun BillPurchasesCardHead(
    modifier: Modifier = Modifier,
    headTextList: List<String> = listOf(),
    billAddressMap: MutableMap<String, Boolean> = mutableMapOf(),
    onFilterChanged: (Map<String, Boolean>) -> Unit,
    filteredList: List<PurchaseBill>,
    purchaseBillsList: List<PurchaseBill>,
    containerColor: Color
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
                    RowsFilter(
                        filterMap = billAddressMap,
                        onCheckedChange = onFilterChanged
                    )
                } else {
                    TotalAndFilteredRowsCount(
                        totalRowsCount = purchaseBillsList.size,
                        filteredRowsCount = filteredList.size
                    )
                }
            }
            Row(
                modifier = Modifier
            ) {
                FilterButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                )
                Text(
                    text = headTextList[0],
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                )
                Text(
                    text = headTextList[1] + "\n" + filteredList.sumOf { it.cash },
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.5f)
                )
                Text(
                    text = headTextList[2] + "\n" + filteredList.sumOf { it.upi },
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.5f)
                )
                Text(
                    text = headTextList[3] + "\n" + filteredList.sumOf { it.total },
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.5f)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseBillsListPreview(
    modifier: Modifier = Modifier
){
    DukaanTheme {
        PurchaseBillsList(modifier, {}, {}, {})
    }

}