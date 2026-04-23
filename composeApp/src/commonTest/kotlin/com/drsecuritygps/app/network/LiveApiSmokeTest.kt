package com.drsecuritygps.app.network

import com.drsecuritygps.app.core.model.ReportGenerationRequest
import com.drsecuritygps.app.core.model.ReportKind
import com.drsecuritygps.app.core.model.defaultStop
import com.drsecuritygps.app.core.model.resolveFormat
import com.drsecuritygps.app.core.model.resolveType
import com.drsecuritygps.app.core.model.Session
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LiveApiSmokeTest {
    @Test
    fun login_profile_devices_and_read_only_flows_work_against_live_backend() = runTest {
        val email = System.getenv("DRSECURITYGPS_TEST_EMAIL")?.takeIf { it.isNotBlank() } ?: return@runTest
        val password = System.getenv("DRSECURITYGPS_TEST_PASSWORD")?.takeIf { it.isNotBlank() } ?: return@runTest

        var session: Session? = null
        var unauthorizedTriggered = false
        val api = KtorDrSecurityApi(
            sessionProvider = { session },
            onUnauthorized = { unauthorizedTriggered = true },
        )

        val login = api.login(email, password)
        session = login

        assertTrue(login.userApiHash.isNotBlank(), "user_api_hash should not be blank")
        assertEquals(email, login.email)

        val profile = api.getUserProfile()
        assertEquals(email, profile.email)

        val devices = api.getDevices()
        assertTrue(devices.isNotEmpty(), "Expected at least one device from live backend")
        assertTrue(devices.any { it.id.isNotBlank() && it.name.isNotBlank() }, "Expected device with id and name")
        val firstDevice = devices.first()

        val searchedDevices = api.getDevices(search = firstDevice.name.take(3))
        assertTrue(searchedDevices.isNotEmpty(), "Search should return at least one device")

        val latest = api.getDevicesLatest(time = (Clock.System.now().toEpochMilliseconds() / 1000) - 3600)
        assertTrue(latest.items.isNotEmpty(), "Expected latest devices payload")

        val alerts = api.getAlerts()
        assertTrue(alerts.isNotEmpty(), "Expected alerts payload")
        assertTrue(alerts.any { it.id.isNotBlank() && it.title.isNotBlank() }, "Expected alert with id and title")

        val commands = api.getDeviceCommands(firstDevice.id)
        assertNotNull(commands, "Commands response should be present")

        val sensors = api.getDeviceSensors(firstDevice.id)
        assertNotNull(sensors, "get_sensors response should be present")

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val history = api.getHistory(
            deviceId = firstDevice.id,
            range = com.drsecuritygps.app.core.model.HistoryRange(
                fromDate = today,
                fromTime = "00:00",
                toDate = today,
                toTime = "23:59",
            ),
        )
        assertNotNull(history, "History response should be present even if empty")

        val addReportData = api.getAddReportData()
        val reportTypes = if (addReportData.types.isEmpty()) api.getReports().types else addReportData.types
        val reportCatalog = addReportData.copy(types = reportTypes)
        assertTrue(reportCatalog.formats.isNotEmpty(), "Expected report formats payload")

        val reportFormat = reportCatalog.resolveFormat()
        val reportType = reportCatalog.resolveType(ReportKind.DrivesStops)
            ?: reportCatalog.resolveType(ReportKind.VehicleHistory)
        if (reportFormat != null && reportType != null) {
            val report = api.generateReport(
                ReportGenerationRequest(
                    title = reportType.label,
                    deviceId = firstDevice.id,
                    typeId = reportType.id,
                    formatId = reportFormat.id,
                    fromDate = today,
                    fromTime = "00:00",
                    toDate = today,
                    toTime = "23:59",
                    stopId = reportCatalog.defaultStop()?.id,
                ),
            )
            assertTrue(!report.url.isNullOrBlank(), "Expected generated report URL")
        }

        assertTrue(!unauthorizedTriggered, "Unauthorized callback should not trigger during smoke test")
    }
}
