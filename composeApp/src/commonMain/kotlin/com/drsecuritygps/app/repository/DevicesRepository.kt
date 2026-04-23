package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.DeviceSensorRow
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.DevicesLatestBatch
import com.drsecuritygps.app.core.model.UserMapIconItem
import com.drsecuritygps.app.network.DrSecurityApi
import com.drsecuritygps.app.presentation.enrichDeviceSummary
import com.drsecuritygps.app.storage.SessionStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class DevicesRepository(
    private val api: DrSecurityApi,
    private val sessionStore: SessionStore,
) {
    suspend fun getDevices(search: String? = null): List<DeviceSummary> {
        val devices = api.getDevices(search)
        val enriched = enrichWithSensors(devices)
        sessionStore.saveDevices(enriched)
        return enriched
    }

    suspend fun getDevicesLatest(time: Long? = null): DevicesLatestBatch = api.getDevicesLatest(time)

    suspend fun getUserMapIcons(search: String? = null): List<UserMapIconItem> = api.getUserMapIcons(search)

    fun getCachedDevices(): List<DeviceSummary> = sessionStore.readCachedDevices()

    private suspend fun enrichWithSensors(devices: List<DeviceSummary>): List<DeviceSummary> {
        if (devices.isEmpty()) return devices
        val semaphore = Semaphore(permits = 4)
        return coroutineScope {
            devices.map { device ->
                async {
                    semaphore.withPermit {
                        val rows = fetchSensorsWithRetry(device.id)
                        rows.enrichDeviceSummary(device)
                    }
                }
            }.awaitAll()
        }
    }

    private suspend fun fetchSensorsWithRetry(deviceId: String): List<DeviceSensorRow> {
        repeat(3) { attempt ->
            runCatching { api.getDeviceSensors(deviceId) }
                .onSuccess { return it }
            if (attempt < 2) delay(300L * (attempt + 1))
        }
        return emptyList()
    }
}
