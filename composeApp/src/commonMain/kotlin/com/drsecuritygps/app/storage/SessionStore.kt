package com.drsecuritygps.app.storage

import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.Session
import com.drsecuritygps.app.network.ApiEnvironment
import com.drsecuritygps.app.storage.db.AppDatabase
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

internal data class SessionCacheRow(
    val email: String?,
    val language: String?,
)

internal data class CachedPayloadRow(
    val payload: String,
)

internal interface SessionCacheGateway {
    fun read(): SessionCacheRow?
    fun upsert(email: String?, language: String, updatedAt: Long)
    fun clear()
}

internal interface DeviceCacheGateway {
    fun upsert(deviceId: String, payload: String, updatedAt: Long)
    fun readAll(): List<CachedPayloadRow>
    fun clear()
}

internal interface AlertCacheGateway {
    fun upsert(alertId: String, payload: String, updatedAt: Long)
    fun readAll(): List<CachedPayloadRow>
    fun clear()
}

internal interface AlertEventNotifiedGateway {
    fun count(): Long
    fun eventExists(eventId: String): Boolean
    fun insertNotified(eventId: String, payload: String, notifiedAt: Long)
    fun clear()
}

internal interface ReverseGeocodeCacheGateway {
    fun read(cacheKey: String): String?
    fun upsert(cacheKey: String, address: String, updatedAt: Long)
}

class SessionStore internal constructor(
    private val secureStorage: SecureStorage,
    private val json: Json,
    private val sessionCache: SessionCacheGateway,
    private val deviceCache: DeviceCacheGateway,
    private val alertCache: AlertCacheGateway,
    private val alertEventNotified: AlertEventNotifiedGateway,
    private val reverseGeocodeCache: ReverseGeocodeCacheGateway,
) {
    constructor(
        secureStorage: SecureStorage,
        database: AppDatabase,
        json: Json,
    ) : this(
        secureStorage = secureStorage,
        json = json,
        sessionCache = SqlSessionCacheGateway(database),
        deviceCache = SqlDeviceCacheGateway(database),
        alertCache = SqlAlertCacheGateway(database),
        alertEventNotified = SqlAlertEventNotifiedGateway(database),
        reverseGeocodeCache = SqlReverseGeocodeCacheGateway(database),
    )

    suspend fun readSession(): Session? {
        val token = secureStorage.getString(USER_API_HASH) ?: return null
        val row = sessionCache.read()
        return Session(
            userApiHash = token,
            email = row?.email,
            language = row?.language ?: ApiEnvironment.defaultLanguage,
        )
    }

    suspend fun saveSession(session: Session) {
        secureStorage.putString(USER_API_HASH, session.userApiHash)
        sessionCache.upsert(
            email = session.email,
            language = session.language,
            updatedAt = currentTimeMillis(),
        )
    }

    suspend fun clearSession() {
        secureStorage.remove(USER_API_HASH)
        sessionCache.clear()
        deviceCache.clear()
        alertCache.clear()
        alertEventNotified.clear()
    }

    fun saveDevices(devices: List<DeviceSummary>) {
        deviceCache.clear()
        val now = currentTimeMillis()
        devices.forEach { device ->
            deviceCache.upsert(
                deviceId = device.id,
                payload = json.encodeToString(DeviceSummary.serializer(), device),
                updatedAt = now,
            )
        }
    }

    fun readCachedDevices(): List<DeviceSummary> = deviceCache.readAll().map {
        json.decodeFromString(DeviceSummary.serializer(), it.payload)
    }

    fun saveAlerts(alerts: List<AlertItem>) {
        alertCache.clear()
        val now = currentTimeMillis()
        alerts.forEach { alert ->
            alertCache.upsert(
                alertId = alert.id,
                payload = json.encodeToString(AlertItem.serializer(), alert),
                updatedAt = now,
            )
        }
    }

    fun readCachedAlerts(): List<AlertItem> = alertCache.readAll().map {
        json.decodeFromString(AlertItem.serializer(), it.payload)
    }

    fun readCachedReverseGeocode(cacheKey: String): String? = reverseGeocodeCache.read(cacheKey)

    /**
     * Compara con SQLite qué [AlertEventItem] son realmente nuevos. La primera tanda (tabla vacía) solo
     * rellena el histórico sin notificar, para no spamear con eventos viejos al instalar o tras cerrar sesión.
     */
    fun consumeNewAlertEventsForNotifications(events: List<AlertEventItem>): List<AlertEventItem> {
        if (events.isEmpty()) return emptyList()
        val now = currentTimeMillis()
        val isBaseline = alertEventNotified.count() == 0L
        if (isBaseline) {
            for (e in events) {
                alertEventNotified.insertNotified(
                    eventId = e.id,
                    payload = json.encodeToString(AlertEventItem.serializer(), e),
                    notifiedAt = now,
                )
            }
            return emptyList()
        }
        val newOnes = ArrayList<AlertEventItem>(4)
        for (e in events) {
            if (!alertEventNotified.eventExists(e.id)) {
                alertEventNotified.insertNotified(
                    eventId = e.id,
                    payload = json.encodeToString(AlertEventItem.serializer(), e),
                    notifiedAt = now,
                )
                newOnes.add(e)
            }
        }
        return newOnes
    }

    fun saveReverseGeocode(cacheKey: String, address: String) {
        reverseGeocodeCache.upsert(
            cacheKey = cacheKey,
            address = address,
            updatedAt = currentTimeMillis(),
        )
    }

    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

    private companion object {
        const val USER_API_HASH = "user_api_hash"
    }
}

