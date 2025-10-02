package com.bluestreak.dukaan.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.ZoneId
import java.util.Date
import java.util.Locale


val FINANCIAL_START: MonthDay? = MonthDay.of(4, 1)

private fun getStartOfFinancialYear(date: LocalDate): LocalDate {
    // Try "the same year as the date we've been given"
    val defaultDate = date.with(FINANCIAL_START)
    // If we haven't reached that yet, subtract a year. Otherwise, use it.
    return if (defaultDate.isAfter(date)) defaultDate.minusYears(1) else defaultDate
}

@Composable
fun DateRangePickerDukaan(
    onValChange: (Long, Long) -> Unit,
    selectedStartDateMillis: Long = 0,
    selectedEndDateMillis: Long = 0,
    isFinYear: Boolean = false,
) {
    val defaultDate = getStartOfFinancialYear(LocalDate.now(ZoneId.systemDefault()))
    var startDateMillis: Long =
        LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    if(isFinYear) startDateMillis = defaultDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    var endDateMillis: Long =
        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    if(selectedStartDateMillis > 0 && selectedEndDateMillis > 0) {
        startDateMillis = selectedStartDateMillis
        endDateMillis = selectedEndDateMillis
    }

    var selectedDateRange by remember { mutableStateOf<Pair<Long?, Long?>>(startDateMillis to endDateMillis) }
    var showRangeModal by remember { mutableStateOf(false) }
    val startDate = Date(selectedDateRange.first?:startDateMillis)
    val endDate = Date(selectedDateRange.second?:endDateMillis)
    val formattedStartDate =
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(startDate)
    val formattedEndDate =
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(endDate)

    onValChange(selectedDateRange.first?:startDateMillis, selectedDateRange.second?:endDateMillis)

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showRangeModal = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date Range"
                )
            }
            Text(
                text = "Date range: $formattedStartDate - $formattedEndDate",
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (showRangeModal) {
            DateRangePickerModal(
                onDateRangeSelected = {
                    selectedDateRange = it
                    showRangeModal = false
                },
                onDismiss = { showRangeModal = false },
                startDateMillis = startDateMillis,
                endDateMillis = endDateMillis,
            )
        }
    }

}