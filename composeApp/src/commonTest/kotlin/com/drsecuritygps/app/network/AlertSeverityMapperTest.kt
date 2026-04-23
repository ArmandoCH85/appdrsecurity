package com.drsecuritygps.app.network

import com.drsecuritygps.app.core.model.AlertSeverity
import kotlin.test.Test
import kotlin.test.assertEquals

class AlertSeverityMapperTest {
    @Test
    fun `maps critical alert types`() {
        assertEquals(AlertSeverity.Critical, mapAlertSeverity("overspeed"))
    }

    @Test
    fun `maps warning alert types`() {
        assertEquals(AlertSeverity.Warning, mapAlertSeverity("offline_duration"))
    }

    @Test
    fun `defaults to info for unknown types`() {
        assertEquals(AlertSeverity.Info, mapAlertSeverity("maintenance"))
    }
}
