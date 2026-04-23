package com.drsecuritygps.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drsecuritygps.app.core.model.HistoryPoint

@Composable
expect fun PlatformHistorySegmentMapPreview(
    points: List<HistoryPoint>,
    isStopSegment: Boolean,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    fallback: @Composable () -> Unit = {},
)
