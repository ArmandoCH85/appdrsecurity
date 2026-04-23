package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.model.DeviceSensorListItem
import com.drsecuritygps.app.core.model.DeviceSensorRow
import com.drsecuritygps.app.core.model.DeviceSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeviceSensorMapperTest {
    @Test
    fun `merges embedded get_devices sensors when get_sensors returns nothing`() {
        val device = DeviceSummary(
            id = "64",
            name = "BDC946",
            embeddedSensorRows = listOf(
                DeviceSensorListItem("ENCENDIDO", "ON"),
                DeviceSensorListItem("Servicio · CAMBIO DE ACEITE", "6d."),
            ),
        )
        val out = emptyList<DeviceSensorRow>().enrichDeviceSummary(device)
        assertEquals(2, out.sensorExtraRows.size)
        assertEquals("ON", out.sensorExtraRows.first { it.label.contains("ENCENDIDO") }.value)
    }

    @Test
    fun `maps engine battery and satellites plus extras`() {
        val rows = listOf(
            DeviceSensorRow(name = "Engine", type = "engine", tagName = "enginehours", value = "ON", unit = ""),
            DeviceSensorRow(name = "Bat", type = "battery", tagName = "batt", value = "85", unit = "%"),
            DeviceSensorRow(name = "GNSS", type = "gps", tagName = "satellites", value = "9", unit = ""),
            DeviceSensorRow(name = "Temp", type = "temperature", tagName = "temp1", value = "31", unit = "°C"),
        )
        val device = DeviceSummary(id = "1", name = "Unit")
        val out = rows.enrichDeviceSummary(device)
        assertEquals("ON", out.sensorEngineDisplay)
        assertEquals("85 %", out.sensorBatteryDisplay)
        assertEquals("9", out.sensorSatellitesDisplay)
        assertTrue(out.sensorExtraRows.any { it.label.contains("Temp", ignoreCase = true) })
    }

    @Test
    fun `dash from get_sensors does not fill engine slot so UI can use ignition fallback`() {
        val rows = listOf(
            DeviceSensorRow(name = "Engine", type = "engine", tagName = "e", value = "-", unit = ""),
        )
        val out = rows.enrichDeviceSummary(DeviceSummary(id = "1", name = "U"))
        assertEquals(null, out.sensorEngineDisplay)
    }

    @Test
    fun `mergeSensorExtras collapses Odometer and Odometro with same value`() {
        val merged = mergeSensorExtras(
            listOf(DeviceSensorListItem("Odometer", "0 85433")),
            listOf(DeviceSensorListItem("Odómetro", "0 85433")),
        )
        assertEquals(1, merged.size)
        assertEquals("0 85433", merged.first().value)
        assertTrue(merged.first().label.contains("ó", ignoreCase = true) || merged.first().label.contains("Odo", ignoreCase = true))
    }
}
