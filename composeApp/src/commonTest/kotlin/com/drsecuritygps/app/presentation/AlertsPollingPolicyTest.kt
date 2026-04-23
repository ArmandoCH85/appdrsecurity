package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.UiState
import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.CommandConnection
import com.drsecuritygps.app.core.model.CommandField
import com.drsecuritygps.app.core.model.CommandFieldOption
import com.drsecuritygps.app.core.model.CommandTemplate
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.AlertSeverity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AlertsPollingPolicyTest {
    @Test
    fun `polls immediately when there is no previous refresh`() {
        assertTrue(shouldPollAlerts(lastPollTimeMillis = null, nowMillis = 1_000L))
    }

    @Test
    fun `waits until poll interval has elapsed`() {
        assertFalse(shouldPollAlerts(lastPollTimeMillis = 1_000L, nowMillis = 30_999L))
        assertTrue(shouldPollAlerts(lastPollTimeMillis = 1_000L, nowMillis = 31_000L))
    }

    @Test
    fun `maps empty alerts to empty ui state`() {
        assertEquals(UiState.Empty, alertsUiState(emptyList()))
    }

    @Test
    fun `maps non empty alerts to success ui state`() {
        val alerts = listOf(
            AlertItem(
                id = "alert-1",
                title = "Geo fence",
                severity = AlertSeverity.Warning,
            ),
        )

        assertEquals(UiState.Success(alerts), alertsUiState(alerts))
    }

    @Test
    fun `maps non empty alert events to success ui state`() {
        val events = listOf(
            AlertEventItem(
                id = "evt-1",
                deviceId = "7",
                message = "SOS",
                timestamp = "2026-04-22 12:00:00",
            ),
        )

        assertEquals(UiState.Success(events), alertEventsUiState(events))
    }

    @Test
    fun `resolves known device name and falls back for unknown device`() {
        val devices = listOf(
            DeviceSummary(id = "7", name = "Camion 01"),
        )

        assertEquals("Camion 01", resolveEventDeviceName("7", devices))
        assertEquals("Unidad #9", resolveEventDeviceName("9", devices))
    }

    @Test
    fun `resolves stop start and secure commands from real catalog`() {
        val commands = listOf(
            CommandTemplate(type = "engine_resume", title = "Encender motor", connection = CommandConnection.Gprs),
            CommandTemplate(type = "immobilize", title = "Inmovilizar motor", connection = CommandConnection.Gprs),
            CommandTemplate(type = "secure_park_mode", title = "Secure Park", connection = CommandConnection.Sms),
        )

        assertEquals("immobilize", resolveQuickCommandTemplate("engine_stop", commands)?.type)
        assertEquals("engine_resume", resolveQuickCommandTemplate("engine_start", commands)?.type)
        assertEquals("secure_park_mode", resolveQuickCommandTemplate("secure_park", commands)?.type)
    }

    @Test
    fun `builds quick command message from defaults and options`() {
        val command = CommandTemplate(
            type = "secure_mode",
            title = "Secure mode",
            connection = CommandConnection.Gprs,
            attributes = listOf(
                CommandField(name = "mode", label = "Modo", type = "select", required = true, options = listOf(CommandFieldOption(id = "1", title = "On"))),
                CommandField(name = "confirm", label = "Confirmar", type = "text", required = true, defaultValue = "yes"),
            ),
        )

        assertEquals("{\"mode\":\"1\", \"confirm\":\"yes\"}", buildQuickCommandMessage(command))
    }

    @Test
    fun `returns null when quick command requires manual parameters`() {
        val command = CommandTemplate(
            type = "engine_stop",
            title = "Stop engine",
            connection = CommandConnection.Gprs,
            attributes = listOf(
                CommandField(name = "pin", label = "PIN", type = "password", required = true),
            ),
        )

        assertNull(buildQuickCommandMessage(command))
    }
}
