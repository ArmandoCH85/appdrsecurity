package com.drsecuritygps.app.presentation

import android.util.Log
import com.drsecuritygps.app.core.MapInvalidPayloadException
import com.drsecuritygps.app.core.MapOfflineException
import com.drsecuritygps.app.core.MapTimeoutException
import com.drsecuritygps.app.core.MapUnauthorizedException
import com.drsecuritygps.app.core.SessionExpiredException
import com.drsecuritygps.app.core.UiState
import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.AppSettings
import com.drsecuritygps.app.core.model.CommandConnection
import com.drsecuritygps.app.core.model.CommandRequest
import com.drsecuritygps.app.core.model.CommandTemplate
import com.drsecuritygps.app.core.model.DataSource
import com.drsecuritygps.app.core.model.DeviceDetail
import com.drsecuritygps.app.core.model.DeviceFilter
import com.drsecuritygps.app.core.model.DeviceLivePosition
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.GroupedDevices
import com.drsecuritygps.app.core.model.HistoryRange
import com.drsecuritygps.app.core.model.HistoryTrip
import com.drsecuritygps.app.core.model.ReportCatalog
import com.drsecuritygps.app.core.model.ReportGenerationRequest
import com.drsecuritygps.app.core.model.ReportKind
import com.drsecuritygps.app.core.model.defaultStop
import com.drsecuritygps.app.core.model.resolveFormat
import com.drsecuritygps.app.core.model.resolveType
import com.drsecuritygps.app.core.model.MapCapability
import com.drsecuritygps.app.core.model.MapFeedState
import com.drsecuritygps.app.core.model.MapaError
import com.drsecuritygps.app.core.model.Session
import com.drsecuritygps.app.core.model.UserProfile
import com.drsecuritygps.app.core.model.deviceStatusLabel
import com.drsecuritygps.app.core.model.toCourseDegrees
import com.drsecuritygps.app.core.model.hasRenderableCoordinates
import com.drsecuritygps.app.network.ApiEnvironment
import com.drsecuritygps.app.network.woxAssetUrl
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
import com.drsecuritygps.app.platform.ReportUrlOpener
import com.drsecuritygps.app.storage.SessionStore
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.utils.io.errors.IOException
import kotlin.math.roundToInt

enum class RootTab { Devices, Map, Alerts, Commands, Reports, Profile }

sealed interface Destination {
    data object Tabs : Destination
    data object DeviceDetail : Destination
    data object History : Destination
    data object ReportView : Destination
    data object AlertsMenu : Destination
}

data class AppUiState(
    val bootstrapLoading: Boolean = true,
    val session: Session? = null,
    val destination: Destination = Destination.Tabs,
    val currentTab: RootTab = RootTab.Map,
    val selectedDeviceId: String? = null,
    val showDeviceBottomSheet: Boolean = false,
    val devices: UiState<List<DeviceSummary>> = UiState.Loading,
    val alerts: UiState<List<AlertItem>> = UiState.Loading,
    val alertEvents: UiState<List<AlertEventItem>> = UiState.Empty,
    val history: UiState<List<HistoryTrip>> = UiState.Empty,
    val historyDate: String = currentLocalDate(),
    val historyPlaybackPointIndex: Int = 0,
    val historyIsPlaying: Boolean = false,
    val historyResolvedAddresses: Map<String, String> = emptyMap(),
    val historyResolvingKeys: Set<String> = emptySet(),
    val commands: UiState<List<CommandTemplate>> = UiState.Empty,
    val reports: UiState<ReportCatalog> = UiState.Loading,
    val reportFromDate: String = currentLocalDate(),
    val reportFromTime: String = "00:00",
    val reportToDate: String = currentLocalDate(),
    val reportToTime: String = currentLocalTime(),
    val reportBusyAction: ReportKind? = null,
    val reportFeedback: String? = null,
    val reportFeedbackIsError: Boolean = false,
    val profile: UiState<UserProfile> = UiState.Loading,
    val searchQuery: String = "",
    val deviceFilter: DeviceFilter = DeviceFilter.All,
    val settings: AppSettings = AppSettings(),
    val mapFeed: MapFeedState = MapFeedState.Loading,
    /** [mapIconId] Wox → ruta de imagen (solo resolución; no se muestra como lista en UI). */
    val woxMapIconPathByWoxId: Map<Int, String> = emptyMap(),
    val mapCapability: MapCapability = MapCapability.Available(),
    val loginEmail: String = "",
    val loginPassword: String = "",
    val isSubmittingLogin: Boolean = false,
    val isSendingCommand: Boolean = false,
    val activeMessage: String? = null,
)

