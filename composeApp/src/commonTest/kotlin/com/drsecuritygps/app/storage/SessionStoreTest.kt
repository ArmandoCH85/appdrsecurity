package com.drsecuritygps.app.storage

import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.AlertNotificationChannel
import com.drsecuritygps.app.core.model.AlertNotifications
import com.drsecuritygps.app.core.model.AlertSeverity
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.Session
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionStoreTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Test
    fun `clearSession removes sensitive caches and preserves reverse geocode`() = runTest {
        val secureStorage = FakeSecureStorage()
        val sessionCache = FakeSessionCacheGateway()
        val deviceCache = FakeDeviceCacheGateway()
        val alertCache = FakeAlertCacheGateway()
        val alertEventNotified = FakeAlertEventNotifiedGateway()
        val reverseGeocodeCache = FakeReverseGeocodeCacheGateway()
        val store = SessionStore(
            secureStorage = secureStorage,
            json = json,
            sessionCache = sessionCache,
            deviceCache = deviceCache,
            alertCache = alertCache,
            alertEventNotified = alertEventNotified,
            reverseGeocodeCache = reverseGeocodeCache,
        )

        store.saveSession(Session(userApiHash = "token-1", email = "user@drsecurity.com"))
        store.saveDevices(listOf(DeviceSummary(id = "101", name = "Unidad 101")))
        store.saveAlerts(
            listOf(
                AlertItem(
                    id = "alert-1",
                    title = "Velocidad alta",
                    severity = AlertSeverity.Critical,
                    active = true,
                    deviceIds = listOf("101"),
                    notifications = AlertNotifications(
                        push = AlertNotificationChannel(active = true),
                    ),
                    message = "overspeed",
                ),
            ),
        )
        store.saveReverseGeocode("12.34,-76.54", "Av. Principal 123")

        assertTrue(store.readCachedDevices().isNotEmpty())
        assertTrue(store.readCachedAlerts().isNotEmpty())

        store.clearSession()

        assertTrue(sessionCache.cleared)
        assertTrue(deviceCache.cleared)
        assertTrue(alertCache.cleared)
        assertTrue(alertEventNotified.cleared)
        assertEquals(emptyList(), store.readCachedDevices())
        assertEquals(emptyList(), store.readCachedAlerts())
        assertEquals("Av. Principal 123", store.readCachedReverseGeocode("12.34,-76.54"))
        assertEquals(null, store.readSession())
    }
}

private class FakeSecureStorage : SecureStorage {
    private val values = mutableMapOf<String, String>()

    override suspend fun getString(key: String): String? = values[key]

    override suspend fun putString(key: String, value: String) {
        values[key] = value
    }

    override suspend fun remove(key: String) {
        values.remove(key)
    }
}

private class FakeSessionCacheGateway : SessionCacheGateway {
    var row: SessionCacheRow? = null
    var cleared: Boolean = false

    override fun read(): SessionCacheRow? = row

    override fun upsert(email: String?, language: String, updatedAt: Long) {
        row = SessionCacheRow(email = email, language = language)
    }

    override fun clear() {
        cleared = true
        row = null
    }
}

private class FakeDeviceCacheGateway : DeviceCacheGateway {
    private val payloads = linkedMapOf<String, CachedPayloadRow>()
    var cleared: Boolean = false

    override fun upsert(deviceId: String, payload: String, updatedAt: Long) {
        payloads[deviceId] = CachedPayloadRow(payload)
    }

    override fun readAll(): List<CachedPayloadRow> = payloads.values.toList()

    override fun clear() {
        cleared = true
        payloads.clear()
    }
}

private class FakeAlertCacheGateway : AlertCacheGateway {
    private val payloads = linkedMapOf<String, CachedPayloadRow>()
    var cleared: Boolean = false

    override fun upsert(alertId: String, payload: String, updatedAt: Long) {
        payloads[alertId] = CachedPayloadRow(payload)
    }

    override fun readAll(): List<CachedPayloadRow> = payloads.values.toList()

    override fun clear() {
        cleared = true
        payloads.clear()
    }
}

private class FakeAlertEventNotifiedGateway : AlertEventNotifiedGateway {
    private val ids = mutableSetOf<String>()
    var cleared: Boolean = false

    override fun count(): Long = ids.size.toLong()

    override fun eventExists(eventId: String): Boolean = eventId in ids

    override fun insertNotified(eventId: String, payload: String, notifiedAt: Long) {
        ids.add(eventId)
    }

    override fun clear() {
        cleared = true
        ids.clear()
    }
}

private class FakeReverseGeocodeCacheGateway : ReverseGeocodeCacheGateway {
    private val values = mutableMapOf<String, String>()

    override fun read(cacheKey: String): String? = values[cacheKey]

    override fun upsert(cacheKey: String, address: String, updatedAt: Long) {
        values[cacheKey] = address
    }
}
