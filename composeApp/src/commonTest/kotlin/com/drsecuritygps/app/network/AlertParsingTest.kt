package com.drsecuritygps.app.network

import com.drsecuritygps.app.core.model.AlertSeverity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AlertParsingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parses alert payload with numeric device ids and notification channels`() {
        val alert = parseAlertItem(
            json.parseToJsonElement(
                """
                {
                  "id": 15,
                  "name": "Velocidad alta",
                  "type": "overspeed",
                  "active": 1,
                  "updated_at": "2026-03-26 09:00:00",
                  "devices": [101, 202],
                  "notifications": {
                    "sound": { "active": true },
                    "push": { "active": 1 },
                    "email": { "active": false, "input": "ops@drsecurity.com" },
                    "sms": { "active": 0, "input": "+51999999999" }
                  }
                }
                """.trimIndent(),
            ).jsonObject,
        )

        assertEquals("15", alert.id)
        assertEquals("Velocidad alta", alert.title)
        assertEquals(AlertSeverity.Critical, alert.severity)
        assertTrue(alert.active)
        assertEquals(listOf("101", "202"), alert.deviceIds)
        assertTrue(alert.notifications.sound.active)
        assertTrue(alert.notifications.push.active)
        assertFalse(alert.notifications.email.active)
        assertEquals("ops@drsecurity.com", alert.notifications.email.input)
        assertFalse(alert.notifications.sms.active)
        assertEquals("+51999999999", alert.notifications.sms.input)
        assertFalse(alert.notifications.webhook.active)
    }

    @Test
    fun `defaults alert notifications and activity when fields are missing`() {
        val alert = parseAlertItem(
            json.parseToJsonElement(
                """
                {
                  "id": 7,
                  "type": "custom",
                  "active": "0"
                }
                """.trimIndent(),
            ).jsonObject,
        )

        assertEquals("7", alert.id)
        assertEquals("custom", alert.title)
        assertEquals(AlertSeverity.Info, alert.severity)
        assertFalse(alert.active)
        assertTrue(alert.deviceIds.isEmpty())
        assertFalse(alert.notifications.sound.active)
        assertFalse(alert.notifications.push.active)
        assertFalse(alert.notifications.email.active)
        assertFalse(alert.notifications.sms.active)
        assertFalse(alert.notifications.webhook.active)
        assertNull(alert.notifications.webhook.input)
    }

    @Test
    fun `parses alert event payload with coordinates and message severity`() {
        val event = parseAlertEventItem(
            json.parseToJsonElement(
                """
                {
                  "id": 30013,
                  "device_id": 7,
                  "alert_id": 5,
                  "message": "SOS button pressed",
                  "address": "Av. Peru 123",
                  "latitude": -12.0464,
                  "longitude": -77.0428,
                  "speed": 0,
                  "time": "2026-04-22 10:20:30",
                  "created_at": "2026-04-22 10:20:31"
                }
                """.trimIndent(),
            ).jsonObject,
        )

        assertEquals("30013", event.id)
        assertEquals("7", event.deviceId)
        assertEquals("5", event.alertId)
        assertEquals("SOS button pressed", event.message)
        assertEquals("Av. Peru 123", event.address)
        assertEquals(-12.0464, event.latitude)
        assertEquals(-77.0428, event.longitude)
        assertEquals(0.0, event.speed)
        assertEquals("2026-04-22 10:20:30", event.timestamp)
        assertEquals(AlertSeverity.Critical, event.severity)
    }

    @Test
    fun `falls back to created at when event time is missing`() {
        val event = parseAlertEventItem(
            json.parseToJsonElement(
                """
                {
                  "id": 99,
                  "device_id": 3,
                  "message": "Device idle",
                  "created_at": "2026-04-22 11:00:00"
                }
                """.trimIndent(),
            ).jsonObject,
        )

        assertEquals("2026-04-22 11:00:00", event.timestamp)
        assertEquals(AlertSeverity.Warning, event.severity)
    }
}
