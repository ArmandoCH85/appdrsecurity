package com.drsecuritygps.app.core.model

fun isOnlineStatus(status: String): Boolean =
    status.contains("online", ignoreCase = true) || status.contains("ack", ignoreCase = true)

fun hasRenderableAlarm(alarmRaw: String?): Boolean {
    val normalized = alarmRaw?.trim().orEmpty()
    return normalized.isNotEmpty() && normalized != "0" && normalized.lowercase() != "null"
}

fun DeviceSummary.hasRenderableCoordinates(): Boolean = hasValidCoordinates

fun deviceSubtitle(address: String, lastUpdate: String): String? {
    val normalizedAddress = address.trim()
    if (normalizedAddress.isNotEmpty() && normalizedAddress != "-") {
        return normalizedAddress
    }

    val normalizedUpdate = lastUpdate.trim()
    return normalizedUpdate.ifEmpty { null }
}

fun deviceStatusLabel(onlineStatus: String): String = when {
    isOnlineStatus(onlineStatus) -> "En línea"
    onlineStatus.contains("offline", ignoreCase = true) -> "Fuera de línea"
    onlineStatus.isBlank() -> "Sin estado"
    else -> onlineStatus
}

/** Convierte el campo `course` de Wox a grados (0 = Norte, horario); null si no hay dato. */
fun String?.toCourseDegrees(): Double? {
    val raw = this?.trim() ?: return null
    if (raw.isEmpty() || raw == "-") return null
    val v = raw.toDoubleOrNull() ?: return null
    if (!v.isFinite()) return null
    return ((v % 360.0) + 360.0) % 360.0
}