class AppController(
    private val authRepository: AuthRepository,
    private val devicesRepository: DevicesRepository,
    private val alertsRepository: AlertsRepository,
    private val alertEventsRepository: AlertEventsRepository,
    private val historyRepository: HistoryRepository,
    private val commandsRepository: CommandsRepository,
    private val profileRepository: ProfileRepository,
    private val reportRepository: ReportRepository,
    private val sessionStore: SessionStore,
    private val localAlertNotifier: LocalAlertNotifier = NoOpLocalAlertNotifier,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    private val alertEventsSync = Mutex()
    private var liveUpdatesJob: Job? = null
    private var historyPlaybackJob: Job? = null
    private var lastDevicesTimestamp: Long? = null
    private var lastAlertsPollTimeMillis: Long? = null

    fun bootstrap() {
        scope.launch {
            val restored = authRepository.restoreSession()
            if (restored == null) {
                _state.update { it.copy(bootstrapLoading = false, session = null) }
                return@launch
            }
            _state.update { it.copy(session = restored, bootstrapLoading = false) }
            loadPostLoginData()
            startLiveUpdates()
        }
    }

    fun updateLoginEmail(value: String) = _state.update { it.copy(loginEmail = value) }
    fun updateLoginPassword(value: String) = _state.update { it.copy(loginPassword = value) }
    fun updateSearch(value: String) = _state.update { it.copy(searchQuery = value) }
    fun updateFilter(value: DeviceFilter) = _state.update { it.copy(deviceFilter = value) }

    fun login() {
        val current = _state.value
        if (current.loginEmail.isBlank() || current.loginPassword.isBlank()) {
            _state.update { it.copy(activeMessage = "Completa correo y contrasena.") }
            return
        }

        scope.launch {
            _state.update { it.copy(isSubmittingLogin = true, activeMessage = null) }
            runCatching { authRepository.login(current.loginEmail, current.loginPassword) }
                .onSuccess { (session, profile) ->
                    _state.update {
                        it.copy(
                            session = session,
                            profile = UiState.Success(profile),
                            isSubmittingLogin = false,
                            activeMessage = null,
                        )
                    }
                    loadDevices()
                    loadAlerts()
                    loadCommandsForSelected()
                    loadReportCatalog()
                    startLiveUpdates()
                }
                .onFailure { error ->
                    handleFailure(
                        error = error,
                        defaultMessage = "No se pudo iniciar sesion.",
                        unauthorizedMessage = "No se pudo iniciar sesion. Verifica tus credenciales.",
                    )
                    _state.update { it.copy(isSubmittingLogin = false) }
                }
        }
    }

    fun logout() {
        scope.launch {
            liveUpdatesJob?.cancel()
            stopHistoryPlayback()
            lastDevicesTimestamp = null
            lastAlertsPollTimeMillis = null
            authRepository.logout()
            _state.value = AppUiState(bootstrapLoading = false)
        }
    }

    fun refreshCurrentTab() {
        when (_state.value.currentTab) {
            RootTab.Devices, RootTab.Map -> loadDevices()
            RootTab.Alerts -> loadAlertEvents()
            RootTab.Commands -> loadCommandsForSelected()
            RootTab.Reports -> { /* No specific refresh needed */ }
            RootTab.Profile -> loadProfile()
        }
    }

    fun selectTab(tab: RootTab) {
        _state.update { it.copy(currentTab = tab, destination = Destination.Tabs) }
        if (tab == RootTab.Map) {
            emitMapPlatformCapability(_state.value.mapCapability)
        }
        refreshCurrentTab()
    }

    fun selectDevice(deviceId: String) {
        _state.update {
            it.copy(
                selectedDeviceId = deviceId,
                // Evita mapear quick commands con el catálogo de otra unidad hasta que llegue get_device_commands.
                commands = UiState.Empty,
            )
        }
        if (deviceId.isNotBlank()) loadCommands(deviceId)
    }

    fun clearDeviceSelection() {
        _state.update { it.copy(selectedDeviceId = null, showDeviceBottomSheet = false) }
    }

    fun openDetail(deviceId: String) {
        selectDevice(deviceId)
        _state.update { it.copy(showDeviceBottomSheet = true) }
    }

    fun dismissDeviceBottomSheet() {
        _state.update { it.copy(showDeviceBottomSheet = false) }
    }

    fun openHistory() {
        _state.update { it.copy(destination = Destination.History) }
        loadHistory()
    }

    fun openAlertsMenu() {
        selectTab(RootTab.Alerts)
    }

    fun generateReportForDevice(kind: ReportKind) {
        exportReport(kind)
    }

    fun openReportView() {
        _state.update { it.copy(destination = Destination.ReportView) }
    }

    fun refreshAlertEvents() {
        loadAlertEvents()
    }

    fun selectDeviceForHistory(deviceId: String) {
        selectDevice(deviceId)
        loadHistory()
    }

    fun selectDeviceAndGenerateReport(deviceId: String) {
        selectDevice(deviceId)
    }

    fun openHistoryForDevice(deviceId: String) {
        selectDevice(deviceId)
        _state.update { it.copy(destination = Destination.History) }
        loadHistory()
    }

    fun selectHistoryDate(date: String) {
        if (_state.value.historyDate == date) return
        stopHistoryPlayback()
        _state.update { it.copy(historyDate = date, historyPlaybackPointIndex = 0) }
        loadHistory()
    }

    fun shiftHistoryDate(days: Int) {
        val nextDate = LocalDate.parse(_state.value.historyDate) + DatePeriod(days = days)
        selectHistoryDate(nextDate.toString())
    }

    fun updateReportFromDateTime(date: String, time: String) {
        _state.update {
            it.copy(
                reportFromDate = date,
                reportFromTime = time,
                reportFeedback = null,
                reportFeedbackIsError = false,
            )
        }
    }

    fun updateReportToDateTime(date: String, time: String) {
        _state.update {
            it.copy(
                reportToDate = date,
                reportToTime = time,
                reportFeedback = null,
                reportFeedbackIsError = false,
            )
        }
    }

    fun refreshReports() {
        loadReportCatalog(force = true)
    }

    fun generateVehicleHistoryReport() = exportReport(ReportKind.VehicleHistory)

    fun generateDrivesStopsReport() = exportReport(ReportKind.DrivesStops)

    fun toggleHistoryPlayback() {
        val totalPoints = currentHistoryPoints().size
        if (totalPoints <= 1) return
        if (_state.value.historyIsPlaying) {
            stopHistoryPlayback()
        } else {
            startHistoryPlayback(totalPoints)
        }
    }

    fun seekHistoryPlayback(fraction: Float) {
        val points = currentHistoryPoints()
        if (points.isEmpty()) return
        stopHistoryPlayback()
        val clamped = fraction.coerceIn(0f, 1f)
        val targetIndex = (points.lastIndex * clamped).roundToInt()
        _state.update { it.copy(historyPlaybackPointIndex = targetIndex) }
    }

    fun resolveHistoryAddress(segmentId: String, latitude: Double, longitude: Double) {
        val current = _state.value
        if (segmentId in current.historyResolvingKeys || current.historyResolvedAddresses.containsKey(segmentId)) return

        scope.launch {
            _state.update {
                it.copy(
                    historyResolvingKeys = it.historyResolvingKeys + segmentId,
                    activeMessage = null,
                )
            }
            runCatching {
                historyRepository.resolveAddress(latitude = latitude, longitude = longitude)
            }.onSuccess { address ->
                if (address.isNullOrBlank()) {
                    _state.update {
                        it.copy(
                            historyResolvingKeys = it.historyResolvingKeys - segmentId,
                            activeMessage = "No pudimos obtener la dirección para ese punto.",
                        )
                    }
                    return@onSuccess
                }

                _state.update {
                    it.copy(
                        historyResolvingKeys = it.historyResolvingKeys - segmentId,
                        historyResolvedAddresses = it.historyResolvedAddresses + (segmentId to address),
                        activeMessage = null,
                    )
                }
            }.onFailure { error ->
                if (error is SessionExpiredException) {
                    handleFailure(error, "No pudimos obtener la dirección para ese punto.")
                    return@onFailure
                }
                handleFailure(error, "No pudimos obtener la dirección para ese punto.")
                _state.update {
                    it.copy(
                        historyResolvingKeys = it.historyResolvingKeys - segmentId,
                    )
                }
            }
        }
    }

    fun goBack() {
        stopHistoryPlayback()
        _state.update { it.copy(destination = Destination.Tabs) }
    }

    fun dismissMessage() {
        _state.update { it.copy(activeMessage = null) }
    }

    fun sendCommand(template: CommandTemplate, rawMessage: String) {
        val deviceId = _state.value.selectedDeviceId ?: return
        scope.launch {
            _state.update { it.copy(isSendingCommand = true) }
            runCatching {
                commandsRepository.sendCommand(
                    template = template,
                    request = CommandRequest(
                        deviceId = deviceId,
                        type = template.type,
                        message = rawMessage,
                    ),
                )
            }.onSuccess {
                _state.update {
                    it.copy(isSendingCommand = false, activeMessage = "Comando enviado.")
                }
            }.onFailure { error ->
                if (error is SessionExpiredException) {
                    handleFailure(error, "No se pudo enviar el comando.")
                    return@onFailure
                }
                handleFailure(error, "No se pudo enviar el comando.")
                _state.update { it.copy(isSendingCommand = false) }
            }
        }
    }

    fun sendQuickCommand(type: String) {
        val deviceId = _state.value.selectedDeviceId ?: return
        scope.launch {
            _state.update { it.copy(isSendingCommand = true) }
            runCatching {
                val commands = when (val commandsState = _state.value.commands) {
                    is UiState.Success -> commandsState.data
                    else -> commandsRepository.getDeviceCommands(deviceId).also { available ->
                        _state.update {
                            it.copy(commands = if (available.isEmpty()) UiState.Empty else UiState.Success(available))
                        }
                    }
                }
                val template = resolveQuickCommandTemplate(type, commands)
                    ?: throw QuickCommandException("Esta unidad no admite este comando.")
                val message = buildQuickCommandMessage(template)
                    ?: throw QuickCommandException(
                        "El comando “${template.title}” requiere parámetros. Configúralo en Comandos."
                    )
                commandsRepository.sendCommand(
                    template = template,
                    request = CommandRequest(
                        deviceId = deviceId,
                        type = template.type,
                        message = message,
                    ),
                )
            }.onSuccess {
                _state.update {
                    it.copy(isSendingCommand = false, activeMessage = "Comando enviado.")
                }
            }.onFailure { error ->
                if (error is SessionExpiredException) {
                    handleFailure(error, "No se pudo enviar el comando.")
                    return@onFailure
                }
                if (error is QuickCommandException) {
                    _state.update { it.copy(isSendingCommand = false, activeMessage = error.message) }
                    return@onFailure
                }
                handleFailure(error, "No se pudo enviar el comando.")
                _state.update { it.copy(isSendingCommand = false) }
            }
        }
    }

    fun registerPushToken(token: String) {
        scope.launch {
            runCatching { profileRepository.registerFcmToken(token) }
        }
    }

    fun stop() {
        liveUpdatesJob?.cancel()
        stopHistoryPlayback()
        scope.coroutineContext[Job]?.cancel()
    }

    fun visibleDevices(): List<DeviceSummary> {
        val devices = (_state.value.devices as? UiState.Success)?.data.orEmpty()
        return filterDevices(devices, _state.value.searchQuery, _state.value.deviceFilter)
    }

    fun groupedDevices(): GroupedDevices {
        val devices = (_state.value.devices as? UiState.Success)?.data.orEmpty()
        val grouped = groupDevices(devices)
        return filterGroupedDevices(grouped, _state.value.searchQuery, _state.value.deviceFilter)
    }

    fun visibleMapDevices(): List<DeviceSummary> = visibleDevices().filter(DeviceSummary::hasRenderableCoordinates)

    /**
     * URL del marcador en mapa: prioriza icono de mapa Wox (`map_icon` / [mapIconId] + catálogo),
     * luego el icono de objeto (`icon.path` / device icon).
     */
    fun vehicleMapIconUrl(device: DeviceSummary): String? {
        // Tipo "arrow" (p. ej. LEILA): Wox rota un PNG; en app usamos flecha vectorial + course (ver MapBridge)
        if (device.iconType?.equals("arrow", ignoreCase = true) == true) {
            return null
        }
        val path = device.mapIconPath
            ?: device.mapIconId?.let { _state.value.woxMapIconPathByWoxId[it] }
            ?: device.iconPath
        return path?.let { woxAssetUrl(it) }
    }

    fun livePositions(): List<DeviceLivePosition> = visibleMapDevices().map {
        DeviceLivePosition(
            id = it.id,
            title = it.name,
            latitude = it.latitude,
            longitude = it.longitude,
            status = deviceStatusLabel(it.onlineStatus),
            iconUrl = vehicleMapIconUrl(it),
            iconColor = it.iconColor,
            courseDegrees = it.course.toCourseDegrees(),
        )
    }

    fun selectedDeviceDetail(): DeviceDetail? {
        val selected = visibleDevices().firstOrNull { it.id == _state.value.selectedDeviceId }
            ?: (_state.value.devices as? UiState.Success)?.data?.firstOrNull { it.id == _state.value.selectedDeviceId }
            ?: return null
        Log.d("DrSecurity", "selectedDeviceDetail: id=${selected.id}, name=${selected.name}, batteryLevel=${selected.batteryLevel}, ignition=${selected.ignition}")
        return DeviceDetail(
            summary = selected,
            batteryPercent = selected.batteryLevel,
            ignitionState = selected.ignition,
            lastReport = selected.lastUpdate,
        )
    }

    private suspend fun loadPostLoginData() {
        loadProfile()
        loadDevices()
        loadAlerts()
        loadCommandsForSelected()
        loadReportCatalog()
    }

    private fun loadReportCatalog(force: Boolean = false) {
        val currentReports = _state.value.reports
        if (!force && currentReports is UiState.Success && currentReports.data.types.isNotEmpty()) return

        scope.launch {
            _state.update {
                it.copy(
                    reports = UiState.Loading,
                    reportFeedback = null,
                    reportFeedbackIsError = false,
                )
            }
            runCatching { reportRepository.loadCatalog(forceRefresh = force) }
                .onSuccess { catalog ->
                    _state.update {
                        it.copy(
                            reports = UiState.Success(catalog),
                            reportFeedback = null,
                            reportFeedbackIsError = false,
                        )
                    }
                }
                .onFailure { error ->
                    if (error is SessionExpiredException) {
                        handleFailure(error, "No pudimos cargar el catalogo de reportes.", FailureTarget.Report)
                        return@onFailure
                    }
                    handleFailure(error, "No pudimos cargar el catalogo de reportes.", FailureTarget.Report)
                    _state.update {
                        it.copy(reports = UiState.Error("No pudimos cargar el catalogo de reportes.", error))
                    }
                }
        }
    }

    private fun loadDevices() {
        scope.launch {
            val search = _state.value.searchQuery.takeIf(String::isNotBlank)
            launch {
                runCatching { devicesRepository.getUserMapIcons(search) }
                    .onSuccess { list ->
                        _state.update {
                            it.copy(woxMapIconPathByWoxId = list.associate { item -> item.mapIconId to item.path })
                        }
                    }
            }
            val cached = devicesRepository.getCachedDevices()
            if (cached.isNotEmpty()) {
                _state.update {
                    it.copy(
                        devices = UiState.Success(cached),
                        mapFeed = mapFeedStateFor(cached, source = DataSource.Cache, stale = true),
                    )
                }
            }

            runCatching { devicesRepository.getDevices(search) }
                .onSuccess { devices ->
                    val selectedDeviceId = _state.value.selectedDeviceId ?: devices.firstOrNull()?.id
                    _state.update {
                        it.copy(
                            devices = if (devices.isEmpty()) UiState.Empty else UiState.Success(devices),
                            selectedDeviceId = selectedDeviceId,
                            mapFeed = mapFeedStateFor(devices, source = DataSource.Network),
                        )
                    }
                    if (selectedDeviceId != null) {
                        loadCommands(selectedDeviceId)
                    } else {
                        _state.update { it.copy(commands = UiState.Empty) }
                    }
                    emitMapStateTransition(cause = "network_success", result = "ready")
                    lastDevicesTimestamp = devices.maxOfOrNull { it.timestampSeconds ?: 0L }?.takeIf { it > 0 }
                }
                .onFailure { error ->
                    applyMapFeedFailure(
                        error = error,
                        showGlobalMessage = true,
                    )
                }
        }
    }

    private fun loadAlerts() {
        scope.launch {
            val cached = alertsRepository.getCachedAlerts()
            if (cached.isNotEmpty()) {
                _state.update { it.copy(alerts = alertsUiState(cached)) }
            }
            runCatching { alertsRepository.getAlerts() }
                .onSuccess { alerts ->
                    lastAlertsPollTimeMillis = Clock.System.now().toEpochMilliseconds()
                    _state.update { it.copy(alerts = alertsUiState(alerts)) }
                }
                .onFailure { error -> handleFailure(error, "No se pudieron cargar las alertas.") }
        }
    }

    private suspend fun fetchAndResolveAlertEvents(): List<AlertEventItem> {
        Log.d("AppController", "fetchAndResolveAlertEvents: calling repository.getAlertEvents()")
        val events = alertEventsRepository.getAlertEvents()
        Log.d("AppController", "fetchAndResolveAlertEvents: received ${events.size} events")
        return events.map { event ->
            event.copy(deviceName = resolveEventDeviceName(event.deviceId, visibleDevices()))
        }
    }

    private var lastSuccessfulAlertEvents: List<AlertEventItem>? = null

    private fun loadAlertEvents() {
        scope.launch {
            Log.d("AppController", "=== loadAlertEvents START ===")
            _state.update { it.copy(alertEvents = UiState.Loading) }
            val session = _state.value.session
            if (session == null) {
                Log.d("AppController", "loadAlertEvents: session is null, returning Empty")
                _state.update { it.copy(alertEvents = UiState.Empty) }
                return@launch
            }
            Log.d("AppController", "loadAlertEvents: session OK, userApiHash=${session.userApiHash.take(8)}...")
            try {
                val resolved = alertEventsSync.withLock {
                    Log.d("AppController", "loadAlertEvents: acquiring lock, fetching events")
                    val r = fetchAndResolveAlertEvents()
                    Log.d("AppController", "loadAlertEvents: got ${r.size} events from API")
                    val newOnes = sessionStore.consumeNewAlertEventsForNotifications(r)
                    Log.d("AppController", "loadAlertEvents: ${newOnes.size} new events for notifications")
                    if (newOnes.isNotEmpty() && _state.value.settings.notificationsEnabled) {
                        localAlertNotifier.showNewAlertEvents(newOnes)
                    }
                    r
                }
                lastSuccessfulAlertEvents = resolved
                Log.d("AppController", "loadAlertEvents: SUCCESS, updating UI with ${resolved.size} events")
                _state.update { it.copy(alertEvents = alertEventsUiState(resolved)) }
            } catch (error: Throwable) {
                Log.e("AppController", "loadAlertEvents: FAILED - ${error::class.simpleName}: ${error.message}", error)
                if (error is SessionExpiredException) {
                    Log.d("AppController", "loadAlertEvents: SessionExpiredException, logging out")
                    scope.launch { logout() }
                    _state.update { it.copy(alertEvents = UiState.Empty) }
                } else {
                    val fallback = lastSuccessfulAlertEvents
                    if (fallback != null) {
                        Log.d("AppController", "loadAlertEvents: using fallback cache with ${fallback.size} events")
                        _state.update { it.copy(alertEvents = alertEventsUiState(fallback)) }
                    } else {
                        Log.d("AppController", "loadAlertEvents: no fallback, showing error")
                        handleFailure(error, "No se pudieron cargar los eventos de alerta.")
                        _state.update { it.copy(alertEvents = UiState.Empty) }
                    }
                }
            }
            Log.d("AppController", "=== loadAlertEvents END ===")
        }
    }

    /**
     * Sincroniza eventos con el servidor, notifica en sistema los nuevos (diff SQLite) y actualiza la lista
     * solo si el usuario está en [RootTab.Alerts]. Se ejecuta en el ciclo de [LIVE_UPDATES_INTERVAL_MILLIS]
     * aunque otra pestaña esté activa, para no depender de FCM.
     */
    private suspend fun syncAlertEventsFromServer() {
        Log.d("AppController", ">>> syncAlertEventsFromServer START")
        if (_state.value.session == null) {
            Log.d("AppController", "syncAlertEventsFromServer: session null, skipping")
            return
        }
        try {
            val resolved = alertEventsSync.withLock {
                val r = fetchAndResolveAlertEvents()
                val newOnes = sessionStore.consumeNewAlertEventsForNotifications(r)
                if (newOnes.isNotEmpty() && _state.value.settings.notificationsEnabled) {
                    localAlertNotifier.showNewAlertEvents(newOnes)
                }
                r
            }
            if (_state.value.currentTab == RootTab.Alerts) {
                Log.d("AppController", "syncAlertEventsFromServer: updating UI with ${resolved.size} events")
                _state.update { it.copy(alertEvents = alertEventsUiState(resolved)) }
            }
            Log.d("AppController", "<<< syncAlertEventsFromServer END")
        } catch (error: Throwable) {
            if (error is SessionExpiredException) {
                Log.d("AppController", "syncAlertEventsFromServer: SessionExpiredException, logging out")
                logout()
            } else {
                Log.w("AppController", "syncAlertEventsFromServer failed: ${error::class.simpleName}: ${error.message}", error)
            }
        }
    }

    private fun loadProfile() {
        scope.launch {
            runCatching { profileRepository.getProfile() }
                .onSuccess { profile -> _state.update { it.copy(profile = UiState.Success(profile)) } }
                .onFailure { error -> handleFailure(error, "No se pudo cargar el perfil.") }
        }
    }

    private fun loadCommandsForSelected() {
        val deviceId = _state.value.selectedDeviceId ?: run {
            _state.update { it.copy(commands = UiState.Empty) }
            return
        }
        loadCommands(deviceId)
    }

    private fun loadHistory() {
        val deviceId = _state.value.selectedDeviceId ?: return
        scope.launch {
            stopHistoryPlayback()
            _state.update {
                it.copy(
                    history = UiState.Loading,
                    historyPlaybackPointIndex = 0,
                    historyIsPlaying = false,
                    historyResolvedAddresses = emptyMap(),
                    historyResolvingKeys = emptySet(),
                )
            }
            val historyDate = _state.value.historyDate
            runCatching {
                historyRepository.getHistory(
                    deviceId = deviceId,
                    range = HistoryRange(
                        fromDate = historyDate,
                        fromTime = "00:00",
                        toDate = historyDate,
                        toTime = "23:59",
                    ),
                )
            }.onSuccess { trips ->
                _state.update {
                    it.copy(
                        history = if (trips.isEmpty()) UiState.Empty else UiState.Success(trips),
                        historyPlaybackPointIndex = 0,
                        historyIsPlaying = false,
                    )
                }
            }.onFailure { error -> handleFailure(error, "No se pudo cargar el historial.") }
        }
    }

    private fun exportReport(kind: ReportKind) {
        val current = _state.value
        val deviceId = current.selectedDeviceId ?: run {
            _state.update { it.copy(reportFeedback = "Selecciona una unidad antes de exportar.", reportFeedbackIsError = true) }
            return
        }
        val catalog = (current.reports as? UiState.Success)?.data ?: run {
            _state.update { it.copy(reportFeedback = "Cargando catalogo de reportes...", reportFeedbackIsError = false) }
            return
        }
        val type = catalog.resolveType(kind) ?: run {
            _state.update { it.copy(reportFeedback = "Reporte no disponible en este servidor.", reportFeedbackIsError = true) }
            return
        }
        val format = catalog.resolveFormat() ?: run {
            _state.update { it.copy(reportFeedback = "Reporte no disponible en este servidor.", reportFeedbackIsError = true) }
            return
        }
        val startKey = "${current.reportFromDate} ${current.reportFromTime}"
        val endKey = "${current.reportToDate} ${current.reportToTime}"
        if (startKey > endKey) {
            _state.update { it.copy(reportFeedback = "La fecha/hora de inicio no puede ser mayor que la final.", reportFeedbackIsError = true) }
            return
        }

        val stop = if (kind == ReportKind.DrivesStops) catalog.defaultStop() else null

        scope.launch {
            _state.update {
                it.copy(
                    reportBusyAction = kind,
                    reportFeedback = null,
                )
            }
            runCatching {
                reportRepository.generateReport(
                    ReportGenerationRequest(
                        title = type.label,
                        deviceId = deviceId,
                        typeId = type.id,
                        formatId = format.id,
                        fromDate = current.reportFromDate,
                        fromTime = current.reportFromTime,
                        toDate = current.reportToDate,
                        toTime = current.reportToTime,
                        stopId = stop?.id,
                    ),
                )
            }.onSuccess { generated ->
                val url = normalizeReportUrl(generated.url.orEmpty())
                if (url.isBlank()) {
                    _state.update {
                        it.copy(
                            reportBusyAction = null,
                            reportFeedback = "El servidor no devolvio un enlace de descarga.",
                            reportFeedbackIsError = true,
                        )
                    }
                    return@onSuccess
                }

                if (!ReportUrlOpener.open(url)) {
                    _state.update {
                        it.copy(
                            reportBusyAction = null,
                            reportFeedback = "No pudimos abrir el reporte en este dispositivo.",
                            reportFeedbackIsError = true,
                        )
                    }
                    return@onSuccess
                }

                _state.update {
                    it.copy(
                        reportBusyAction = null,
                        reportFeedback = "Reporte generado y abierto.",
                        reportFeedbackIsError = false,
                    )
                }
            }.onFailure { error ->
                if (error is SessionExpiredException) {
                    handleFailure(error, "No pudimos generar el reporte.", FailureTarget.Report)
                    return@onFailure
                }
                handleFailure(error, "No pudimos generar el reporte.", FailureTarget.Report)
                _state.update { it.copy(reportBusyAction = null) }
            }
        }
    }

    private fun startLiveUpdates() {
        liveUpdatesJob?.cancel()
        liveUpdatesJob = scope.launch {
            while (true) {
                delay(LIVE_UPDATES_INTERVAL_MILLIS)
                runCatching { devicesRepository.getDevicesLatest(lastDevicesTimestamp) }
                    .onSuccess { batch ->
                        if (batch.items.isNotEmpty()) {
                            val current = (_state.value.devices as? UiState.Success)?.data.orEmpty()
                            val merged = mergeDevices(current, batch.items)
                            _state.update {
                                it.copy(
                                    devices = UiState.Success(merged),
                                    mapFeed = mapFeedStateFor(merged, source = DataSource.Network),
                                )
                            }
                        }
                        batch.serverTimeSeconds?.let { serverTime ->
                            if (lastDevicesTimestamp == null || serverTime > lastDevicesTimestamp!!) {
                                lastDevicesTimestamp = serverTime
                            }
                        }
                    }
                    .onFailure { error ->
                        emitMapRetryAttempt(cause = error::class.simpleName ?: "unknown", attempt = 1, max = 1)
                        applyMapFeedFailure(
                            error = error,
                            showGlobalMessage = false,
                        )
                    }
                pollAlertsIfDue()
                syncAlertEventsFromServer()
            }
        }
    }

    private suspend fun pollAlertsIfDue(nowMillis: Long = Clock.System.now().toEpochMilliseconds()) {
        if (!shouldPollAlerts(lastAlertsPollTimeMillis, nowMillis)) return

        runCatching { alertsRepository.getAlerts() }
            .onSuccess { alerts ->
                lastAlertsPollTimeMillis = nowMillis
                _state.update { it.copy(alerts = alertsUiState(alerts)) }
            }
            .onFailure { error ->
                if (error is SessionExpiredException) {
                    logout()
                }
            }
    }

    private fun loadCommands(deviceId: String) {
        scope.launch {
            runCatching { commandsRepository.getDeviceCommands(deviceId) }
                .onSuccess { commands ->
                    _state.update { it.copy(commands = if (commands.isEmpty()) UiState.Empty else UiState.Success(commands)) }
                }
                .onFailure { error -> handleFailure(error, "No se pudieron cargar los comandos.") }
        }
    }

    private fun handleFailure(
        error: Throwable,
        defaultMessage: String? = null,
        target: FailureTarget = FailureTarget.Global,
        unauthorizedMessage: String = "Tu sesion no es valida. Inicia sesion otra vez.",
    ) {
        if (error is SessionExpiredException) {
            logout()
            return
        }

        val message = error.toFriendlyMessage(
            defaultMessage = defaultMessage ?: when (target) {
                FailureTarget.Global -> "Ocurrio un error."
                FailureTarget.Report -> "No pudimos completar la accion."
            },
            unauthorizedMessage = unauthorizedMessage,
        )
        _state.update {
            when (target) {
                FailureTarget.Global -> it.copy(activeMessage = message)
                FailureTarget.Report -> it.copy(reportFeedback = message, reportFeedbackIsError = true)
            }
        }
    }

    private fun applyMapFeedFailure(
        error: Throwable,
        showGlobalMessage: Boolean,
    ) {
        if (error is SessionExpiredException) {
            handleFailure(error)
            return
        }

        val mappedError = mapToMapaError(error)
        val currentDevices = (_state.value.devices as? UiState.Success)?.data.orEmpty()

        if (currentDevices.isNotEmpty()) {
            _state.update { it.copy(mapFeed = MapFeedState.Degraded(currentDevices, mappedError)) }
            emitMapFallbackCache(cause = error::class.simpleName ?: "unknown")
            emitMapStateTransition(cause = "network_failure", result = "fallback_cache")
        } else {
            _state.update { it.copy(mapFeed = MapFeedState.Error(mappedError)) }
            emitMapStateTransition(cause = "network_failure", result = "error")
        }

        if (showGlobalMessage) {
            handleFailure(error, mappedError.toUserMessage())
        }
    }

    private enum class FailureTarget {
        Global,
        Report,
    }

    private fun emitMapStateTransition(cause: String, result: String) {
        emitMapEvent(
            event = MAP_STATE_TRANSITION,
            cause = cause,
            result = result,
        )
    }

    private fun emitMapRetryAttempt(cause: String, attempt: Int, max: Int) {
        emitMapEvent(
            event = MAP_RETRY_ATTEMPT,
            cause = cause,
            result = "attempt_${attempt}_of_$max",
        )
    }

    private fun emitMapFallbackCache(cause: String) {
        emitMapEvent(
            event = MAP_FALLBACK_CACHE,
            cause = cause,
            result = "fallback_cache",
        )
    }

    private fun emitMapPlatformCapability(capability: MapCapability) {
        val result = when (capability) {
            is MapCapability.Available -> "available"
            is MapCapability.Unavailable -> "unavailable"
        }
        emitMapEvent(
            event = MAP_PLATFORM_CAPABILITY,
            cause = "platform",
            result = result,
            platform = capability.platformName(),
        )
    }

    private fun emitMapEvent(
        event: String,
        cause: String,
        result: String,
        platform: String = _state.value.mapCapability.platformName(),
    ) {
        println("map_event=$event cause=$cause platform=$platform result=$result")
    }

    private companion object {
        const val MAP_STATE_TRANSITION = "map_state_transition"
        const val MAP_RETRY_ATTEMPT = "map_retry_attempt"
        const val MAP_FALLBACK_CACHE = "map_fallback_cache"
        const val MAP_PLATFORM_CAPABILITY = "map_platform_capability"
        const val LIVE_UPDATES_INTERVAL_MILLIS = 10_000L
        const val ALERTS_POLL_INTERVAL_MILLIS = 30_000L
    }

    private fun mapFeedStateFor(
        devices: List<DeviceSummary>,
        source: DataSource,
        stale: Boolean = false,
    ): MapFeedState {
        val mappableDevices = devices.filter(DeviceSummary::hasRenderableCoordinates)
        if (mappableDevices.isEmpty()) {
            return MapFeedState.Empty(source = source)
        }
        return MapFeedState.Ready(devices = mappableDevices, source = source, stale = stale)
    }

    private fun startHistoryPlayback(totalPoints: Int) {
        historyPlaybackJob?.cancel()
        val startIndex = _state.value.historyPlaybackPointIndex.takeUnless { it >= totalPoints - 1 } ?: 0
        _state.update { it.copy(historyPlaybackPointIndex = startIndex, historyIsPlaying = true) }
        historyPlaybackJob = scope.launch {
            while (_state.value.historyPlaybackPointIndex < totalPoints - 1) {
                delay(900)
                _state.update {
                    it.copy(
                        historyPlaybackPointIndex = (it.historyPlaybackPointIndex + 1).coerceAtMost(totalPoints - 1),
                    )
                }
            }
            _state.update { it.copy(historyIsPlaying = false) }
        }
    }

    private fun stopHistoryPlayback() {
        historyPlaybackJob?.cancel()
        historyPlaybackJob = null
        if (_state.value.historyIsPlaying) {
            _state.update { it.copy(historyIsPlaying = false) }
        }
    }

    private fun currentHistoryPoints() =
        ((_state.value.history as? UiState.Success)?.data).orEmpty().flatMap { it.points }
}

