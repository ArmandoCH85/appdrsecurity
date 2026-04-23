package com.drsecuritygps.app.network

import android.util.Log
import com.drsecuritygps.app.core.SessionExpiredException
import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.core.model.CommandConnection
import com.drsecuritygps.app.core.model.CommandField
import com.drsecuritygps.app.core.model.CommandFieldOption
import com.drsecuritygps.app.core.model.CommandRequest
import com.drsecuritygps.app.core.model.CommandTemplate
import com.drsecuritygps.app.core.model.DeviceSensorListItem
import com.drsecuritygps.app.core.model.normalizeIgnitionDisplay
import com.drsecuritygps.app.core.model.DeviceSensorRow
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.DevicesLatestBatch
import com.drsecuritygps.app.core.model.HistoryPoint
import com.drsecuritygps.app.core.model.HistoryRange
import com.drsecuritygps.app.core.model.HistoryTrip
import com.drsecuritygps.app.core.model.ReportCatalog
import com.drsecuritygps.app.core.model.ReportCatalogChoice
import com.drsecuritygps.app.core.model.ReportGenerationRequest
import com.drsecuritygps.app.core.model.ReportGenerationResponse
import com.drsecuritygps.app.core.model.Session
import com.drsecuritygps.app.core.model.UserMapIconItem
import com.drsecuritygps.app.core.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.contentOrNull

