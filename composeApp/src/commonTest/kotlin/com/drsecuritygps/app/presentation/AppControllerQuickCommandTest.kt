package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.CommandConnection
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
import com.drsecuritygps.app.core.model.UserProfile
import com.drsecuritygps.app.network.DrSecurityApi
import com.drsecuritygps.app.network.GeoapifyReverseGeocoder
import com.drsecuritygps.app.repository.AlertsRepository
import com.drsecuritygps.app.repository.AlertEventsRepository
import com.drsecuritygps.app.repository.AuthRepository
import com.drsecuritygps.app.repository.CommandsRepository
import com.drsecuritygps.app.repository.DevicesRepository
import com.drsecuritygps.app.repository.HistoryRepository
import com.drsecuritygps.app.repository.ProfileRepository
import com.drsecuritygps.app.repository.ReportRepository
import com.drsecuritygps.app.storage.AlertCacheGateway
import com.drsecuritygps.app.storage.AlertEventNotifiedGateway
import com.drsecuritygps.app.storage.CachedPayloadRow
import com.drsecuritygps.app.storage.DeviceCacheGateway
import com.drsecuritygps.app.storage.ReverseGeocodeCacheGateway
import com.drsecuritygps.app.storage.SecureStorage
import com.drsecuritygps.app.storage.SessionCacheGateway
import com.drsecuritygps.app.storage.SessionCacheRow
import com.drsecuritygps.app.storage.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppControllerQuickCommandTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    private val sampleDevice = DeviceSummary(
        id = "110",
        name = "TEST-1",
        timestampSeconds = 1L,
        latitude = 1.0,
        longitude = 1.0,
        hasValidCoordinates = true,
    )

    @Test
    fun `sendQuickCommand with empty device catalog shows no admit message`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val store = buildSessionStore()
        store.saveSession(Session(userApiHash = "token-1", email = "a@a.com"))
        val api = QuickCommandFakeApi(
            devices = listOf(sampleDevice),
            deviceCommands = emptyList(),
        )
        val controller = buildController(api, store)
        try {
            controller.bootstrap()
            drain(30)

            assertEquals("110", controller.state.value.selectedDeviceId, "debe quedar unidad seleccionada")
            controller.sendQuickCommand("engine_stop")
            drain(30)

            val msg = controller.state.value.activeMessage
            assertNotNull(msg)
            assertTrue(
                msg.contains("no admite", ignoreCase = true),
                "mensaje actual: $msg",
            )
            assertEquals(false, controller.state.value.isSendingCommand)
        } finally {
            controller.stop()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `sendQuickCommand dispatches gprs when template matches`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val store = buildSessionStore()
        store.saveSession(Session(userApiHash = "token-1", email = "a@a.com"))
        val api = QuickCommandFakeApi(
            devices = listOf(sampleDevice),
            deviceCommands = listOf(
                CommandTemplate(
                    type = "immobilize",
                    title = "Inmovilizar motor",
                    connection = CommandConnection.Gprs,
                    attributes = emptyList(),
                ),
            ),
        )
        val controller = buildController(api, store)
        try {
            controller.bootstrap()
            drain(30)

            controller.sendQuickCommand("engine_stop")
            drain(30)

            assertEquals(1, api.gprsCalls, "se esperaba un send_gprs_command")
            assertEquals("110", api.lastGprs?.deviceId)
            assertEquals("immobilize", api.lastGprs?.type)
            assertEquals("Comando enviado.", controller.state.value.activeMessage)
        } finally {
            controller.stop()
            Dispatchers.resetMain()
        }
    }

    private fun buildController(
        api: DrSecurityApi,
        store: SessionStore,
    ) = AppController(
        authRepository = AuthRepository(api, store),
        devicesRepository = DevicesRepository(api, store),
        alertsRepository = AlertsRepository(api, store),
        alertEventsRepository = AlertEventsRepository(api),
        historyRepository = HistoryRepository(
            api = api,
            sessionStore = store,
            reverseGeocoder = GeoapifyReverseGeocoder(apiKey = "", json = json),
        ),
        commandsRepository = CommandsRepository(api),
        profileRepository = ProfileRepository(api),
        reportRepository = ReportRepository(api, store),
        sessionStore = store,
    )

    private fun TestScope.drain(steps: Int) {
        repeat(steps) { runCurrent() }
    }

    private fun buildSessionStore(): SessionStore =
        SessionStore(
            secureStorage = QuickCommandFakeStorage(),
            json = json,
            sessionCache = QuickCommandFakeSessionCache(),
            deviceCache = QuickCommandFakeDeviceCache(),
            alertCache = QuickCommandFakeAlertCache(),
            alertEventNotified = QuickCommandFakeEventNotified(),
            reverseGeocodeCache = QuickCommandFakeRevGeo(),
        )
}

