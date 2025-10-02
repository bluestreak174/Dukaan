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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.Category
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.theme.DukaanTheme
import com.bluestreak.dukaan.ui.viewmodel.CategoryEntryViewModel

object CategoryListDestination : NavigationDestination {
    override val route = "category_list"
    override val titleRes = R.string.category_list_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    modifier: Modifier = Modifier,
    navigateToCategoryEntry: () -> Unit = {},
    navigateToCategoryEdit: (Int) -> Unit = {},
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    viewModel: CategoryEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val categoryListUiState = viewModel.categoryListUiState.collectAsState()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                title = stringResource(CategoryListDestination.titleRes),
                canNavigateBack = canNavigateBack,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToCategoryEntry() },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.product_entry_title)
                )
            }
        },
    ) { innerPadding ->
        CategoryListBody(
            categoryList = categoryListUiState.value.categoryList,
            navigateToCategoryEdit = navigateToCategoryEdit,
            contentPadding = innerPadding
        )

    }

}

@Composable
fun CategoryListBody(
    modifier: Modifier = Modifier,
    categoryList: List<Category>,
    navigateToCategoryEdit: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    CategoryList(
        categoryList = categoryList,
        navigateToCategoryEdit = navigateToCategoryEdit,
        contentPadding = contentPadding
    )

}

@Composable
fun CategoryList(
    categoryList: List<Category>,
    navigateToCategoryEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = categoryList, key = { it.id }) { item ->
            CategoryCard(
                category = item,
                navigateToCategoryEdit = navigateToCategoryEdit,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.card_padding_small))
                    .clickable {  }
            )
        }

    }
}

@Composable
fun CategoryCard(
    category: Category,
    navigateToCategoryEdit: (Int) -> Unit,
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
                text = "${category.name} ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = { navigateToCategoryEdit(category.id) }
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.product)
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun CategoryListScreenPreview(){
    DukaanTheme {
        CategoryListScreen()
    }
}
