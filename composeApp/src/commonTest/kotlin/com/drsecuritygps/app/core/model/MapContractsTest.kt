package com.drsecuritygps.app.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class MapContractsTest {
    @Test
    fun `app settings disables map reliability flags by default`() {
        val settings = AppSettings()

        assertFalse(settings.mapReliabilityEnabled)
        assertFalse(settings.mapRetryEnabled)
        assertFalse(settings.mapAndroidGuardsEnabled)
        assertFalse(settings.mapIosDegradedUiEnabled)
    }

    @Test
    fun `map feed state keeps source contract for ready and empty`() {
        val ready = MapFeedState.Ready(
            devices = listOf(DeviceSummary(id = "1", name = "Unidad 1")),
            source = DataSource.Network,
        )
        val empty = MapFeedState.Empty(source = DataSource.Cache)

        assertEquals(DataSource.Network, ready.source)
        assertEquals(DataSource.Cache, empty.source)
    }

    @Test
    fun `map capability supports unavailable reason contract`() {
        val unavailable = MapCapability.Unavailable(
            platform = "iOS",
            reason = "Mapa nativo no implementado en esta fase",
        )

        assertIs<MapCapability.Unavailable>(unavailable)
        assertEquals("iOS", unavailable.platform)
    }
}