private class QuickCommandFakeApi(
    private val devices: List<DeviceSummary>,
    private val deviceCommands: List<CommandTemplate>,
) : DrSecurityApi {
    var gprsCalls: Int = 0
    var lastGprs: CommandRequest? = null

    override suspend fun login(email: String, password: String): Session =
        Session(userApiHash = "token-1", email = email)

    override suspend fun getUserProfile(): UserProfile = UserProfile(email = "a@a.com")

    override suspend fun getDevices(search: String?): List<DeviceSummary> = devices

    override suspend fun getUserMapIcons(search: String?) = emptyList<com.drsecuritygps.app.core.model.UserMapIconItem>()

    override suspend fun getDeviceSensors(deviceId: String): List<DeviceSensorRow> = emptyList()

    override suspend fun getDevicesLatest(time: Long?): DevicesLatestBatch =
        DevicesLatestBatch(items = emptyList(), serverTimeSeconds = time)

    override suspend fun getHistory(deviceId: String, range: HistoryRange): List<HistoryTrip> = emptyList()

    override suspend fun getAddReportData(): ReportCatalog = ReportCatalog()

    override suspend fun getReports(): ReportCatalog = ReportCatalog()

    override suspend fun generateReport(request: ReportGenerationRequest): ReportGenerationResponse =
        ReportGenerationResponse()

    override suspend fun getAlerts(): List<AlertItem> = emptyList()

    override suspend fun getAlertEvents(limit: Int, page: Int) = emptyList<com.drsecuritygps.app.core.model.AlertEventItem>()

    override suspend fun getDeviceCommands(deviceId: String): List<CommandTemplate> = deviceCommands

    override suspend fun sendGprsCommand(request: CommandRequest) {
        gprsCalls++
        lastGprs = request
    }

    override suspend fun sendSmsCommand(request: CommandRequest) = Unit

    override suspend fun registerFcmToken(token: String, projectId: String?) = Unit
}

private class QuickCommandFakeStorage : SecureStorage {
    private val values = mutableMapOf<String, String>()
    override suspend fun getString(key: String): String? = values[key]
    override suspend fun putString(key: String, value: String) {
        values[key] = value
    }
    override suspend fun remove(key: String) {
        values.remove(key)
    }
}

private class QuickCommandFakeSessionCache : SessionCacheGateway {
    private var row: SessionCacheRow? = null
    override fun read(): SessionCacheRow? = row
    override fun upsert(email: String?, language: String, updatedAt: Long) {
        row = SessionCacheRow(email = email, language = language)
    }
    override fun clear() {
        row = null
    }
}

private class QuickCommandFakeDeviceCache : DeviceCacheGateway {
    private val payloads = linkedMapOf<String, CachedPayloadRow>()
    override fun upsert(deviceId: String, payload: String, updatedAt: Long) {
        payloads[deviceId] = CachedPayloadRow(payload)
    }
    override fun readAll(): List<CachedPayloadRow> = payloads.values.toList()
    override fun clear() {
        payloads.clear()
    }
}

private class QuickCommandFakeAlertCache : AlertCacheGateway {
    override fun upsert(alertId: String, payload: String, updatedAt: Long) = Unit
    override fun readAll(): List<CachedPayloadRow> = emptyList()
    override fun clear() = Unit
}

private class QuickCommandFakeEventNotified : AlertEventNotifiedGateway {
    private val ids = mutableSetOf<String>()

    override fun count(): Long = ids.size.toLong()

    override fun eventExists(eventId: String): Boolean = eventId in ids

    override fun insertNotified(eventId: String, payload: String, notifiedAt: Long) {
        ids.add(eventId)
    }

    override fun clear() {
        ids.clear()
    }
}

private class QuickCommandFakeRevGeo : ReverseGeocodeCacheGateway {
    override fun read(cacheKey: String): String? = null
    override fun upsert(cacheKey: String, address: String, updatedAt: Long) = Unit
}
