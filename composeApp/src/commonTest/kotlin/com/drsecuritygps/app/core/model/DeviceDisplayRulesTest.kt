package com.drsecuritygps.app.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeviceDisplayRulesTest {
    @Test
    fun `alarm zero is not rendered as active alarm`() {
        assertFalse(hasRenderableAlarm("0"))
    }

    @Test
    fun `blank or null alarm is not rendered as active alarm`() {
        assertFalse(hasRenderableAlarm(""))
        assertFalse(hasRenderableAlarm(null))
        assertFalse(hasRenderableAlarm("null"))
    }

    @Test
    fun `non zero alarm is rendered as active alarm`() {
        assertTrue(hasRenderableAlarm("1"))
        assertTrue(hasRenderableAlarm("sos"))
    }

    @Test
    fun `subtitle prefers address over last update`() {
        assertEquals("Av. Principal 123", deviceSubtitle("Av. Principal 123", "2026-03-14 10:00:00"))
    }

    @Test
    fun `subtitle falls back to last update when address is not usable`() {
        assertEquals("2026-03-14 10:00:00", deviceSubtitle("-", "2026-03-14 10:00:00"))
        assertNull(deviceSubtitle("-", ""))
    }
}
