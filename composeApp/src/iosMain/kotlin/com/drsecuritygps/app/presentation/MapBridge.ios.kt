package com.drsecuritygps.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.drsecuritygps.app.core.model.DeviceLivePosition
import com.drsecuritygps.app.core.model.MapCapability

@Composable
actual fun PlatformDeviceMap(
    devices: List<DeviceLivePosition>,
    routePath: List<Pair<Double, Double>>,
    selectedDeviceId: String?,
    showLabels: Boolean,
    showControls: Boolean,
    controlsBottomExtraPadding: Dp,
    capability: MapCapability,
    onDeviceSelected: (String) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onRecenter: () -> Unit,
    onToggleLayer: () -> Unit,
    onUnavailableAction: () -> Unit,
    modifier: Modifier,
) {
    Box(modifier = modifier.background(Color(0xFFF3F3F3))) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Mapa iOS")
            if (capability is MapCapability.Unavailable) {
                onUnavailableAction()
                Text(capability.reason)
                return@Column
            }
            devices.take(5).forEach { device ->
                Text("${if (device.id == selectedDeviceId) "•" else "○"} ${device.title} ${device.latitude}, ${device.longitude}")
            }
        }
    }
}
