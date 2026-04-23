package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.MapInvalidPayloadException
import com.drsecuritygps.app.core.MapOfflineException
import com.drsecuritygps.app.core.MapTimeoutException
import com.drsecuritygps.app.core.MapUnauthorizedException
import com.drsecuritygps.app.core.SessionExpiredException
import com.drsecuritygps.app.core.model.MapaError
import io.ktor.utils.io.errors.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

class MapErrorMapperTest {
    @Test
    fun `maps timeout error deterministically`() {
        assertEquals(MapaError.Timeout, mapToMapaError(MapTimeoutException()))
    }

    @Test
    fun `maps offline error deterministically`() {
        assertEquals(MapaError.Offline, mapToMapaError(MapOfflineException()))
    }

    @Test
    fun `maps io exception to offline user facing state`() {
        val mapped = mapToMapaError(IOException("Failed to connect to /gpscloud.alertasecurity.com.pe:443"))

        assertEquals(MapaError.Offline, mapped)
        assertEquals("Sin conectividad. Verificá tu red e intentá nuevamente.", mapped.toUserMessage())
    }

    @Test
    fun `maps unauthorized errors deterministically`() {
        assertEquals(MapaError.Unauthorized, mapToMapaError(SessionExpiredException()))
        assertEquals(MapaError.Unauthorized, mapToMapaError(MapUnauthorizedException()))
    }

    @Test
    fun `maps invalid payload error deterministically`() {
        assertEquals(MapaError.InvalidPayload, mapToMapaError(MapInvalidPayloadException()))
    }

    @Test
    fun `maps unknown error and returns stable message`() {
        val mapped = mapToMapaError(IllegalStateException("boom"))

        assertEquals(MapaError.Unknown, mapped)
        assertEquals("No pudimos cargar el mapa. Reintentá.", mapped.toUserMessage())
    }
}
