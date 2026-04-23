package com.drsecuritygps.app.network

import com.drsecuritygps.app.core.model.AlertSeverity

internal fun mapAlertSeverity(type: String): AlertSeverity = when (type) {
    "sos", "overspeed", "fuel_theft", "unplugged" -> AlertSeverity.Critical
    "idle_duration", "offline_duration" -> AlertSeverity.Warning
    else -> AlertSeverity.Info
}
