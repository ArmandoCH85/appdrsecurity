package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.model.DeviceFilter
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.GroupedDevices
import com.drsecuritygps.app.core.model.hasRenderableAlarm
import com.drsecuritygps.app.core.model.isOnlineStatus

internal fun filterDevices(
    devices: List<DeviceSummary>,
    query: String,
    filter: DeviceFilter,
): List<DeviceSummary> = devices.filter { device ->
    val matchesSearch = query.isBlank() ||
        device.id.contains(query, ignoreCase = true) ||
        device.name.contains(query, ignoreCase = true) ||
        device.address.contains(query, ignoreCase = true)

    val matchesFilter = when (filter) {
        DeviceFilter.All -> true
        DeviceFilter.Online -> isOnlineStatus(device.onlineStatus)
        DeviceFilter.Offline -> device.onlineStatus.contains("offline", ignoreCase = true)
        DeviceFilter.Critical -> hasRenderableAlarm(device.alarm)
    }
    matchesSearch && matchesFilter
}

internal fun filterGroupedDevices(
    devices: GroupedDevices,
    query: String,
    filter: DeviceFilter,
): GroupedDevices = devices.mapValues { (_, groupDevices) ->
    filterDevices(groupDevices, query, filter)
}.filterValues { it.isNotEmpty() }

internal fun groupDevices(devices: List<DeviceSummary>): GroupedDevices {
    val grouped = devices.groupBy { device ->
        val groupName = device.groupName?.trim()
        if (groupName.isNullOrBlank()) null else groupName
    }
    return grouped.entries
        .sortedWith(compareBy<Map.Entry<String?, List<DeviceSummary>>> { it.key == null }.thenBy { it.key ?: "" })
        .associate { (key, value) -> (key ?: "Sin Grupo") to value }
}

internal fun mergeDevices(
    current: List<DeviceSummary>,
    updates: List<DeviceSummary>,
): List<DeviceSummary> {
    val currentOrder = current.map { it.id }
    val mergedById = current.associateBy { it.id }.toMutableMap()

    updates.forEach { update ->
        val previous = mergedById[update.id]
        mergedById[update.id] = if (previous != null) {
            mergeLiveDevicePayload(previous, update)
        } else {
            update
        }
    }

    val appendedIds = updates
        .map { it.id }
        .filterNot { it in currentOrder }

    return (currentOrder + appendedIds).mapNotNull(mergedById::get)
}

/** `get_devices_latest` a veces trae menos campos; fusionamos filas embebidas y conservamos sensores de API. */
private fun mergeLiveDevicePayload(previous: DeviceSummary, fresh: DeviceSummary): DeviceSummary {
    val emb = if (fresh.embeddedSensorRows.isNotEmpty()) {
        fresh.embeddedSensorRows
    } else {
        previous.embeddedSensorRows
    }
    val mergedRows = mergeSensorExtras(emb, previous.sensorExtraRows)
    return fresh.copy(
        sensorEngineDisplay = previous.sensorEngineDisplay,
        sensorBatteryDisplay = previous.sensorBatteryDisplay,
        sensorSatellitesDisplay = previous.sensorSatellitesDisplay,
        sensorExtraRows = mergedRows,
        embeddedSensorRows = emb,
        listDistanceText = fresh.listDistanceText ?: previous.listDistanceText,
        listSpeedText = fresh.listSpeedText ?: previous.listSpeedText,
        mapIconPath = fresh.mapIconPath ?: previous.mapIconPath,
        mapIconId = fresh.mapIconId ?: previous.mapIconId,
        iconPath = fresh.iconPath ?: previous.iconPath,
        course = fresh.course ?: previous.course,
        iconType = fresh.iconType ?: previous.iconType,
    )
}