private class SqlSessionCacheGateway(database: AppDatabase) : SessionCacheGateway {
    private val queries = database.session_cacheQueries

    override fun read(): SessionCacheRow? = queries.selectSession().executeAsOneOrNull()?.let {
        SessionCacheRow(
            email = it.email,
            language = it.language,
        )
    }

    override fun upsert(email: String?, language: String, updatedAt: Long) {
        queries.upsertSession(
            email = email,
            language = language,
            updated_at = updatedAt,
        )
    }

    override fun clear() {
        queries.clearSession()
    }
}

private class SqlDeviceCacheGateway(database: AppDatabase) : DeviceCacheGateway {
    private val queries = database.device_cacheQueries

    override fun upsert(deviceId: String, payload: String, updatedAt: Long) {
        queries.upsertDevice(
            device_id = deviceId,
            payload = payload,
            updated_at = updatedAt,
        )
    }

    override fun readAll(): List<CachedPayloadRow> = queries.selectAllDevices().executeAsList().map {
        CachedPayloadRow(payload = it.payload)
    }

    override fun clear() {
        queries.clearDevices()
    }
}

private class SqlAlertCacheGateway(database: AppDatabase) : AlertCacheGateway {
    private val queries = database.alert_cacheQueries

    override fun upsert(alertId: String, payload: String, updatedAt: Long) {
        queries.upsertAlert(
            alert_id = alertId,
            payload = payload,
            updated_at = updatedAt,
        )
    }

    override fun readAll(): List<CachedPayloadRow> = queries.selectAllAlerts().executeAsList().map {
        CachedPayloadRow(payload = it.payload)
    }

    override fun clear() {
        queries.clearAlerts()
    }
}

private class SqlAlertEventNotifiedGateway(
    private val database: AppDatabase,
) : AlertEventNotifiedGateway {
    private val q get() = database.alert_event_notifiedQueries

    override fun count(): Long = q.countNotified().executeAsOne()

    override fun eventExists(eventId: String): Boolean = q.eventExists(event_id = eventId).executeAsOne()

    override fun insertNotified(eventId: String, payload: String, notifiedAt: Long) {
        q.insertNotified(
            event_id = eventId,
            payload = payload,
            notified_at = notifiedAt,
        )
    }

    override fun clear() {
        q.clearNotified()
    }
}

private class SqlReverseGeocodeCacheGateway(database: AppDatabase) : ReverseGeocodeCacheGateway {
    private val queries = database.reverse_geocode_cacheQueries

    override fun read(cacheKey: String): String? = queries.selectAddressByKey(cacheKey).executeAsOneOrNull()

    override fun upsert(cacheKey: String, address: String, updatedAt: Long) {
        queries.upsertAddress(
            cache_key = cacheKey,
            address = address,
            updated_at = updatedAt,
        )
    }
}