private class QuickCommandException(message: String) : Exception(message)

internal fun alertsUiState(alerts: List<AlertItem>): UiState<List<AlertItem>> =
    if (alerts.isEmpty()) UiState.Empty else UiState.Success(alerts)

internal fun alertEventsUiState(events: List<AlertEventItem>): UiState<List<AlertEventItem>> =
    if (events.isEmpty()) UiState.Empty else UiState.Success(events)

internal fun resolveEventDeviceName(deviceId: String, devices: List<DeviceSummary>): String =
    devices.firstOrNull { it.id == deviceId }?.name ?: "Unidad #$deviceId"

internal fun shouldPollAlerts(
    lastPollTimeMillis: Long?,
    nowMillis: Long,
    pollIntervalMillis: Long = 30_000L,
): Boolean = lastPollTimeMillis == null || nowMillis - lastPollTimeMillis >= pollIntervalMillis

internal fun resolveQuickCommandTemplate(
    requestedType: String,
    commands: List<CommandTemplate>,
): CommandTemplate? {
    val action = QuickCommandAction.from(requestedType) ?: return null
    return commands
        .map { command -> command to quickCommandScore(action, command) }
        .filter { (_, score) -> score > 0 }
        .maxByOrNull { (_, score) -> score }
        ?.first
}

