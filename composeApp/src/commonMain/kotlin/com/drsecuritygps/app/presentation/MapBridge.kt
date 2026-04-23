package com.drsecuritygps.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drsecuritygps.app.core.model.DeviceLivePosition
import com.drsecuritygps.app.core.model.MapCapability

@Composable
expect fun PlatformDeviceMap(
    devices: List<DeviceLivePosition>,
    routePath: List<Pair<Double, Double>> = emptyList(),
    selectedDeviceId: String?,
    showLabels: Boolean = true,
    showControls: Boolean = false,
    /** Sobre [navigationBars] + padding base: deja visibles +/− encima de la barra de pestañas. */
    controlsBottomExtraPadding: Dp = 0.dp,
    capability: MapCapability = MapCapability.Available(),
    onDeviceSelected: (String) -> Unit,
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
    onRecenter: () -> Unit = {},
    onToggleLayer: () -> Unit = {},
    onUnavailableAction: () -> Unit = {},
    modifier: Modifier = Modifier,
)
