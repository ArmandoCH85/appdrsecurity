package com.drsecuritygps.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drsecuritygps.app.core.model.HistoryPoint

@Composable
actual fun PlatformHistorySegmentMapPreview(
    points: List<HistoryPoint>,
    isStopSegment: Boolean,
    expanded: Boolean,
    modifier: Modifier,
    fallback: @Composable () -> Unit,
) {
    fallback()
}
