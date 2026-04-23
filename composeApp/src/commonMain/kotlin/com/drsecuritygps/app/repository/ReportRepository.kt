package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.ReportCatalog
import com.drsecuritygps.app.core.model.ReportGenerationRequest
import com.drsecuritygps.app.core.model.ReportGenerationResponse
import com.drsecuritygps.app.network.DrSecurityApi
import com.drsecuritygps.app.storage.SessionStore

class ReportRepository(
    private val api: DrSecurityApi,
    private val sessionStore: SessionStore,
) {
    private var cachedUserApiHash: String? = null
    private var cachedCatalog: ReportCatalog? = null

    suspend fun loadCatalog(forceRefresh: Boolean = false): ReportCatalog {
        val currentUserApiHash = sessionStore.readSession()?.userApiHash
        if (!forceRefresh && currentUserApiHash != null && cachedUserApiHash == currentUserApiHash) {
            cachedCatalog?.let { return it }
        }

        val primary = api.getAddReportData()
        val merged = if (primary.types.isNotEmpty()) {
            primary
        } else {
            val fallback = api.getReports()
            primary.copy(types = fallback.types.ifEmpty { primary.types })
        }

        cachedUserApiHash = currentUserApiHash
        cachedCatalog = merged
        return merged
    }

    suspend fun generateReport(request: ReportGenerationRequest): ReportGenerationResponse =
        api.generateReport(request)
}
