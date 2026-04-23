package com.drsecuritygps.app.presentation

import com.drsecuritygps.app.network.ApiEnvironment
import com.drsecuritygps.app.network.GeoapifyReverseGeocoder
import com.drsecuritygps.app.network.KtorDrSecurityApi
import com.drsecuritygps.app.repository.AlertsRepository
import com.drsecuritygps.app.repository.AlertEventsRepository
import com.drsecuritygps.app.repository.AuthRepository
import com.drsecuritygps.app.repository.CommandsRepository
import com.drsecuritygps.app.repository.DevicesRepository
import com.drsecuritygps.app.repository.HistoryRepository
import com.drsecuritygps.app.repository.ProfileRepository
import com.drsecuritygps.app.repository.ReportRepository
import com.drsecuritygps.app.platform.LocalAlertNotifier
import com.drsecuritygps.app.platform.NoOpLocalAlertNotifier
import com.drsecuritygps.app.storage.AppDatabaseFactory
import com.drsecuritygps.app.storage.DatabaseDriverFactory
import com.drsecuritygps.app.storage.SecureStorage
import com.drsecuritygps.app.storage.SessionStore
import kotlinx.serialization.json.Json

class AppGraph(
    secureStorage: SecureStorage,
    databaseDriverFactory: DatabaseDriverFactory,
    localAlertNotifier: LocalAlertNotifier = NoOpLocalAlertNotifier,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }
    private val database = AppDatabaseFactory(databaseDriverFactory).database
    private val sessionStore = SessionStore(secureStorage, database, json)
    private val api = KtorDrSecurityApi(
        sessionProvider = { sessionStore.readSession() },
        onUnauthorized = { sessionStore.clearSession() },
        baseUrl = ApiEnvironment.baseUrl,
        json = json,
    )
    private val geoapifyReverseGeocoder = GeoapifyReverseGeocoder(json = json)

    val controller = AppController(
        authRepository = AuthRepository(api, sessionStore),
        devicesRepository = DevicesRepository(api, sessionStore),
        alertsRepository = AlertsRepository(api, sessionStore),
        alertEventsRepository = AlertEventsRepository(api),
        historyRepository = HistoryRepository(api, sessionStore, geoapifyReverseGeocoder),
        commandsRepository = CommandsRepository(api),
        profileRepository = ProfileRepository(api),
        reportRepository = ReportRepository(api, sessionStore),
        sessionStore = sessionStore,
        localAlertNotifier = localAlertNotifier,
    )
}
