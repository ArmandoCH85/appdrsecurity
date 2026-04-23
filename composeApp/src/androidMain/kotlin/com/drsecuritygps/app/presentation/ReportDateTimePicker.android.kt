package com.drsecuritygps.app.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
actual fun PlatformReportDateTimePickerField(
    label: String,
    date: String,
    time: String,
    onDateTimeSelected: (String, String) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val currentDate = remember(date) { parseDateParts(date) }
    val currentTime = remember(time) { parseTimeParts(time) }

    OutlinedButton(
        onClick = {
            val initialDate = parseDateParts(date)
            val initialTime = parseTimeParts(time)
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            onDateTimeSelected(
                                formatDate(year, month + 1, dayOfMonth),
                                formatTime(hourOfDay, minute),
                            )
                        },
                        initialTime.first,
                        initialTime.second,
                        true,
                    ).show()
                },
                initialDate.first,
                initialDate.second - 1,
                initialDate.third,
            ).show()
        },
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            Text(label)
            Text("${formatDate(currentDate.first, currentDate.second, currentDate.third)} ${formatTime(currentTime.first, currentTime.second)}")
        }
    }
}

private fun parseDateParts(value: String): Triple<Int, Int, Int> {
    val parts = value.split("-")
    return if (parts.size == 3) {
        Triple(parts[0].toIntOrNull() ?: 2026, parts[1].toIntOrNull() ?: 1, parts[2].toIntOrNull() ?: 1)
    } else {
        val now = Calendar.getInstance()
        Triple(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH))
    }
}

private fun parseTimeParts(value: String): Pair<Int, Int> {
    val parts = value.split(":")
    return if (parts.size >= 2) {
        Pair(parts[0].toIntOrNull() ?: 0, parts[1].toIntOrNull() ?: 0)
    } else {
        val now = Calendar.getInstance()
        Pair(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
    }
}

private fun formatDate(year: Int, month: Int, day: Int): String =
    "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

private fun formatTime(hour: Int, minute: Int): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
