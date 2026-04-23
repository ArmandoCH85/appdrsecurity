package com.drsecuritygps.app.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class GeoapifyReverseGeocoder(
    private val apiKey: String = ApiEnvironment.geoapifyApiKey,
    private val baseUrl: String = ApiEnvironment.geoapifyReverseGeocodeUrl,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    },
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        expectSuccess = false
    }

    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
        language: String = ApiEnvironment.defaultLanguage,
    ): String? {
        if (apiKey.isBlank()) return null

        val response = client.get(baseUrl) {
            parameter("lat", latitude)
            parameter("lon", longitude)
            parameter("lang", language)
            parameter("apiKey", apiKey)
        }
        if (!response.status.isSuccess()) return null

        return parseGeoapifyFormattedAddress(response.body())
    }
}

internal fun parseGeoapifyFormattedAddress(payload: JsonObject): String? {
    val feature = payload.array("features").firstOrNull()?.objectOrNull() ?: return null
    val properties = feature.obj("properties") ?: return null

    return properties.string("formatted")
        ?: listOfNotNull(
            properties.string("address_line1"),
            properties.string("address_line2"),
        ).joinToString(", ").takeIf { it.isNotBlank() }
}