class KtorDrSecurityApi(
    private val sessionProvider: suspend () -> Session?,
    private val onUnauthorized: suspend () -> Unit,
    private val baseUrl: String = ApiEnvironment.baseUrl,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    },
) : DrSecurityApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    if (!message.contains("user_api_hash")) {
                        println(message)
                    }
                }
            }
            level = LogLevel.HEADERS
        }
        expectSuccess = false
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.value == 401) {
                    throw SessionExpiredException()
                }
                if (!response.status.isSuccess()) {
                    throw ResponseException(response, response.bodyAsText())
                }
            }
            handleResponseExceptionWithRequest { exception, _ ->
                if (exception is SessionExpiredException) {
                    onUnauthorized()
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): Session {
        val payload: JsonObject = client.submitForm(
            url = "$baseUrl/login",
            formParameters = Parameters.build {
                append("email", email)
                append("password", password)
            },
        ).body()
        return Session(
            userApiHash = payload.string("user_api_hash").orEmpty(),
            email = email,
            language = ApiEnvironment.defaultLanguage,
        )
    }

    override suspend fun getUserProfile(): UserProfile {
        val payload = authGet("get_user_data").body<JsonObject>()
        return UserProfile(
            email = payload.string("email").orEmpty(),
            expirationDate = payload.string("expiration_date"),
            daysLeft = payload.int("days_left"),
            plan = payload.string("plan").orEmpty(),
            devicesLimit = payload.int("devices_limit"),
            groupId = payload.int("group_id"),
        )
    }

    override suspend fun getDevices(search: String?): List<DeviceSummary> {
        val payload = authGet("get_devices") {
            search?.takeIf { it.isNotBlank() }?.let { parameter("s", it) }
        }.body<JsonArray>()
        return payload.flatMap { group ->
            val groupName = group.jsonObject.string("name") ?: group.jsonObject.string("title")
            group.jsonObject.array("items").map { parseDevice(it.jsonObject, groupName) }
        }
    }

    override suspend fun getUserMapIcons(search: String?): List<UserMapIconItem> {
        val payload = authGet("get_user_map_icons") {
            search?.takeIf { it.isNotBlank() }?.let { parameter("s", it) }
        }.body<JsonObject>()
        if (payload.int("status") != 1) return emptyList()
        val items = payload.obj("items") ?: return emptyList()
        val arr = when {
            items["mapIcons"] != null -> items.array("mapIcons")
            items["map_icons"] != null -> items.array("map_icons")
            else -> JsonArray(emptyList())
        }
        return arr.mapNotNull { el -> el.objectOrNull()?.let { parseUserMapIcon(it) } }
    }

    override suspend fun getDevicesLatest(time: Long?): DevicesLatestBatch {
        val payload = authGet("get_devices_latest") {
            time?.let { parameter("time", it) }
        }.body<JsonObject>()
        return DevicesLatestBatch(
            items = payload.array("items").map { parseDevice(it.jsonObject) },
            serverTimeSeconds = payload.long("time"),
        )
    }

    override suspend fun getDeviceSensors(deviceId: String): List<DeviceSensorRow> {
        val all = mutableListOf<DeviceSensorRow>()
        var page = 1
        while (true) {
            val payload = authGet("get_sensors") {
                parameter("device_id", deviceId)
                parameter("page", page)
            }.body<JsonObject>()
            val chunk = payload.array("data").takeIf { it.isNotEmpty() }
                ?: payload.array("items")
            chunk.forEach { element ->
                element.objectOrNull()?.let { all.add(parseSensorRow(it)) }
            }
            val lastPage = payload.int("last_page")
                ?: payload.obj("meta")?.int("last_page")
                ?: 1
            if (page >= lastPage) break
            page++
        }
        return all
    }

    override suspend fun getHistory(deviceId: String, range: HistoryRange): List<HistoryTrip> {
        val payload = authGet("get_history") {
            parameter("device_id", deviceId)
            parameter("from_date", range.fromDate)
            parameter("from_time", range.fromTime)
            parameter("to_date", range.toDate)
            parameter("to_time", range.toTime)
        }.body<JsonObject>()

        return payload.array("items").map { tripElement ->
            val trip = tripElement.jsonObject
            val points = trip.array("items").mapNotNull { item ->
                item.objectOrNull()?.let { point ->
                    HistoryPoint(
                        latitude = point.double("latitude") ?: return@let null,
                        longitude = point.double("longitude") ?: return@let null,
                        timestamp = point.string("time").orEmpty(),
                        address = point.string("address").orEmpty(),
                        speed = point.double("speed") ?: 0.0,
                    )
                }
            }
            HistoryTrip(
                title = trip.string("show").orEmpty(),
                statusCode = trip.int("status"),
                startTime = points.firstOrNull()?.timestamp.orEmpty(),
                endTime = points.lastOrNull()?.timestamp.orEmpty(),
                distanceLabel = trip["distance"]?.jsonPrimitive?.content.orEmpty(),
                durationLabel = trip.string("time").orEmpty(),
                points = points,
            )
        }
    }

    override suspend fun getAddReportData(): ReportCatalog {
        val payload = authGet("add_report_data").body<JsonObject>()
        return ReportCatalog(
            types = payload.array("types").mapNotNull { it.objectOrNull()?.toReportChoice("value") },
            formats = payload.array("formats").mapNotNull { it.objectOrNull()?.toReportChoice("value") },
            stops = payload.array("stops").mapNotNull { it.objectOrNull()?.toReportChoice("value") },
            filters = payload.array("filters").mapNotNull { it.objectOrNull()?.toReportChoice("value") },
        )
    }

    override suspend fun getReports(): ReportCatalog {
        val payload = authGet("get_reports").body<JsonObject>()
        val items = payload.obj("items")
        return ReportCatalog(
            types = (items?.array("types") ?: JsonArray(emptyList()))
                .mapNotNull { it.objectOrNull()?.toReportChoice("title") },
        )
    }

    override suspend fun generateReport(request: ReportGenerationRequest): ReportGenerationResponse {
        val payload = client.submitForm(
            url = "$baseUrl/generate_report",
            formParameters = Parameters.build {
                authQueryParameters().forEach { (key, value) -> append(key, value) }
                append("title", request.title)
                append("type", request.typeId)
                append("format", request.formatId)
                append("devices[]", request.deviceId)
                append("date_from", request.fromDate)
                append("date_to", request.toDate)
                append("from_time", request.fromTime)
                append("to_time", request.toTime)
                request.stopId?.let { append("stops", it) }
            },
        ).body<JsonObject>()

        return ReportGenerationResponse(
            status = payload.int("status"),
            url = payload.string("url"),
        )
    }

    override suspend fun getAlerts(): List<AlertItem> {
        val payload = authGet("get_alerts").body<JsonObject>()
        val items = payload.obj("items") ?: return emptyList()
        return items.array("alerts").map { element -> parseAlertItem(element.jsonObject) }
    }

    override suspend fun getAlertEvents(limit: Int, page: Int): List<AlertEventItem> {
        Log.d("KtorDrSecurityApi", "getAlertEvents: calling get_events API, limit=$limit, page=$page")
        val payload = authGet("get_events") {
            parameter("limit", limit)
            parameter("page", page)
        }.body<JsonObject>()
        Log.d("KtorDrSecurityApi", "getAlertEvents: response received, checking items")
        val items = payload.obj("items")
        if (items == null) {
            Log.d("KtorDrSecurityApi", "getAlertEvents: items is null, returning empty list")
            return emptyList()
        }
        val data = items.array("data")
        Log.d("KtorDrSecurityApi", "getAlertEvents: ${data.size} events in response")
        return data.map { parseAlertEventItem(it.jsonObject) }
    }

    override suspend fun getDeviceCommands(deviceId: String): List<CommandTemplate> {
        val payload = authGet("get_device_commands") {
            parameter("device_id", deviceId)
        }.body<JsonArray>()
        return payload.map { element ->
            val item = element.jsonObject
            CommandTemplate(
                type = item.string("type").orEmpty(),
                title = item.string("title").orEmpty(),
                connection = when (item.string("connection")?.lowercase()) {
                    "sms" -> CommandConnection.Sms
                    else -> CommandConnection.Gprs
                },
                attributes = item.array("attributes").map { attrElement ->
                    val attr = attrElement.jsonObject
                    CommandField(
                        name = attr.string("name").orEmpty(),
                        label = attr.string("title").orEmpty(),
                        type = attr.string("type").orEmpty(),
                        description = attr.string("description").orEmpty(),
                        required = attr.bool("required")
                            ?: attr.string("validation")?.contains("required", ignoreCase = true)
                            ?: false,
                        defaultValue = attr["default"]?.jsonPrimitive?.contentOrNull,
                        options = attr.array("options").map { optionElement ->
                            val option = optionElement.jsonObject
                            CommandFieldOption(
                                id = option["id"]?.jsonPrimitive?.content.orEmpty(),
                                title = option.string("title").orEmpty(),
                            )
                        },
                    )
                },
            )
        }
    }

    override suspend fun sendGprsCommand(request: CommandRequest) {
        authPost("send_gprs_command", requestPayload(request))
    }

    override suspend fun sendSmsCommand(request: CommandRequest) {
        client.submitForm(
            url = "$baseUrl/send_sms_command",
            formParameters = Parameters.build {
                authQueryParameters().forEach { (key, value) -> append(key, value) }
                append("message", request.message)
                append("devices[]", request.deviceId)
            },
        )
    }

    override suspend fun registerFcmToken(token: String, projectId: String?) {
        authPost(
            path = "fcm_token",
            payload = buildJsonObject {
                put("token", token)
                projectId?.let { put("project_id", it) }
            },
        )
    }

    private suspend fun authGet(path: String, builder: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {}) =
        client.get {
            url("$baseUrl/$path")
            authQuery()
            builder()
        }

    private suspend fun authPost(path: String, payload: JsonObject) {
        client.submitForm(
            url = "$baseUrl/$path",
            formParameters = Parameters.build {
                authQueryParameters().forEach { (key, value) -> append(key, value) }
                payload.forEach { (key, value) -> append(key, value.jsonPrimitive.content) }
            },
        )
    }

    private suspend fun io.ktor.client.request.HttpRequestBuilder.authQuery() {
        authQueryParameters().forEach { (key, value) -> parameter(key, value) }
    }

    private suspend fun authQueryParameters(): Map<String, String> {
        val session = sessionProvider() ?: throw SessionExpiredException()
        return mapOf(
            "lang" to session.language,
            "user_api_hash" to session.userApiHash,
        )
    }

    private fun requestPayload(request: CommandRequest): JsonObject = buildJsonObject {
        put("device_id", request.deviceId)
        put("type", request.type)
        put("message", request.message)
        put("auto_send_when_online", request.autoSendWhenOnline.toString())
    }

    private fun parseSensorRow(obj: JsonObject): DeviceSensorRow = DeviceSensorRow(
        name = obj.string("name").orEmpty(),
        type = obj.string("type").orEmpty(),
        tagName = obj.string("tag_name").orEmpty(),
        value = obj.string("value").orEmpty(),
        unit = obj.string("unit_of_measurement").orEmpty(),
        typeTitle = obj.string("type_title"),
    )

    private fun parseDevice(obj: JsonObject, groupName: String? = null): DeviceSummary {
        val sensors = obj.arrayOrJsonString("sensors", json)
        val latitude = obj.double("lat")
        val longitude = obj.double("lng")

        Log.d("DrSecurity", "=== parseDevice: ${obj.string("name")} ===")
        Log.d("DrSecurity", "  speed=${obj.double("speed")}, power=${obj.string("power")}, engine_status=${obj["engine_status"]}")
        Log.d("DrSecurity", "  sensors raw: $sensors")

        val power = obj.string("power").takeIf { it != "-" && it != null }
        val engineStatus = obj.bool("engine_status")

        val batteryLevel = findSensorValue(sensors, "bateria")
        val ignitionRaw = findSensorValue(sensors, "encendido") ?: findSensorValue(sensors, "acc")
        val ignition = ignitionRaw?.let { normalizeIgnitionDisplay(it) ?: it }

        Log.d("DrSecurity", "  parsed: battery=$batteryLevel, ignition=$ignition, power=$power")

        val mapIconJson = obj["map_icon"]?.jsonObject
        val mapIconPath = mapIconJson?.string("url")
            ?.takeIf { it.startsWith("http", ignoreCase = true) }
            ?: mapIconJson?.string("path")
        val mapIconId = obj.int("map_icon_id")
            ?: mapIconJson?.int("id")
            ?: obj["device_data"]?.jsonObject?.string("map_icon_id")?.toIntOrNull()
        val iconType = obj.string("icon_type")
            ?: obj["icon"]?.jsonObject?.string("type")
        val iconPath = obj["icon"]?.jsonObject?.let { ic ->
            ic.string("url")?.takeIf { it.startsWith("http", ignoreCase = true) } ?: ic.string("path")
        }
        val iconColor = obj.string("icon_color")
        val embeddedSensors = parseEmbeddedSensorRows(sensors)
        val telemetryRows = buildDeviceTelemetryRows(obj)
        val embeddedServices = parseEmbeddedServiceRows(obj.array("services"))
        val listDistanceText = telemetryRows.firstOrNull { it.label.equals("Distancia", ignoreCase = true) }?.value
        val listSpeedText = telemetryRows.firstOrNull { it.label.equals("Velocidad", ignoreCase = true) }?.value

        return DeviceSummary(
            id = obj["id"]?.jsonPrimitive?.content.orEmpty(),
            name = obj.string("name").orEmpty(),
            onlineStatus = obj.string("online").orEmpty(),
            alarm = obj.string("alarm").orEmpty(),
            lastUpdate = obj.string("time").orEmpty(),
            timestampSeconds = obj.long("timestamp"),
            speedKph = obj.double("speed") ?: 0.0,
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0,
            hasValidCoordinates = latitude != null && longitude != null,
            address = obj.string("address").orEmpty(),
            batteryLevel = batteryLevel,
            ignition = ignition,
            course = obj.string("course")
                ?: obj["course"]?.jsonPrimitive?.contentOrNull, // a veces viene como número
            iconType = iconType,
            groupName = groupName,
            mapIconPath = mapIconPath,
            mapIconId = mapIconId,
            iconPath = iconPath,
            iconColor = iconColor,
            embeddedSensorRows = embeddedSensors + telemetryRows + embeddedServices,
            listDistanceText = listDistanceText,
            listSpeedText = listSpeedText,
        )
    }

    private fun parseUserMapIcon(obj: JsonObject): UserMapIconItem? {
        val mapIcon = obj["map_icon"]?.jsonObject ?: return null
        val path = mapIcon.string("path") ?: return null
        return UserMapIconItem(
            id = obj.int("id") ?: 0,
            name = obj.string("name").orEmpty(),
            mapIconId = obj.int("map_icon_id") ?: mapIcon.int("id") ?: 0,
            path = path,
            width = mapIcon.string("width"),
            height = mapIcon.string("height"),
            active = obj.int("active") ?: 1,
        )
    }

    /** Distancia total y velocidad vienen en el objeto dispositivo, no siempre en `sensors`. */
    private fun buildDeviceTelemetryRows(obj: JsonObject): List<DeviceSensorListItem> {
        val out = mutableListOf<DeviceSensorListItem>()
        val dist = obj.double("total_distance")
        val distUnit = obj.string("unit_of_distance")?.trim()?.takeIf { it.isNotEmpty() } ?: "km"
        if (dist != null) {
            out.add(DeviceSensorListItem("Distancia", "${formatTelemetryNumber(dist)} $distUnit"))
        }
        val speed = obj.double("speed")
        if (speed != null) {
            val speedUnit = obj.string("distance_unit_hour")?.trim()?.takeIf { it.isNotEmpty() } ?: "kph"
            out.add(DeviceSensorListItem("Velocidad", "${formatTelemetryNumber(speed)} $speedUnit"))
        }
        return out
    }

    private fun formatTelemetryNumber(n: Double): String {
        val r = kotlin.math.round(n * 100.0) / 100.0
        val asLong = r.toLong()
        return if (kotlin.math.abs(r - asLong.toDouble()) < 1e-9) {
            asLong.toString()
        } else {
            r.toString()
        }
    }

    private fun parseEmbeddedSensorRows(sensors: JsonArray): List<DeviceSensorListItem> {
        val out = mutableListOf<DeviceSensorListItem>()
        sensors.forEach { element ->
            val o = element.objectOrNull() ?: return@forEach
            val label = o.string("name")?.trim()?.takeIf { it.isNotEmpty() }
                ?: o.string("type")?.trim()?.takeIf { it.isNotEmpty() }
                ?: return@forEach
            val raw = pickEmbeddedSensorRawValue(o)
            val value = when {
                raw.isEmpty() || raw == "-" -> "Sin dato"
                else -> normalizeIgnitionDisplay(raw) ?: raw
            }
            out.add(DeviceSensorListItem(label = label, value = value))
        }
        return out
    }

    private fun pickEmbeddedSensorRawValue(o: JsonObject): String {
        val v = o.string("value")?.trim().orEmpty()
        val valAlt = o.string("val")?.trim().orEmpty()
        val valBool = o.bool("val")
        val valFromBool = when (valBool) {
            true -> "ON"
            false -> "OFF"
            null -> ""
        }
        val valNum = o.double("val")
        val valFromNum = valNum?.let { formatTelemetryNumber(it) }.orEmpty()
        val scale = o.string("scale_value")?.trim().orEmpty()
        return when {
            v.isNotEmpty() && v != "-" -> v
            valAlt.isNotEmpty() && valAlt != "-" -> valAlt
            valFromBool.isNotEmpty() -> valFromBool
            valFromNum.isNotEmpty() && valFromNum != "-" -> valFromNum
            scale.isNotEmpty() && scale != "-" -> scale
            else -> v.ifEmpty { valAlt.ifEmpty { scale } }
        }
    }

    private fun parseEmbeddedServiceRows(services: JsonArray): List<DeviceSensorListItem> {
        val out = mutableListOf<DeviceSensorListItem>()
        services.forEach { element ->
            val o = element.objectOrNull() ?: return@forEach
            val name = o.string("name")?.trim()?.takeIf { it.isNotEmpty() } ?: return@forEach
            val raw = o.string("value")?.trim().orEmpty()
            val value = if (raw.isEmpty() || raw == "-") "Sin dato" else raw
            out.add(
                DeviceSensorListItem(
                    label = "Servicio · $name",
                    value = value,
                ),
            )
        }
        return out
    }

    private fun findSensorValue(sensors: JsonArray, namePattern: String): String? {
        sensors.forEach { sensor ->
            val sensorName = sensor.jsonObject.string("name").orEmpty()
            Log.d("DrSecurity", "  check sensor: name=$sensorName, value=${sensor.jsonObject.string("value")}")
        }
        return sensors.firstOrNull { sensor ->
            val sensorName = sensor.jsonObject.string("name").orEmpty().lowercase()
            sensorName.contains(namePattern.lowercase())
        }?.jsonObject?.let { pickEmbeddedSensorRawValue(it) }?.takeIf { it.isNotEmpty() && it != "-" }
    }

    private fun JsonObject.toReportChoice(labelField: String): ReportCatalogChoice? {
        val id = string("id") ?: return null
        val label = string(labelField)?.ifBlank { null }
            ?: string("title")?.ifBlank { null }
            ?: return null
        return ReportCatalogChoice(id = id, label = label)
    }
}
