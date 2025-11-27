@file:OptIn(ExperimentalMaterial3Api::class)
package com.addendtek.dukaan


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.addendtek.dukaan.ui.navigation.DukaanDropdownMenu
import com.addendtek.dukaan.ui.navigation.DukaanNavHost


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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DukaanTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    titleSummary: String = "",
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
    onSettingsClick: () -> Unit = {},
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    navigateToHome: () -> Unit ={}
) {
    CenterAlignedTopAppBar(
        title = {

                if(!canNavigateBack) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconWithToolTip(
                                tooltipText = titleSummary,
                                iconImageVector = Icons.Filled.Assistant,
                                iconColor = Color.Gray
                            )
                            Text(
                                text = titleSummary.split("\n").last(),
                                style = MaterialTheme.typography.labelLarge,
                            )

                        }
                    }
                }else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

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
                    onPurchaseSalesClick = onPurchaseSalesClick,
                    onSettingsClick = onSettingsClick,
                    modifier = Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconWithToolTip(
    modifier: Modifier = Modifier,
    tooltipText: String = stringResource(R.string.app_name),
    iconImageVector: ImageVector = Icons.Filled.Favorite,
    iconColor: Color = Color.Blue,
    onIconClick: () -> Unit = {},
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(
                    text = tooltipText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onIconClick) {
            Icon(
                imageVector = iconImageVector,
                contentDescription = tooltipText,
                tint = iconColor
            )
        }
    }
}
