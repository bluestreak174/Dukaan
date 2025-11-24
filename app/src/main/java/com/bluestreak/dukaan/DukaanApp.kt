@file:OptIn(ExperimentalMaterial3Api::class)
package com.bluestreak.dukaan

import android.text.Layout.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Blinds
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
@OptIn(ExperimentalMaterial3Api::class)
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
            if(!canNavigateBack) {
                IconWithToolTip(
                    modifier = Modifier.padding(start = 150.dp, top = 30.dp),
                    tooltipText = stringResource((R.string.home_summary_tooltip)),
                    iconImageVector = Icons.Filled.Assistant,
                    iconColor = Color.Gray
                )
            }
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
            PlainTooltip { Text(tooltipText) }
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
