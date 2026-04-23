package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.network.DrSecurityApi
import com.drsecuritygps.app.storage.SessionStore

class AlertsRepository(
    private val api: DrSecurityApi,
    private val sessionStore: SessionStore,
) {
    suspend fun getAlerts(): List<AlertItem> {
        val alerts = api.getAlerts()
        sessionStore.saveAlerts(alerts)
        return alerts
    }

    fun getCachedAlerts(): List<AlertItem> = sessionStore.readCachedAlerts()
}
