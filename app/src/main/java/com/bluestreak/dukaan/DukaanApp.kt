@file:OptIn(ExperimentalMaterial3Api::class)
package com.bluestreak.dukaan

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bluestreak.dukaan.ui.navigation.DukaanDropdownMenu
import com.bluestreak.dukaan.ui.navigation.DukaanNavHost


/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun DukaanApp(navController: NavHostController = rememberNavController()) {
    DukaanNavHost(navController = navController)
}

/**
 * App bar to display title and conditionally display the back navigation.
 */
@Composable
fun DukaanTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onCategoryClick: () -> Unit = {},
    onQuantityTypeClick: () -> Unit = {},
    onImageEntryClick: () -> Unit = {},
    onPurchaseListClick: () -> Unit = {},
    onSalesListClick: () -> Unit = {},
    onPurchaseBillClick: () -> Unit = {},
    onSalesBillClick: () -> Unit = {},
    onSummaryClick: () -> Unit = {},
    onPbillsClick: () -> Unit = {},
    onSbillsClick: () -> Unit = {},
    onPurchaseSalesClick: () -> Unit = {},
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    navigateToHome: () -> Unit ={}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
                },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.home_content_description)
                )
            }
        },
        actions = {
            if(!canNavigateBack) {
                DukaanDropdownMenu(
                    onCategoryClick = onCategoryClick,
                    onQuantityTypeClick = onQuantityTypeClick,
                    onImageEntryClick = onImageEntryClick,
                    onPurchaseListClick = onPurchaseListClick,
                    onSalesListClick = onSalesListClick,
                    onPurchaseBillClick = onPurchaseBillClick,
                    onSalesBillClick = onSalesBillClick,
                    onSummaryClick = onSummaryClick,
                    onPbillsClick = onPbillsClick,
                    onSbillsClick = onSbillsClick,
                    onPurchaseSalesClick = onPurchaseSalesClick
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.home_content_description)
                )
            }
        }
    )
}