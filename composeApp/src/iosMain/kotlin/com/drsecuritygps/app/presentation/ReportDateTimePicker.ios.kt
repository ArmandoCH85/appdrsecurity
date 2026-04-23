package com.drsecuritygps.app.presentation

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformReportDateTimePickerField(
    label: String,
    date: String,
    time: String,
    onDateTimeSelected: (String, String) -> Unit,
    modifier: Modifier,
) {
    OutlinedButton(
        onClick = {},
        enabled = false,
        modifier = modifier,
    ) {
        Text("$label: $date $time")
    }
}