internal fun buildQuickCommandMessage(command: CommandTemplate): String? {
    val values = linkedMapOf<String, String>()
    command.attributes.forEach { field ->
        val value = field.defaultValue?.takeIf { it.isNotBlank() }
            ?: field.options.firstOrNull()?.id?.takeIf { field.required }
        if (field.required && value.isNullOrBlank()) return null
        if (!value.isNullOrBlank()) values[field.name] = value
    }
    if (values.isEmpty()) return ""
    return values.entries.joinToString(prefix = "{", postfix = "}") { (key, value) ->
        "\"$key\":\"$value\""
    }
}

private enum class QuickCommandAction {
    EngineStop,
    EngineStart,
    Secure;

    companion object {
        fun from(type: String): QuickCommandAction? =
            when (type.trim().lowercase()) {
                "engine_stop" -> EngineStop
                "engine_start" -> EngineStart
                "secure_park" -> Secure
                else -> null
            }
    }
}

private fun quickCommandScore(
    action: QuickCommandAction,
    command: CommandTemplate,
): Int {
    val type = command.type.quickCommandKey()
    val title = command.title.quickCommandKey()
    val text = "$type $title"
    fun containsAny(vararg tokens: String): Boolean = tokens.any { token -> text.contains(token) }
    fun exactType(vararg tokens: String): Boolean = tokens.any { token -> type == token }

    return when (action) {
        QuickCommandAction.EngineStop -> when {
            exactType("engine_stop", "stop_engine", "engineoff", "cut_engine") -> 120
            containsAny("apagar motor", "detener motor", "cortar motor", "engine stop", "stop engine") -> 110
            containsAny("inmovilizar", "inmovilizacion", "inmovilizacion motor", "bloquear motor", "engine cut", "fuel cut") -> 100
            containsAny("apagado", "bloqueo", "inmov", "relay off", "kill engine") -> 90
            else -> 0
        }

        QuickCommandAction.EngineStart -> when {
            exactType("engine_start", "start_engine", "engineon", "restore_engine") -> 120
            containsAny("encender motor", "arrancar motor", "activar motor", "engine start", "start engine") -> 110
            containsAny("restaurar motor", "desbloquear motor", "reactivar motor", "resume engine") -> 100
            containsAny("encendido", "desbloqueo", "arranque", "relay on") -> 90
            else -> 0
        }

        QuickCommandAction.Secure -> when {
            exactType("secure_park", "parking", "secure_mode", "valet", "lock") -> 120
            containsAny("secure park", "modo seguro", "parqueo seguro", "modo valet", "park mode") -> 110
            containsAny("bloqueo seguro", "proteccion", "parking lock", "secure") -> 95
            else -> 0
        }
    }
}

