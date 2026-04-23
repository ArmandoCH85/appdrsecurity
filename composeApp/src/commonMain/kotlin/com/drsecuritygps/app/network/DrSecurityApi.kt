package com.drsecuritygps.app.network

import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.core.model.CommandRequest
import com.drsecuritygps.app.core.model.CommandTemplate
import com.drsecuritygps.app.core.model.DeviceSensorRow
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.DevicesLatestBatch
import com.drsecuritygps.app.core.model.HistoryRange
import com.drsecuritygps.app.core.model.HistoryTrip
import com.drsecuritygps.app.core.model.ReportCatalog
import com.drsecuritygps.app.core.model.ReportGenerationRequest
import com.drsecuritygps.app.core.model.ReportGenerationResponse
import com.drsecuritygps.app.core.model.Session
import com.drsecuritygps.app.core.model.UserMapIconItem
import com.drsecuritygps.app.core.model.UserProfile

interface DrSecurityApi {
    suspend fun login(email: String, password: String): Session
    suspend fun getUserProfile(): UserProfile
    suspend fun getDevices(search: String? = null): List<DeviceSummary>
    /**
     * Catálogo de iconos de mapa del usuario (Wox). Sirve para resolver [com.drsecuritygps.app.core.model.DeviceSummary.mapIconId]
     * en URL cuando `get_devices` no trae `map_icon` embebido.
     */
    suspend fun getUserMapIcons(search: String? = null): List<UserMapIconItem>
    suspend fun getDeviceSensors(deviceId: String): List<DeviceSensorRow>
    suspend fun getDevicesLatest(time: Long? = null): DevicesLatestBatch
    suspend fun getHistory(deviceId: String, range: HistoryRange): List<HistoryTrip>
    suspend fun getAddReportData(): ReportCatalog
    suspend fun getReports(): ReportCatalog
    suspend fun generateReport(request: ReportGenerationRequest): ReportGenerationResponse
    suspend fun getAlerts(): List<AlertItem>
    suspend fun getAlertEvents(limit: Int = 30, page: Int = 1): List<AlertEventItem>
    suspend fun getDeviceCommands(deviceId: String): List<CommandTemplate>
    suspend fun sendGprsCommand(request: CommandRequest)
    suspend fun sendSmsCommand(request: CommandRequest)
    suspend fun registerFcmToken(token: String, projectId: String? = null)
}
