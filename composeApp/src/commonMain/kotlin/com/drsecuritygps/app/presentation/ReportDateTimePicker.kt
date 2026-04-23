package com.drsecuritygps.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformReportDateTimePickerField(
    label: String,
    date: String,
    time: String,
    onDateTimeSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier,
)