private fun String.quickCommandKey(): String =
    lowercase()
        .replace('_', ' ')
        .replace('-', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()

private fun normalizeReportUrl(url: String): String {
    val trimmed = url.trim()
    if (trimmed.isBlank()) return ""
    if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
        return trimmed
    }
    return "${ApiEnvironment.baseUrl.trimEnd('/')}/${trimmed.trimStart('/')}"
}

private fun currentLocalDate(): String =
    Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .toString()

private fun currentLocalTime(): String {
    val time = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .time
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}

private data class ReportRangeDefaults(
    val fromDate: String,
    val fromTime: String,
    val toDate: String,
    val toTime: String,
)

private fun currentReportDefaults(): ReportRangeDefaults {
    val date = currentLocalDate()
    return ReportRangeDefaults(
        fromDate = date,
        fromTime = "00:00",
        toDate = date,
        toTime = currentLocalTime(),
    )
}

internal fun mapToMapaError(error: Throwable): MapaError =
    when (error) {
        is MapTimeoutException,
        is HttpRequestTimeoutException,
        -> MapaError.Timeout

        is MapOfflineException,
        is IOException,
        -> MapaError.Offline

        is SessionExpiredException,
        is MapUnauthorizedException,
        -> MapaError.Unauthorized

        is MapInvalidPayloadException,
        -> MapaError.InvalidPayload

        is ResponseException -> {
            if (error.response.status.value == 401) {
                MapaError.Unauthorized
            } else {
                MapaError.Unknown
            }
        }

        else -> MapaError.Unknown
    }

