package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.network.DrSecurityApi

class AlertEventsRepository(
    private val api: DrSecurityApi,
) {
    suspend fun getAlertEvents(limit: Int = 30, page: Int = 1): List<AlertEventItem> =
        api.getAlertEvents(limit = limit, page = page)
}
