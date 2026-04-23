package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.model.DeviceFilter
import com.drsecuritygps.app.core.model.DeviceSensorListItem
import com.drsecuritygps.app.core.model.DeviceSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeviceReducersTest {
    private val devices = listOf(
        DeviceSummary(id = "1", name = "Camion 04", onlineStatus = "online", alarm = "", address = "CDMX"),
        DeviceSummary(id = "2", name = "Van 12", onlineStatus = "offline", alarm = "0", address = "Puebla"),
        DeviceSummary(id = "3", name = "Unidad 105", onlineStatus = "online", alarm = "critical", address = "Toluca"),
    )

    @Test
    fun `filters by search and status`() {
        val result = filterDevices(devices, "Camion", DeviceFilter.Online)
        assertEquals(listOf("1"), result.map { it.id })
    }

    @Test
    fun `returns critical devices only`() {
        val result = filterDevices(devices, "", DeviceFilter.Critical)
        assertEquals(listOf("3"), result.map { it.id })
    }

    @Test
    fun `merge updates replaces existing device and preserves source order`() {
        val updates = listOf(
            DeviceSummary(id = "2", name = "Van 12", onlineStatus = "online", alarm = "", address = "Puebla"),
            DeviceSummary(id = "4", name = "Zeta", onlineStatus = "online", alarm = "", address = "Merida"),
        )

        val result = mergeDevices(devices, updates)

        assertEquals(listOf("1", "2", "3", "4"), result.map { it.id })
        assertEquals("online", result.first { it.id == "2" }.onlineStatus)
    }

    @Test
    fun `merge live fuses fresh embedded telemetry into sensorExtraRows`() {
        val previous = DeviceSummary(
            id = "76",
            name = "AJA866",
            sensorExtraRows = listOf(DeviceSensorListItem("Legado", "x")),
            embeddedSensorRows = listOf(DeviceSensorListItem("Legado", "x")),
            listDistanceText = null,
            listSpeedText = null,
        )
        val fresh = DeviceSummary(
            id = "76",
            name = "AJA866",
            embeddedSensorRows = listOf(
                DeviceSensorListItem("ENCENDIDO", "ON"),
                DeviceSensorListItem("Distancia", "524.72 km"),
                DeviceSensorListItem("Velocidad", "0 kph"),
            ),
            listDistanceText = "524.72 km",
            listSpeedText = "0 kph",
        )
        val result = mergeDevices(listOf(previous), listOf(fresh))
        val merged = result.first { it.id == "76" }
        assertEquals("524.72 km", merged.listDistanceText)
        assertTrue(merged.sensorExtraRows.any { it.label.equals("Distancia", ignoreCase = true) })
        assertTrue(merged.sensorExtraRows.any { it.label.equals("Velocidad", ignoreCase = true) })
    }

    @Test
    fun `merge preserves get_sensors fields from previous snapshot`() {
        val withSensors = devices.map {
            if (it.id == "2") {
                it.copy(
                    sensorEngineDisplay = "ON",
                    sensorBatteryDisplay = "82 %",
                    sensorSatellitesDisplay = "11",
                )
            } else {
                it
            }
        }
        val updates = listOf(
            DeviceSummary(id = "2", name = "Van 12", onlineStatus = "online", alarm = "", address = "Oaxaca"),
        )
        val result = mergeDevices(withSensors, updates)
        val merged = result.first { it.id == "2" }
        assertEquals("Oaxaca", merged.address)
        assertEquals("ON", merged.sensorEngineDisplay)
        assertEquals("82 %", merged.sensorBatteryDisplay)
        assertEquals("11", merged.sensorSatellitesDisplay)
    }

    @Test
    fun `groups devices by groupName and places ungrouped last`() {
        val devices = listOf(
            DeviceSummary(id = "1", name = "A", groupName = "B"),
            DeviceSummary(id = "2", name = "B", groupName = null),
            DeviceSummary(id = "3", name = "C", groupName = ""),
            DeviceSummary(id = "4", name = "D", groupName = "A"),
            DeviceSummary(id = "5", name = "E", groupName = "  A  "),
        )

        val result = groupDevices(devices)

        assertEquals(listOf("A", "B", "Sin Grupo"), result.keys.toList())
        assertEquals(listOf("4", "5"), result.getValue("A").map { it.id })
        assertEquals(listOf("1"), result.getValue("B").map { it.id })
        assertEquals(setOf("2", "3"), result.getValue("Sin Grupo").map { it.id }.toSet())
    }

    @Test
    fun `filters grouped devices and drops empty groups`() {
        val devices = listOf(
            DeviceSummary(id = "1", name = "Unidad Uno", groupName = "A"),
            DeviceSummary(id = "2", name = "Unidad Dos", groupName = "B"),
            DeviceSummary(id = "3", name = "Unidad Tres", groupName = null),
        )

        val grouped = groupDevices(devices)
        val result = filterGroupedDevices(grouped, query = "Uno", filter = DeviceFilter.All)

        assertEquals(listOf("A"), result.keys.toList())
        assertEquals(listOf("1"), result.getValue("A").map { it.id })
    }
}