internal fun MapaError.toUserMessage(): String =
    when (this) {
        MapaError.Timeout -> "Tiempo de espera agotado. Reintentá en unos segundos."
        MapaError.Offline -> "Sin conectividad. Verificá tu red e intentá nuevamente."
        MapaError.Unauthorized -> "Tu sesion no es valida. Inicia sesion otra vez."
        MapaError.InvalidPayload -> "La respuesta del servidor es invalida. Reintentá."
        MapaError.Unknown -> "No pudimos cargar el mapa. Reintentá."
    }

private fun Throwable.toFriendlyMessage(
    defaultMessage: String,
    unauthorizedMessage: String = "Tu sesion no es valida. Inicia sesion otra vez.",
): String =
    when (this) {
        is MapTimeoutException,
        is HttpRequestTimeoutException,
        -> "Tiempo de espera agotado. Reintentá en unos segundos."

        is MapOfflineException,
        is IOException,
        -> "Sin conectividad. Verificá tu red e intentá nuevamente."

        is SessionExpiredException,
        is MapUnauthorizedException,
        -> unauthorizedMessage

        is MapInvalidPayloadException,
        -> "La respuesta del servidor es invalida. Reintentá."

        is ResponseException -> {
            if (this.response.status.value == 401) {
                unauthorizedMessage
            } else {
                defaultMessage
            }
        }

        else -> defaultMessage
    }

private fun MapCapability.platformName(): String =
    when (this) {
        is MapCapability.Available -> platform
        is MapCapability.Unavailable -> platform
    }
