package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.relations.BillDetails
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.viewmodel.PurchaseBillDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

object PurchaseBillDetailsDestination: NavigationDestination {
    override val route = "purchase_bill_details"
    override val titleRes = R.string.purchase_bill_details
    const val billIdArg = "billId"
    val routeWithArgs = "$route/{$billIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseBillDetailsScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PurchaseBillDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val billDetailsUiState by viewModel.billDetailsUiState.collectAsState()
    val billDetailsList: List<BillDetails> = billDetailsUiState.billDetailsList
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(PurchaseBillDetailsDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },

    ) { innerPadding ->
        BillDetailsBody(
            billDetailsList = billDetailsList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )

    }
}

@Composable
fun BillDetailsBody(
    billDetailsList: List<BillDetails>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    BillDetailsList(
        billDetailsList = billDetailsList,
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Composable
fun BillDetailsList(
    billDetailsList: List<BillDetails>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
){
    var billAddress = ""
    var billDate = ""
    if(billDetailsList.isNotEmpty()) {
        val formattedDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(billDetailsList[0].billDate)
        val formattedTime =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(billDetailsList[0].billDate)
        billDate = " ($formattedDate $formattedTime)"
        billAddress = billDetailsList[0].billAddress + " " + billDate

    }
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            PurchaseBillCardHead(billAddress = billAddress)
        }
        items(items = billDetailsList) { item ->
            PurchaseBillCard(
                billDetails = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
            )
        }

    }
}

@Composable
fun PurchaseBillCard(
    billDetails: BillDetails,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = billDetails.productName,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = "${billDetails.qty}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = billDetails.qtyType,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = "${billDetails.amount}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
        }
    }
}

@Composable
fun PurchaseBillCardHead(
    modifier: Modifier = Modifier,
    billAddress: String = ""
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            //horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = billAddress,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.product),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = stringResource(R.string.qty),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = stringResource(R.string.qty_type),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
            Text(
                text = stringResource(R.string.cost),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp).weight(0.5f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseBillDetailsPreview(){
    DukaanTheme {
        PurchaseBillDetailsScreen({},{})
    }
}