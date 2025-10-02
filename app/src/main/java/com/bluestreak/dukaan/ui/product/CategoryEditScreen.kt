package com.bluestreak.dukaan.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.ui.AppViewModelProvider
import com.bluestreak.dukaan.ui.navigation.NavigationDestination
import com.bluestreak.dukaan.ui.viewmodel.CategoryDetails
import com.bluestreak.dukaan.ui.viewmodel.CategoryEditViewModel
import com.bluestreak.dukaan.ui.viewmodel.CategoryUiState
import kotlinx.coroutines.launch

object CategoryEditDestination : NavigationDestination {
    override val route = "category_edit"
    override val titleRes = R.string.edit_category_title
    const val catIdArg = "categoryId"
    val routeWithArgs = "$route/{$catIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    val categoryUiState = viewModel.categoryUiState
    Scaffold(
        topBar = {
            DukaanTopAppBar(
                title = stringResource(CategoryEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        CategoryEditBody(
            categoryUiState = categoryUiState,
            onCategoryValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateCategory()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState()),
        )
    }
}

@Composable
fun CategoryEditBody(
    categoryUiState: CategoryUiState,
    onCategoryValueChange: (CategoryDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = categoryUiState.categoryDetails.name,
            onValueChange = { onCategoryValueChange(categoryUiState.categoryDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.category_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        Button(
            onClick = onSaveClick,
            enabled = categoryUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}