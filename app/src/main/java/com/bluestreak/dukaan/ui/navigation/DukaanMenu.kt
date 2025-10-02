package com.bluestreak.dukaan.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bluestreak.dukaan.R

@Composable
fun DukaanDropdownMenu(
    onCategoryClick: () -> Unit,
    onQuantityTypeClick: () -> Unit,
    onImageEntryClick: () -> Unit,
    onPurchaseListClick: () -> Unit,
    onSalesListClick: () -> Unit,
    onPurchaseBillClick: () -> Unit,
    onSalesBillClick: () -> Unit,
    onSummaryClick: () -> Unit,
    onPbillsClick: () -> Unit,
    onSbillsClick: () -> Unit,
    onPurchaseSalesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.more_options_test))) {
            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.more_options))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_purchase_bill_list)) },
                onClick = onPurchaseBillClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_purchase_bill_test))) {
                        Icon(Icons.Default.AddCircle, contentDescription = stringResource(R.string.menu_purchase_bill_list))
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_sales_bill_list)) },
                onClick = onSalesBillClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_sales_bill_test))) {
                        Icon(Icons.Default.Create, contentDescription = stringResource(R.string.menu_sales_bill_list))
                    }
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_summary)) },
                onClick = onSummaryClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_purchase_list_test))) {
                        Icon(Icons.Default.CheckCircle, contentDescription = stringResource(R.string.menu_summary))
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_purchase_list)) },
                onClick = onPurchaseListClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_purchase_list_test))) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.menu_purchase_list))
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_sales_list)) },
                onClick = onSalesListClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_sales_list_test))) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.menu_sales_list))
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_profit_and_loss)) },
                onClick = onPurchaseSalesClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_purchase_sales_test))) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.menu_purchase_sales_list))
                    }
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_bills_list)) },
                onClick = onPbillsClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_purchase_bills_test))) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.menu_bills_list))
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_sales_bills_list)) },
                onClick = onSbillsClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_sales_bills_test))) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.menu_sales_bills_list))
                    }
                }
            )

            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_category_list)) },
                onClick = onCategoryClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_category_list_test))) {
                        Icon(Icons.Default.Email, contentDescription = stringResource(R.string.menu_category_list))
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_qty_type_list)) },
                onClick = onQuantityTypeClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_qty_type_list_test))) {
                        Icon(Icons.Default.Email, contentDescription = stringResource(R.string.menu_qty_type_list))
                    }
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_image_entry)) },
                onClick = onImageEntryClick,
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag(stringResource(R.string.menu_image_entry_test))) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.menu_image_entry))
                    }
                }
            )
        }
    }
}