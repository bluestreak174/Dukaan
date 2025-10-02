package com.bluestreak.dukaan.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bluestreak.dukaan.R


@Composable
fun FilterButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(R.string.filter_expand_button_content_description),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun FilterCheckBox(
    modifier: Modifier = Modifier,
    filterText: String,
    onCheckedChange: (Boolean) -> Unit,
    filterChecked: Boolean
){

    var checked by remember { mutableStateOf(filterChecked) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                onCheckedChange(it)
            }
        )

        Text(
            text = filterText,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(4.dp)
        )
    }
}

@Composable
fun RowsFilter(
    modifier: Modifier = Modifier,
    filterMap: MutableMap<String, Boolean>,
    onCheckedChange: (Map<String, Boolean>) -> Unit
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )
    ) {
        for(filter in filterMap){
            Row(
                modifier = Modifier.fillMaxWidth()
                    //.verticalScroll(rememberScrollState()),
                //horizontalArrangement = Arrangement.Center
            ){

                FilterCheckBox(
                    filterText = filter.key,
                    filterChecked = filter.value,
                    onCheckedChange = {
                        filterMap.set(filter.key, it)
                        onCheckedChange(filterMap)
                    }
                )

            }
        }

    }
}

@Composable
fun RowsMultiMapFilter(
    modifier: Modifier = Modifier,
    filterMap: MutableMap<String, Boolean>,
    filterSubFilterMap: MutableMap<String, MutableMap<String, Boolean>>,
    onCheckedChange: (MutableMap<String, MutableMap<String, Boolean>>) -> Unit,
    onOuterCheckedChange: (MutableMap<String, Boolean>) -> Unit
){

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )
    ) {
        for(outerFilterMap in filterSubFilterMap) {
            Column() {
                var outerFilterExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterCheckBox(
                        filterText = outerFilterMap.key,
                        filterChecked = filterMap.get(outerFilterMap.key) ?: true,
                        onCheckedChange = {
                            filterMap[outerFilterMap.key] = it
                            onOuterCheckedChange(filterMap)
                        }
                    )
                    FilterButton(
                        expanded = outerFilterExpanded,
                        onClick = { outerFilterExpanded = !outerFilterExpanded}
                    )
                }
                for (innerFilterMap in outerFilterMap.value) {
                    if(outerFilterExpanded){
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        ) {
                            FilterCheckBox(
                                filterText = innerFilterMap.key,
                                filterChecked = innerFilterMap.value,
                                onCheckedChange = {
                                    filterSubFilterMap[outerFilterMap.key]?.set(innerFilterMap.key, it)
                                    onCheckedChange(filterSubFilterMap)
                                }
                            )
                        }
                    }

                }

            }
        }
    }
}




@Composable
fun TotalAndFilteredRowsCount(
    modifier: Modifier = Modifier,
    totalRowsCount: Int = 0,
    filteredRowsCount: Int = 0
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        /*colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )*/
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(
                    R.string.displaying_of,
                    filteredRowsCount,
                    totalRowsCount
                ),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}
