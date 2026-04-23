package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.HistoryRange
import com.drsecuritygps.app.core.model.HistoryTrip
import com.drsecuritygps.app.network.DrSecurityApi
import com.drsecuritygps.app.network.GeoapifyReverseGeocoder
import com.drsecuritygps.app.storage.SessionStore
import kotlin.math.roundToLong

class HistoryRepository(
    private val api: DrSecurityApi,
    private val sessionStore: SessionStore,
    private val reverseGeocoder: GeoapifyReverseGeocoder,
) {
    suspend fun getHistory(deviceId: String, range: HistoryRange): List<HistoryTrip> =
        api.getHistory(deviceId, range)

    suspend fun resolveAddress(latitude: Double, longitude: Double): String? {
        val cacheKey = cacheKeyFor(latitude, longitude)
        sessionStore.readCachedReverseGeocode(cacheKey)?.let { return it }

        val address = reverseGeocoder.reverseGeocode(latitude = latitude, longitude = longitude)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: return null

        sessionStore.saveReverseGeocode(cacheKey, address)
        return address
    }

    companion object {
        fun cacheKeyFor(latitude: Double, longitude: Double): String =
            "${latitude.roundCoordinate()}|${longitude.roundCoordinate()}"

        private fun Double.roundCoordinate(): Long = (this * 1_000_000.0).roundToLong()
    }
}
