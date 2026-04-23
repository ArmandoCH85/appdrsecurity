package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.model.DeviceSensorListItem
import com.drsecuritygps.app.core.model.DeviceSensorRow
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.normalizeIgnitionDisplay

internal fun List<DeviceSensorRow>.enrichDeviceSummary(device: DeviceSummary): DeviceSummary {
    var engine: String? = null
    var battery: String? = null
    var satellites: String? = null
    val usedKeys = mutableSetOf<String>()

    forEach { row ->
        val text = row.valueForDisplay().let { normalizeIgnitionDisplay(it) ?: it }
        val key = row.dedupeKey()
        when {
            row.isEngineSensor() && engine == null -> {
                engine = text
                usedKeys.add(key)
            }
            row.isBatterySensor() && battery == null -> {
                battery = text
                usedKeys.add(key)
            }
            row.isSatelliteSensor() && satellites == null -> {
                satellites = text
                usedKeys.add(key)
            }
        }
    }

    val apiExtras = mapNotNull { row ->
        if (row.dedupeKey() in usedKeys) return@mapNotNull null
        val label = row.extrasLabel() ?: return@mapNotNull null
        DeviceSensorListItem(label = label, value = row.valueForDisplay())
    }

    val mergedExtras = mergeSensorExtras(device.embeddedSensorRows, apiExtras)

    val apiEngine = engine?.takeUnless { it == "Sin dato" }
    val ignPreferred = device.ignition?.let { normalizeIgnitionDisplay(it) ?: it }
    /** El encendido de `get_devices` suele ser más fiable que códigos numéricos de `get_sensors`. */
    val engineForUi = (ignPreferred ?: apiEngine)?.takeUnless { it == "Sin dato" }

    return device.copy(
        sensorEngineDisplay = engineForUi,
        sensorBatteryDisplay = battery?.takeUnless { it == "Sin dato" },
        sensorSatellitesDisplay = satellites?.takeUnless { it == "Sin dato" },
        sensorExtraRows = mergedExtras,
    )
}

internal fun mergeSensorExtras(
    embedded: List<DeviceSensorListItem>,
    fromApi: List<DeviceSensorListItem>,
): List<DeviceSensorListItem> {
    val merged = linkedMapOf<String, DeviceSensorListItem>()
    fun put(item: DeviceSensorListItem) {
        val ck = canonicalExtraSensorKey(item.label)
        val existing = merged[ck]
        merged[ck] = when {
            existing == null -> item
            else -> pickBetterSensorListItem(existing, item)
        }
    }
    embedded.forEach { put(it) }
    fromApi.forEach { put(it) }
    return merged.values.toList()
}

/** Agrupa etiquetas equivalentes (p. ej. `Odometer` / `Odómetro`) para no duplicar chips en la lista. */
internal fun canonicalExtraSensorKey(label: String): String {
    val n = label.trim().lowercase().foldAccents()
    val compact = n.filter { it.isLetterOrDigit() }
    return when {
        n.contains("odometer") || n.contains("odometro") ||
            (n.contains("odo") && n.contains("metro")) -> "kind:odometer"
        else -> "lbl:$compact"
    }
}

private fun String.foldAccents(): String = this
    .replace("á", "a")
    .replace("é", "e")
    .replace("í", "i")
    .replace("ó", "o")
    .replace("ú", "u")
    .replace("ñ", "n")

private fun pickBetterSensorListItem(a: DeviceSensorListItem, b: DeviceSensorListItem): DeviceSensorListItem {
    val aMissing = a.value.isMissingSensorValue()
    val bMissing = b.value.isMissingSensorValue()
    when {
        aMissing && !bMissing -> return b
        bMissing && !aMissing -> return a
    }
    val na = a.valueNormalizedForDedupe()
    val nb = b.valueNormalizedForDedupe()
    if (na.isNotEmpty() && na == nb) {
        return if (spanishLabelPreference(a.label) >= spanishLabelPreference(b.label)) a else b
    }
    return if (nb.length >= na.length) b else a
}

private fun String.isMissingSensorValue(): Boolean {
    val t = trim()
    return t.isEmpty() || t == "-" || t.equals("Sin dato", ignoreCase = true)
}

private fun DeviceSensorListItem.valueNormalizedForDedupe(): String =
    value.trim().lowercase().filter { it.isLetterOrDigit() || it == '.' || it == ',' }

private fun spanishLabelPreference(label: String): Int {
    var score = 0
    if (label.any { it in "áéíóúñÁÉÍÓÚÑ" }) score += 3
    if (label.contains("ómetro", ignoreCase = true)) score += 1
    return score
}

private fun DeviceSensorRow.dedupeKey(): String =
    "${type.lowercase()}|${tagName.lowercase()}|${name.lowercase()}"

/** Valor legible; nunca vacío (el panel suele mandar "-" sin dato). */
private fun DeviceSensorRow.valueForDisplay(): String {
    val v = value.trim()
    if (v.isEmpty() || v == "-") return "Sin dato"
    val u = unit.trim()
    return if (u.isNotEmpty()) "$v $u" else v
}

private fun DeviceSensorRow.extrasLabel(): String? =
    typeTitle?.trim()?.takeIf { it.isNotEmpty() }
        ?: name.trim().takeIf { it.isNotEmpty() }
        ?: type.trim().takeIf { it.isNotEmpty() }

private fun DeviceSensorRow.isEngineSensor(): Boolean {
    val t = type.lowercase()
    if (t == "engine" || t == "ignition" || t == "acc") return true
    val tag = tagName.lowercase()
    if (tag in ENGINE_TAGS) return true
    val n = name.lowercase()
    return n.contains("engine") || n.contains("motor") || n.contains("encendido") ||
        n.contains("ignition") || n.contains("contacto") || n.contains("arranque")
}

private fun DeviceSensorRow.isBatterySensor(): Boolean {
    val t = type.lowercase()
    if (t == "battery" || t == "power") return true
    val tag = tagName.lowercase()
    if (tag in BATTERY_TAGS) return true
    val n = name.lowercase()
    return n.contains("bater") || n.contains("battery") || n.contains("volt")
}

private fun DeviceSensorRow.isSatelliteSensor(): Boolean {
    val t = type.lowercase()
    if (t == "satellite" || t == "gnss") return true
    val tag = tagName.lowercase()
    if (tag in SAT_TAGS) return true
    val n = name.lowercase()
    return n.contains("satélite") || n.contains("gnss") ||
        (n.contains("sat") && !n.contains("status")) ||
        (tag.contains("sat") && tag.length <= 12)
}

private val ENGINE_TAGS = setOf(
    "engine", "enginehours", "ign", "ignition", "acc", "motor", "encendido",
    "dioignition", "engine_lock",
)

private val BATTERY_TAGS = setOf(
    "battery", "batt", "power", "bateria", "batería", "bat", "volt", "voltage",
    "adc1", "adc2", "adc3", "soc", "main_voltage", "backup_battery",
)

private val SAT_TAGS = setOf(
    "sat", "sats", "satellite", "satellites", "num_sat", "satnum", "gnss",
    "gpsstat", "gsv", "in_use_satellites", "visible_satellites",
)
