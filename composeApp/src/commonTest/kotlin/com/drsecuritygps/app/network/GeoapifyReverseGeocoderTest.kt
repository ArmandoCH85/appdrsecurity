package com.drsecuritygps.app.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GeoapifyReverseGeocoderTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Test
    fun `parses formatted address from geoapify response`() {
        val payload = json.parseToJsonElement(
            """
            {
              "features": [
                {
                  "properties": {
                    "formatted": "Parkhaus Kö Talstraße, Talstraße 1, 40217 Dusseldorf, Germany"
                  }
                }
              ]
            }
            """.trimIndent(),
        ).jsonObject

        assertEquals(
            "Parkhaus Kö Talstraße, Talstraße 1, 40217 Dusseldorf, Germany",
            parseGeoapifyFormattedAddress(payload),
        )
    }

    @Test
    fun `falls back to address lines when formatted is missing`() {
        val payload = json.parseToJsonElement(
            """
            {
              "features": [
                {
                  "properties": {
                    "address_line1": "Av. Principal 123",
                    "address_line2": "Lima, Peru"
                  }
                }
              ]
            }
            """.trimIndent(),
        ).jsonObject

        assertEquals("Av. Principal 123, Lima, Peru", parseGeoapifyFormattedAddress(payload))
    }

    @Test
    fun `returns null when geoapify response has no usable features`() {
        val payload = json.parseToJsonElement("""{"features": []}""").jsonObject

        assertNull(parseGeoapifyFormattedAddress(payload))
    }
}
