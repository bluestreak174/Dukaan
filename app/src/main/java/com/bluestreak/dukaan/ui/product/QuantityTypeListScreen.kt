package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.QuantityType
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.viewmodel.QuantityTypeEntryViewModel

object QuantityTypeListScreenDestination : NavigationDestination {
    override val route = "quantity_type_list"
    override val titleRes = R.string.quantity_type_list_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantityTypeListScreen(
    modifier: Modifier = Modifier,
    navigateToQuantityTypeEntry: () -> Unit = {},
    navigateToQuantityTypeEdit: (Int) -> Unit = {},
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: QuantityTypeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val qtyTypeListUiState = viewModel.qtyTypeListUiState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(QuantityTypeListScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToQuantityTypeEntry() },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.quantity_type_entry_title)
                )
            }
        },
    ) {innerPadding ->
        QtyTypeListBody(
            qtyTypeList = qtyTypeListUiState.value.qtyTypeList,
            navigateToQtyTypeEdit = navigateToQuantityTypeEdit,
            contentPadding = innerPadding
        )
    }
}

@Composable
fun QtyTypeListBody(
    modifier: Modifier = Modifier,
    qtyTypeList: List<QuantityType>,
    navigateToQtyTypeEdit: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    QtyTypeList(
        qtyTypeList = qtyTypeList,
        navigateToQtyTypeEdit = navigateToQtyTypeEdit,
        contentPadding = contentPadding
    )

}

@Composable
fun QtyTypeList(
    qtyTypeList: List<QuantityType>,
    navigateToQtyTypeEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = qtyTypeList, key = { it.id }) { item ->
            QtyTypeCard(
                qtyType = item,
                navigateToQtyTypeEdit = navigateToQtyTypeEdit,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
                    .clickable {  }
            )
        }

    }
}

@Composable
fun QtyTypeCard(
    qtyType: QuantityType,
    navigateToQtyTypeEdit: (Int) -> Unit,
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
                text = "${qtyType.type} ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            Text(
                text = "${qtyType.piece} ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = { navigateToQtyTypeEdit(qtyType.id)}
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.product)
                )
            }
        }
    }
}

