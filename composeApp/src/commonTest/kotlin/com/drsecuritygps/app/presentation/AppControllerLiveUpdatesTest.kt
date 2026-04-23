package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.core.SessionExpiredException
import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.CommandRequest
import com.drsecuritygps.app.core.model.CommandTemplate
import com.drsecuritygps.app.core.model.DeviceSensorRow
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.DevicesLatestBatch
import com.drsecuritygps.app.core.model.HistoryRange
import com.drsecuritygps.app.core.model.HistoryTrip
import com.drsecuritygps.app.core.model.MapFeedState
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
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AppControllerLiveUpdatesTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Test
    fun `background live update failure keeps map degraded without blocking modal`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val store = buildSessionStore()
        store.saveSession(Session(userApiHash = "token-1", email = "demo_gps@alertasecurity.com.pe"))

        val api = FakeDrSecurityApi(
            devices = listOf(
                DeviceSummary(
                    id = "110",
                    name = "AEZ-121",
                    timestampSeconds = 1774598235,
                    latitude = -8.08349,
                    longitude = -79.046191,
                    hasValidCoordinates = true,
                ),
            ),
            latestFailure = IOException("socket timeout"),
        )

        val controller = AppController(
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

        try {
            controller.bootstrap()

            repeat(4) { runCurrent() }
            assertNull(controller.state.value.activeMessage)

            advanceTimeBy(10_000)
            runCurrent()

            assertNull(controller.state.value.activeMessage)
            assertIs<MapFeedState.Degraded>(controller.state.value.mapFeed)
        } finally {
            controller.stop()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `background live update session expiry still logs out`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val store = buildSessionStore()
        store.saveSession(Session(userApiHash = "token-1", email = "demo_gps@alertasecurity.com.pe"))

        val api = FakeDrSecurityApi(
            devices = listOf(
                DeviceSummary(
                    id = "110",
                    name = "AEZ-121",
                    timestampSeconds = 1774598235,
                ),
            ),
            latestFailure = SessionExpiredException(),
        )

        val controller = AppController(
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

        try {
            controller.bootstrap()

            repeat(4) { runCurrent() }
            advanceTimeBy(10_000)
            runCurrent()

            assertNull(controller.state.value.session)
        } finally {
            controller.stop()
            Dispatchers.resetMain()
        }
    }

    private fun buildSessionStore(): SessionStore =
        SessionStore(
            secureStorage = FakeSecureStorage(),
            json = json,
            sessionCache = FakeSessionCacheGateway(),
            deviceCache = FakeDeviceCacheGateway(),
            alertCache = FakeAlertCacheGateway(),
            alertEventNotified = LiveUpdatesFakeAlertEventNotified(),
            reverseGeocodeCache = FakeReverseGeocodeCacheGateway(),
        )
}

private class FakeDrSecurityApi(
    private val devices: List<DeviceSummary>,
    private val latestFailure: Throwable? = null,
) : DrSecurityApi {
    override suspend fun login(email: String, password: String): Session =
        Session(userApiHash = "token-1", email = email)

    override suspend fun getUserProfile(): UserProfile = UserProfile(email = "demo_gps@alertasecurity.com.pe")

    override suspend fun getDevices(search: String?): List<DeviceSummary> = devices

    override suspend fun getUserMapIcons(search: String?) =
        emptyList<com.drsecuritygps.app.core.model.UserMapIconItem>()

    override suspend fun getDeviceSensors(deviceId: String): List<DeviceSensorRow> = emptyList()

    override suspend fun getDevicesLatest(time: Long?): DevicesLatestBatch {
        latestFailure?.let { throw it }
        return DevicesLatestBatch(items = emptyList(), serverTimeSeconds = time)
    }

    override suspend fun getHistory(deviceId: String, range: HistoryRange): List<HistoryTrip> = emptyList()

    override suspend fun getAddReportData(): ReportCatalog = ReportCatalog()

    override suspend fun getReports(): ReportCatalog = ReportCatalog()

    override suspend fun generateReport(request: ReportGenerationRequest): ReportGenerationResponse =
        ReportGenerationResponse()

    override suspend fun getAlerts(): List<AlertItem> = emptyList()

    override suspend fun getAlertEvents(limit: Int, page: Int) = emptyList<com.drsecuritygps.app.core.model.AlertEventItem>()

    override suspend fun getDeviceCommands(deviceId: String): List<CommandTemplate> = emptyList()

    override suspend fun sendGprsCommand(request: CommandRequest) = Unit

    override suspend fun sendSmsCommand(request: CommandRequest) = Unit

    override suspend fun registerFcmToken(token: String, projectId: String?) = Unit
}

private class FakeSecureStorage : SecureStorage {
    private val values = mutableMapOf<String, String>()

    override suspend fun getString(key: String): String? = values[key]

    override suspend fun putString(key: String, value: String) {
        values[key] = value
    }

    override suspend fun remove(key: String) {
        values.remove(key)
    }
}

private class FakeSessionCacheGateway : SessionCacheGateway {
    private var row: SessionCacheRow? = null

    override fun read(): SessionCacheRow? = row

    override fun upsert(email: String?, language: String, updatedAt: Long) {
        row = SessionCacheRow(email = email, language = language)
    }

    override fun clear() {
        row = null
    }
}

private class FakeDeviceCacheGateway : DeviceCacheGateway {
    private val payloads = linkedMapOf<String, CachedPayloadRow>()

    override fun upsert(deviceId: String, payload: String, updatedAt: Long) {
        payloads[deviceId] = CachedPayloadRow(payload)
    }

    override fun readAll(): List<CachedPayloadRow> = payloads.values.toList()

    override fun clear() {
        payloads.clear()
    }
}

private class FakeAlertCacheGateway : AlertCacheGateway {
    override fun upsert(alertId: String, payload: String, updatedAt: Long) = Unit

    override fun readAll(): List<CachedPayloadRow> = emptyList()

    override fun clear() = Unit
}

private class FakeReverseGeocodeCacheGateway : ReverseGeocodeCacheGateway {
    override fun read(cacheKey: String): String? = null

    override fun upsert(cacheKey: String, address: String, updatedAt: Long) = Unit
}

private class LiveUpdatesFakeAlertEventNotified : AlertEventNotifiedGateway {
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
