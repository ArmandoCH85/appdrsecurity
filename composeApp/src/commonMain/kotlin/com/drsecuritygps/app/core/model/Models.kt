package com.drsecuritygps.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val userApiHash: String,
    val email: String? = null,
    val language: String = "es",
)

@Serializable
data class UserProfile(
    val email: String = "",
    val expirationDate: String? = null,
    val daysLeft: Int? = null,
    val plan: String = "",
    val devicesLimit: Int? = null,
    val groupId: Int? = null,
)

@Serializable
data class DeviceSensorListItem(
    val label: String,
    val value: String,
)

/** Fila devuelta por `get_sensors` (solo uso en memoria / capa de red). */
data class DeviceSensorRow(
    val name: String = "",
    val type: String = "",
    val tagName: String = "",
    val value: String = "",
    val unit: String = "",
    val typeTitle: String? = null,
)

@Serializable
data class DeviceSummary(
    val id: String,
    val name: String,
    val onlineStatus: String = "",
    val alarm: String = "",
    val lastUpdate: String = "",
    val timestampSeconds: Long? = null,
    val speedKph: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasValidCoordinates: Boolean = false,
    val address: String = "",
    val batteryLevel: String? = null,
    val ignition: String? = null,
    val course: String? = null,
    /**
     * P. ej. [arrow] cuando Wox usa flecha de rumbo; el PNG `icon` es decorativo, la orientación la da [course].
     * Ver `icon_type` / `icon.type` en `get_devices`.
     */
    val iconType: String? = null,
    val groupName: String? = null,
    /** Icono de mapa Wox (`map_icon.path`); prioridad sobre [iconPath] para marcadores. */
    val mapIconPath: String? = null,
    val mapIconId: Int? = null,
    val iconPath: String? = null,
    val iconColor: String? = null,
    /** Sensores incluidos en el objeto del dispositivo (`get_devices` / latest); suelen traer el valor en vivo aunque `get_sensors` devuelva "-". */
    val embeddedSensorRows: List<DeviceSensorListItem> = emptyList(),
    /** Texto mostrado en listado, desde `get_sensors` (tipo motor / engine). */
    val sensorEngineDisplay: String? = null,
    /** Porcentaje o lectura de batería desde sensores dedicados. */
    val sensorBatteryDisplay: String? = null,
    /** Cantidad de satélites u otra métrica GNSS. */
    val sensorSatellitesDisplay: String? = null,
    /** Otros sensores con valor distinto de vacío o "-". */
    val sensorExtraRows: List<DeviceSensorListItem> = emptyList(),
    /** Texto listo para chip (p. ej. `524.72 km`), desde `total_distance` en `get_devices`. */
    val listDistanceText: String? = null,
    /** Texto listo para chip (p. ej. `0 kph`), desde `speed` + unidad. */
    val listSpeedText: String? = null,
)

@Serializable
data class DeviceLivePosition(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val iconUrl: String? = null,
    val iconColor: String? = null,
    /** Rumbo en grados (0 = Norte, sentido horario), desde `get_devices` / `course`. */
    val courseDegrees: Double? = null,
)

/** Respuesta de `get_user_map_icons`; solo se usa en red para mapear [mapIconId] → ruta (no es lista en UI). */
data class UserMapIconItem(
    val id: Int,
    val name: String,
    val mapIconId: Int,
    val path: String,
    val width: String? = null,
    val height: String? = null,
    val active: Int = 1,
)

@Serializable
data class DeviceDetail(
    val summary: DeviceSummary,
    val todayDistanceKm: Double? = null,
    val batteryPercent: String? = null,
    val ignitionState: String? = null,
    val lastReport: String? = null,
)

@Serializable
data class HistoryPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String = "",
    val address: String = "",
    val speed: Double = 0.0,
)

@Serializable
data class HistoryTrip(
    val title: String,
    val statusCode: Int? = null,
    val startTime: String = "",
    val endTime: String = "",
    val distanceLabel: String = "",
    val durationLabel: String = "",
    val points: List<HistoryPoint> = emptyList(),
)

@Serializable
data class AlertItem(
    val id: String,
    val title: String,
    val severity: AlertSeverity,
    val active: Boolean = false,
    val deviceIds: List<String> = emptyList(),
    val notifications: AlertNotifications = AlertNotifications(),
    val message: String = "",
    val timestamp: String = "",
)

@Serializable
data class AlertEventItem(
    val id: String,
    val deviceId: String,
    val deviceName: String = "",
    val alertId: String? = null,
    val message: String,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val speed: Double? = null,
    val timestamp: String = "",
    val severity: AlertSeverity = AlertSeverity.Info,
)

@Serializable
data class AlertNotifications(
    val sound: AlertNotificationChannel = AlertNotificationChannel(),
    val push: AlertNotificationChannel = AlertNotificationChannel(),
    val email: AlertNotificationChannel = AlertNotificationChannel(),
    val sms: AlertNotificationChannel = AlertNotificationChannel(),
    val webhook: AlertNotificationChannel = AlertNotificationChannel(),
)

@Serializable
data class AlertNotificationChannel(
    val active: Boolean = false,
    val input: String? = null,
)

@Serializable
enum class AlertSeverity {
    Critical,
    Warning,
    Info,
}

@Serializable
data class CommandField(
    val name: String,
    val label: String,
    val type: String,
    val description: String = "",
    val required: Boolean = false,
    val defaultValue: String? = null,
    val options: List<CommandFieldOption> = emptyList(),
)

@Serializable
data class CommandFieldOption(
    val id: String,
    val title: String,
)

@Serializable
data class CommandTemplate(
    val type: String,
    val title: String,
    val connection: CommandConnection,
    val attributes: List<CommandField> = emptyList(),
)

@Serializable
enum class CommandConnection {
    Gprs,
    Sms,
    Unknown,
}

@Serializable
data class DevicesLatestBatch(
    val items: List<DeviceSummary> = emptyList(),
    val serverTimeSeconds: Long? = null,
)

@Serializable
data class CommandRequest(
    val deviceId: String,
    val type: String,
    val message: String,
    val autoSendWhenOnline: Boolean = true,
)

@Serializable
data class AppSettings(
    val language: String = "es",
    val notificationsEnabled: Boolean = true,
    val mapReliabilityEnabled: Boolean = false,
    val mapRetryEnabled: Boolean = false,
    val mapAndroidGuardsEnabled: Boolean = false,
    val mapIosDegradedUiEnabled: Boolean = false,
)

enum class DataSource {
    Cache,
    Network,
}

sealed interface MapaError {
    data object Timeout : MapaError
    data object Offline : MapaError
    data object Unauthorized : MapaError
    data object InvalidPayload : MapaError
    data object Unknown : MapaError
}

sealed interface MapFeedState {
    data object Loading : MapFeedState
    data class Ready(
        val devices: List<DeviceSummary>,
        val source: DataSource,
        val stale: Boolean = false,
    ) : MapFeedState

    data class Empty(val source: DataSource) : MapFeedState
    data class Degraded(val devices: List<DeviceSummary>, val error: MapaError) : MapFeedState
    data class Error(val error: MapaError) : MapFeedState
}

sealed interface MapCapability {
    data class Available(val platform: String = "shared") : MapCapability
    data class Unavailable(
        val platform: String,
        val reason: String,
    ) : MapCapability
}

data class HistoryRange(
    val fromDate: String,
    val fromTime: String,
    val toDate: String,
    val toTime: String,
)

enum class DeviceFilter {
    All,
    Online,
    Offline,
    Critical,
}

typealias GroupedDevices = Map<String, List<DeviceSummary>>
