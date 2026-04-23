package com.drsecuritygps.app.core.model

/** Alinea estados de contacto/motor con la web (ON/OFF) aunque venga 1/0 o boolean. */
fun normalizeIgnitionDisplay(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    val s = raw.trim()
    return when (s) {
        "1", "100", "true", "TRUE", "on", "On" -> "ON"
        "0", "-1", "false", "FALSE", "off", "Off" -> "OFF"
        else -> s
    }
}
