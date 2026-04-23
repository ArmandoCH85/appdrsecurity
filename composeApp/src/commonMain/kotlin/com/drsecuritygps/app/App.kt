package com.drsecuritygps.app

import com.drsecuritygps.app.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoEncryption
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.drsecuritygps.app.core.UiState
import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.AlertNotificationChannel
import com.drsecuritygps.app.core.model.AlertNotifications
import com.drsecuritygps.app.core.model.AlertSeverity
import com.drsecuritygps.app.core.model.CommandConnection
import com.drsecuritygps.app.core.model.CommandField
import com.drsecuritygps.app.core.model.CommandTemplate
import com.drsecuritygps.app.core.model.DataSource
import com.drsecuritygps.app.core.model.deviceStatusLabel
import com.drsecuritygps.app.core.model.deviceSubtitle
import com.drsecuritygps.app.core.model.toCourseDegrees
import com.drsecuritygps.app.core.model.DeviceDetail
import com.drsecuritygps.app.core.model.DeviceFilter
import com.drsecuritygps.app.core.model.DeviceLivePosition
import com.drsecuritygps.app.core.model.DeviceSummary
import com.drsecuritygps.app.core.model.normalizeIgnitionDisplay
import com.drsecuritygps.app.core.model.HistoryPoint
import com.drsecuritygps.app.core.model.HistoryTrip
import com.drsecuritygps.app.core.model.GroupedDevices
import com.drsecuritygps.app.core.model.hasRenderableAlarm
import com.drsecuritygps.app.core.model.hasRenderableCoordinates
import com.drsecuritygps.app.core.model.isOnlineStatus
import com.drsecuritygps.app.core.model.MapCapability
import com.drsecuritygps.app.core.model.MapFeedState
import com.drsecuritygps.app.core.model.MapaError
import com.drsecuritygps.app.core.model.ReportCatalog
import com.drsecuritygps.app.core.model.ReportKind
import com.drsecuritygps.app.core.model.resolveFormat
import com.drsecuritygps.app.core.model.resolveType
import com.drsecuritygps.app.core.model.UserProfile
import com.drsecuritygps.app.network.ApiEnvironment
import com.drsecuritygps.app.presentation.AppController
import com.drsecuritygps.app.presentation.BrandLogo
import com.drsecuritygps.app.presentation.Destination
import com.drsecuritygps.app.presentation.DrFilterChip
import com.drsecuritygps.app.presentation.DrSecurityNavigationBar
import com.drsecuritygps.app.presentation.DrSecurityTopBar
import com.drsecuritygps.app.presentation.MaterialNavigationItem
import com.drsecuritygps.app.presentation.PlatformDeviceMap
import com.drsecuritygps.app.presentation.PlatformHistorySegmentMapPreview
import com.drsecuritygps.app.presentation.PlatformReportDateTimePickerField
import com.drsecuritygps.app.presentation.RememberSessionControl
import com.drsecuritygps.app.presentation.RootTab
import com.drsecuritygps.app.presentation.SessionToggleStyle
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun DrSecurityApp(
    controller: AppController,
    startupCrashReport: String? = null,
) {
    val state by controller.state.collectAsState()
    var visibleCrashReport by rememberSaveable(startupCrashReport) { mutableStateOf(startupCrashReport) }

    LaunchedEffect(Unit) { controller.bootstrap() }
    DisposableEffect(Unit) {
        onDispose { controller.stop() }
    }

    DrSecurityTheme {
        Surface(color = AppBg, modifier = Modifier.fillMaxSize()) {
            when {
                state.bootstrapLoading -> ScreenLoader("Cargando sesion...")
                state.session == null -> LoginScreen(
                    email = state.loginEmail,
                    password = state.loginPassword,
                    isLoading = state.isSubmittingLogin,
                    message = state.activeMessage,
                    onEmailChange = controller::updateLoginEmail,
                    onPasswordChange = controller::updateLoginPassword,
                    onSubmit = controller::login,
                )
                state.destination == Destination.DeviceDetail -> DeviceDetailScreen(
                    detail = controller.selectedDeviceDetail(),
                    commands = state.commands,
                    history = state.history,
                    reports = state.reports,
                    reportFromDate = state.reportFromDate,
                    reportFromTime = state.reportFromTime,
                    reportToDate = state.reportToDate,
                    reportToTime = state.reportToTime,
                    reportBusyAction = state.reportBusyAction,
                    reportFeedback = state.reportFeedback,
                    reportFeedbackIsError = state.reportFeedbackIsError,
                    onBack = controller::goBack,
                    onOpenHistory = controller::openHistory,
                    onRefreshReports = controller::refreshReports,
                    onReportFromDateTimeSelected = controller::updateReportFromDateTime,
                    onReportToDateTimeSelected = controller::updateReportToDateTime,
                    onGenerateVehicleHistoryReport = controller::generateVehicleHistoryReport,
                    onGenerateDrivesStopsReport = controller::generateDrivesStopsReport,
                    onOpenCommands = { controller.selectTab(RootTab.Commands) },
                    onGoDevices = { controller.selectTab(RootTab.Devices) },
                    onGoMap = { controller.selectTab(RootTab.Map) },
                    onGoAlerts = { controller.selectTab(RootTab.Alerts) },
                    onGoReports = { controller.selectTab(RootTab.Reports) },
                    onGoProfile = { controller.selectTab(RootTab.Profile) },
                )
                state.destination == Destination.History -> HistoryScreen(
                    selectedDevice = controller.selectedDeviceDetail(),
                    devices = (state.devices as? UiState.Success)?.data.orEmpty(),
                    history = state.history,
                    selectedDate = state.historyDate,
                    playbackPointIndex = state.historyPlaybackPointIndex,
                    isPlaying = state.historyIsPlaying,
                    resolvedAddresses = state.historyResolvedAddresses,
                    resolvingAddressKeys = state.historyResolvingKeys,
                    onDeviceSelected = controller::selectDeviceForHistory,
                    onBack = controller::goBack,
                    onPreviousDate = { controller.shiftHistoryDate(-1) },
                    onNextDate = { controller.shiftHistoryDate(1) },
                    onSelectDate = controller::selectHistoryDate,
                    onTogglePlayback = controller::toggleHistoryPlayback,
                    onSeekPlayback = controller::seekHistoryPlayback,
                    onResolveAddress = controller::resolveHistoryAddress,
                    onGoDevices = { controller.selectTab(RootTab.Devices) },
                    onGoMap = { controller.selectTab(RootTab.Map) },
                    onGoAlerts = { controller.selectTab(RootTab.Alerts) },
                    onGoReports = { controller.selectTab(RootTab.Reports) },
                    onGoProfile = { controller.selectTab(RootTab.Profile) },
                )
                state.destination == Destination.ReportView -> ReportViewScreen(
                    selectedDeviceId = state.selectedDeviceId,
                    devices = (state.devices as? UiState.Success)?.data.orEmpty(),
                    reports = state.reports,
                    reportFromDate = state.reportFromDate,
                    reportFromTime = state.reportFromTime,
                    reportToDate = state.reportToDate,
                    reportToTime = state.reportToTime,
                    reportBusyAction = state.reportBusyAction,
                    reportFeedback = state.reportFeedback,
                    reportFeedbackIsError = state.reportFeedbackIsError,
                    onDeviceSelected = controller::selectDeviceAndGenerateReport,
                    onGenerateVehicleHistoryReport = controller::generateVehicleHistoryReport,
                    onGenerateDrivesStopsReport = controller::generateDrivesStopsReport,
                    onBack = controller::goBack,
                    onRefreshReports = controller::refreshReports,
                    onReportFromDateTimeSelected = controller::updateReportFromDateTime,
                    onReportToDateTimeSelected = controller::updateReportToDateTime,
                    onGoDevices = { controller.selectTab(RootTab.Devices) },
                    onGoMap = { controller.selectTab(RootTab.Map) },
                    onGoAlerts = { controller.selectTab(RootTab.Alerts) },
                    onGoReports = { controller.selectTab(RootTab.Reports) },
                    onGoProfile = { controller.selectTab(RootTab.Profile) },
                )
                state.destination == Destination.AlertsMenu -> AlertsMenuScreen(
                    events = state.alertEvents,
                    onBack = controller::goBack,
                    onRefresh = controller::refreshAlertEvents,
                    onGoDevices = { controller.selectTab(RootTab.Devices) },
                    onGoMap = { controller.selectTab(RootTab.Map) },
                    onGoAlerts = { controller.selectTab(RootTab.Alerts) },
                    onGoReports = { controller.selectTab(RootTab.Reports) },
                    onGoProfile = { controller.selectTab(RootTab.Profile) },
                )
                else -> TabsScreen(controller)
            }

            if (state.session != null && state.activeMessage != null) {
                AlertDialog(
                    onDismissRequest = controller::dismissMessage,
                    confirmButton = {
                        TextButton(onClick = controller::dismissMessage) {
                            Text("Cerrar", color = AppPrimary)
                        }
                    },
                    title = { Text("Mensaje", color = AppText) },
                    text = {
                        Text(
                            text = state.activeMessage.orEmpty(),
                            color = AppText,
                        )
                    },
                )
            }

            visibleCrashReport?.let { crashReport ->
                AlertDialog(
                    onDismissRequest = { visibleCrashReport = null },
                    confirmButton = {
                        TextButton(onClick = { visibleCrashReport = null }) {
                            Text("Cerrar", color = AppPrimary)
                        }
                    },
                    title = { Text("Último crash detectado", color = AppText) },
                    text = {
                        Text(
                            text = crashReport.take(1800),
                            color = AppText,
                            fontSize = 11.sp,
                        )
                    },
                    containerColor = AppCard,
                )
            }
        }
    }
}

@Composable
private fun TabsScreen(controller: AppController) {
    val state by controller.state.collectAsState()
when (state.currentTab) {
        RootTab.Devices -> DevicesScreen(
            devices = state.devices,
            devicesList = controller.visibleDevices(),
            groupedDevices = controller.groupedDevices(),
            selectedDeviceDetail = controller.selectedDeviceDetail(),
            search = state.searchQuery,
            filter = state.deviceFilter,
            onSearchChange = controller::updateSearch,
            onFilterChange = controller::updateFilter,
            onDeviceSelected = controller::openDetail,
            onClearDeviceSelection = controller::clearDeviceSelection,
            commands = state.commands,
            isSendingCommand = state.isSendingCommand,
            onSendCommand = controller::sendCommand,
            onGoMap = { controller.selectTab(RootTab.Map) },
            onGoAlerts = { controller.selectTab(RootTab.Alerts) },
            onGoReports = { controller.selectTab(RootTab.Reports) },
            onGoProfile = { controller.selectTab(RootTab.Profile) },
        )
        RootTab.Map -> LiveMapScreen(
            devices = (state.devices as? UiState.Success)?.data.orEmpty(),
            mapLivePositions = controller.livePositions(),
            selectedDeviceId = state.selectedDeviceId,
            selectedDeviceDetail = controller.selectedDeviceDetail(),
            commands = state.commands,
            isSendingCommand = state.isSendingCommand,
            mapFeed = state.mapFeed,
            mapCapability = state.mapCapability,
            search = state.searchQuery,
            onDeviceSelected = controller::openDetail,
            onSearchChange = controller::updateSearch,
            onRetry = { controller.refreshCurrentTab() },
            onGoDevices = { controller.selectTab(RootTab.Devices) },
            onGoAlerts = { controller.selectTab(RootTab.Alerts) },
            onGoReports = { controller.selectTab(RootTab.Reports) },
            onGoProfile = { controller.selectTab(RootTab.Profile) },
            showDeviceBottomSheet = state.showDeviceBottomSheet,
            onDismissBottomSheet = controller::dismissDeviceBottomSheet,
            onSendCommand = controller::sendCommand,
            vehicleMapIconUrl = controller::vehicleMapIconUrl,
        )
        RootTab.Alerts -> AlertsMenuScreen(
            events = state.alertEvents,
            onBack = { controller.selectTab(RootTab.Map) },
            onRefresh = controller::refreshAlertEvents,
            onGoDevices = { controller.selectTab(RootTab.Devices) },
            onGoMap = { controller.selectTab(RootTab.Map) },
            onGoAlerts = {},
            onGoReports = { controller.selectTab(RootTab.Reports) },
            onGoProfile = { controller.selectTab(RootTab.Profile) },
        )
        RootTab.Commands -> CommandsScreen(
            selectedDevice = controller.selectedDeviceDetail(),
            commands = state.commands,
            isSendingCommand = state.isSendingCommand,
            onSendCommand = controller::sendCommand,
            onGoDevices = { controller.selectTab(RootTab.Devices) },
            onGoMap = { controller.selectTab(RootTab.Map) },
            onGoAlerts = { controller.selectTab(RootTab.Alerts) },
            onGoReports = { controller.selectTab(RootTab.Reports) },
            onGoProfile = { controller.selectTab(RootTab.Profile) },
        )
        RootTab.Reports -> ReportsScreen(
            devices = state.devices,
            reports = state.reports,
            alerts = state.alerts,
            onRefreshReports = controller::refreshReports,
            onGoDevices = { controller.selectTab(RootTab.Devices) },
            onGoMap = { controller.selectTab(RootTab.Map) },
            onGoAlerts = { controller.selectTab(RootTab.Alerts) },
            onGoProfile = { controller.selectTab(RootTab.Profile) },
            onNavigateToHistory = { controller.openHistory() },
            onNavigateToReport = { controller.openReportView() },
        )
        RootTab.Profile -> ProfileScreen(
            profile = state.profile,
            onLogout = controller::logout,
            onGoDevices = { controller.selectTab(RootTab.Devices) },
            onGoMap = { controller.selectTab(RootTab.Map) },
            onGoAlerts = { controller.selectTab(RootTab.Alerts) },
            onGoReports = { controller.selectTab(RootTab.Reports) },
        )
    }
}

@Composable
private fun LoginScreen(
    email: String,
    password: String,
    isLoading: Boolean,
    message: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(AppPrimaryLight, AppBg, AppBg)),
            ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .navigationBarsPadding(),
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
            Spacer(Modifier.height(6.dp))
            BrandLogo(modifier = Modifier.fillMaxWidth(0.34f).height(68.dp))
            Spacer(Modifier.height(10.dp))
            Text(AppBrand.displayName, color = AppText, fontWeight = FontWeight.Bold, fontSize = 27.sp)
            Text("Monitoreo satelital avanzado", color = AppPrimary.copy(alpha = 0.72f), fontSize = 12.sp)
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 460.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.20f)),
            ) {
                Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    InputBlock("Correo electrónico", email, "nombre@empresa.com", Icons.Default.Mail, false, onEmailChange)
                    InputBlock("Contraseña", password, "••••••••", Icons.Default.Lock, true, onPasswordChange)
                    RememberSessionControl(
                        checked = true,
                        onCheckedChange = {},
                        enabled = false,
                        label = "Recordar sesión",
                        supportingText = null,
                        style = SessionToggleStyle.Checkbox,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = onSubmit,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(if (isLoading) "Ingresando..." else "Iniciar sesión", fontWeight = FontWeight.Bold)
                    }
                    message?.let {
                        Text(it, color = AppDanger, fontSize = 12.sp)
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun InputBlock(
    label: String,
    value: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    password: Boolean,
    onChange: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val useHiddenPassword = password && !passwordVisible
    val transformation: VisualTransformation =
        if (useHiddenPassword) PasswordVisualTransformation() else VisualTransformation.None
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = AppText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = AppMuted.copy(alpha = 0.7f)) },
            leadingIcon = { Icon(icon, null, tint = AppMuted) },
            visualTransformation = transformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (password) KeyboardType.Password else KeyboardType.Email,
            ),
            trailingIcon = {
                if (password) {
                    val desc = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = desc,
                            tint = AppMuted,
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
        )
    }
}

@Composable
private fun DevicesScreen(
    devices: UiState<List<DeviceSummary>>,
    devicesList: List<DeviceSummary>,
    groupedDevices: GroupedDevices,
    selectedDeviceDetail: DeviceDetail?,
    commands: UiState<List<CommandTemplate>>,
    isSendingCommand: Boolean,
    search: String,
    filter: DeviceFilter,
    onSearchChange: (String) -> Unit,
    onFilterChange: (DeviceFilter) -> Unit,
    onDeviceSelected: (String) -> Unit,
    onClearDeviceSelection: () -> Unit,
    onSendCommand: (CommandTemplate, String) -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    var locallySelectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val detail = if (locallySelectedId != null) selectedDeviceDetail else null
    if (detail != null) {
        DeviceDetailView(
            detail = detail,
            commands = commands,
            isSendingCommand = isSendingCommand,
            onSendCommand = onSendCommand,
            onBack = { locallySelectedId = null; onClearDeviceSelection() },
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
        return
    }
    val totalDevices = groupedDevices.values.sumOf { it.size }
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = "Seleccionar dispositivo",
            left = { IconButton(onClick = onGoMap) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
            right = { Icon(Icons.Default.DirectionsCar, null, tint = AppPrimary, modifier = Modifier.size(20.dp)) },
        )
        SearchBar(search, onSearchChange, placeholder = "Buscar unidad...")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterPill("Todos", filter == DeviceFilter.All) { onFilterChange(DeviceFilter.All) }
            FilterPill("En línea", filter == DeviceFilter.Online) { onFilterChange(DeviceFilter.Online) }
            FilterPill("Fuera de línea", filter == DeviceFilter.Offline) { onFilterChange(DeviceFilter.Offline) }
            FilterPill("Alarmas", filter == DeviceFilter.Critical) { onFilterChange(DeviceFilter.Critical) }
        }
        Spacer(Modifier.height(14.dp))
        when (devices) {
            UiState.Loading -> ScreenLoader("Cargando unidades...")
            is UiState.Error -> ScreenError(devices.message)
            UiState.Empty -> ScreenEmpty("Sin unidades disponibles")
            is UiState.Success -> {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("DISPOSITIVOS", color = AppPrimary.copy(alpha = 0.75f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$totalDevices unidades", color = AppMuted, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    groupedDevices.forEach { (groupName, groupDevices) ->
                        val devicesList = groupDevices
                        item(key = "group_$groupName") {
                            CollapsibleGroupCard(
                                groupName = groupName,
                                deviceCount = devicesList.size,
                                initiallyExpanded = groupedDevices.keys.firstOrNull() == groupName,
                                onDeviceClick = { deviceId ->
                                    locallySelectedId = deviceId
                                    onDeviceSelected(deviceId)
                                },
                                devices = devicesList,
                            )
                        }
                    }
                }
            }
        }
        AppBottomBar(
            selectedTab = RootTab.Devices,
            onGoDevices = {},
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
}

/** Espacio bajo el sheet para no solapar el `NavigationBar` + insets del sistema + margen. */
@Composable
private fun deviceQuickSheetBottomPadding(extraGap: Dp = 18.dp): Dp {
    val systemInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val materialNavBarHeight = 80.dp
    return materialNavBarHeight + systemInset + extraGap
}

@Composable
private fun DeviceDetailView(
    detail: DeviceDetail,
    commands: UiState<List<CommandTemplate>>,
    isSendingCommand: Boolean,
    onSendCommand: (CommandTemplate, String) -> Unit,
    onBack: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    val summary = detail.summary
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(AppBg)) {
        if (summary.hasRenderableCoordinates()) {
            PlatformDeviceMap(
                devices = listOf(
                    DeviceLivePosition(
                        id = summary.id,
                        title = summary.name,
                        latitude = summary.latitude,
                        longitude = summary.longitude,
                        status = deviceStatusLabel(summary.onlineStatus),
                        courseDegrees = summary.course.toCourseDegrees(),
                    ),
                ),
                selectedDeviceId = summary.id,
                showLabels = false,
                showControls = true,
                controlsBottomExtraPadding = 80.dp,
                onDeviceSelected = {},
                modifier = Modifier.fillMaxSize(),
            )
} else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp),
                ) {
                    Icon(Icons.Default.Warning, null, tint = AppMuted, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Sin coordenadas válidas", color = AppText, fontWeight = FontWeight.Bold)
                    Text("Esta unidad no reporta ubicación.", color = AppMuted, fontSize = 12.sp)
                }
            }
        }
        DeviceQuickSheet(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = deviceQuickSheetBottomPadding()),
            device = detail,
            commands = commands,
            isSendingCommand = isSendingCommand,
            onSendCommand = onSendCommand,
            onDismiss = onBack,
        )
        AppBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedTab = RootTab.Devices,
            onGoDevices = onBack,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
}

@Composable
private fun CollapsibleGroupCard(
    groupName: String,
    deviceCount: Int,
    initiallyExpanded: Boolean,
    onDeviceClick: (String) -> Unit,
    devices: List<DeviceSummary>,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppCard),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AppPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                    Column {
                        Text(
                            text = groupName.uppercase(),
                            color = AppText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "$deviceCount unidades",
                            color = AppMuted,
                            fontSize = 11.sp,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = AppPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp),
                )
            }
            if (expanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    devices.forEach { device ->
                        DeviceListCard(device, onClick = { onDeviceClick(device.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeviceListSensorBand(device: DeviceSummary) {
    val encendido = normalizeIgnitionDisplay(device.ignition)
        ?: normalizeIgnitionDisplay(device.sensorEngineDisplay)
    val dist = device.listDistanceText
        ?: device.sensorExtraRows.firstOrNull { it.label.equals("Distancia", ignoreCase = true) }?.value
    val vel = device.listSpeedText
        ?: device.sensorExtraRows.firstOrNull { it.label.equals("Velocidad", ignoreCase = true) }?.value
    val bat = device.sensorBatteryDisplay ?: device.batteryLevel?.takeIf { it.isNotBlank() }
    val sat = device.sensorSatellitesDisplay

    val filteredExtras = device.sensorExtraRows.filter { row -> !isCoreMetricRowDuplicate(row.label) }

    val hasCore = !encendido.isNullOrBlank() || !dist.isNullOrBlank() || !vel.isNullOrBlank() ||
        !bat.isNullOrBlank() || !sat.isNullOrBlank()
    if (!hasCore && filteredExtras.isEmpty()) return

    Spacer(Modifier.height(2.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        encendido?.let {
            CompactSensorChip(
                icon = Icons.Default.Key,
                text = it,
                contentDescription = "Encendido: $it",
                accent = true,
            )
        }
        dist?.let {
            CompactSensorChip(
                icon = Icons.Default.Route,
                text = it,
                contentDescription = "Distancia: $it",
                accent = true,
            )
        }
        vel?.let {
            CompactSensorChip(
                icon = Icons.Default.Speed,
                text = it,
                contentDescription = "Velocidad: $it",
                accent = true,
            )
        }
        bat?.let {
            CompactSensorChip(
                icon = Icons.Default.BatteryFull,
                text = it,
                contentDescription = "Batería: $it",
                accent = false,
            )
        }
        sat?.let {
            CompactSensorChip(
                icon = Icons.Filled.SatelliteAlt,
                text = it,
                contentDescription = "Satélites: $it",
                accent = false,
            )
        }
        filteredExtras.take(32).forEach { row ->
            val shortLabel = if (row.label.length > 18) row.label.take(16) + "…" else row.label
            CompactSensorChip(
                icon = Icons.Default.Tune,
                text = "$shortLabel · ${row.value}",
                contentDescription = "${row.label}: ${row.value}",
                accent = false,
                microText = true,
            )
        }
    }
}

private fun isCoreMetricRowDuplicate(label: String): Boolean {
    val k = label.lowercase()
    return k.contains("encendido") || k == "ignition" ||
        k.contains("distancia") || k.contains("velocidad")
}

@Composable
private fun CompactSensorChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    contentDescription: String,
    accent: Boolean,
    microText: Boolean = false,
) {
    val cd = contentDescription
    Surface(
        modifier = Modifier.semantics { this.contentDescription = cd },
        shape = RoundedCornerShape(10.dp),
        color = if (accent) AppPrimary.copy(alpha = 0.10f) else AppSurfaceSoft,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (accent) AppPrimary else AppMuted,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = text,
                color = AppText,
                fontSize = if (microText) 8.5.sp else 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                lineHeight = 11.sp,
            )
        }
    }
}

@Composable
private fun DeviceListOnlineStatusIndicator(onlineStatus: String) {
    val online = isOnlineStatus(onlineStatus)
    val offline = onlineStatus.contains("offline", ignoreCase = true)
    val desc = deviceStatusLabel(onlineStatus)
    val tint = when {
        online -> AppSuccess
        offline -> AppMuted
        else -> AppMuted.copy(alpha = 0.72f)
    }
    val icon = when {
        online -> Icons.Filled.CloudDone
        offline -> Icons.Filled.CloudOff
        else -> Icons.Filled.HelpOutline
    }
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = tint,
            modifier = Modifier.size(13.dp),
        )
    }
}

@Composable
private fun DeviceListCard(device: DeviceSummary, onClick: () -> Unit) {
    val hasAlarm = hasRenderableAlarm(device.alarm)
    val subtitle = deviceSubtitle(device.address, device.lastUpdate)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppCard),
        border = androidx.compose.foundation.BorderStroke(
            if (hasAlarm) 1.5.dp else 1.dp,
            if (hasAlarm) AppWarning.copy(alpha = 0.70f) else AppPrimary.copy(alpha = 0.20f),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = device.name,
                        color = AppText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        lineHeight = 17.sp,
                        modifier = Modifier.weight(1f).padding(end = 6.dp),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DeviceListOnlineStatusIndicator(device.onlineStatus)
                        if (hasAlarm) {
                            StatusPill("ALARMA", AppWarning)
                        }
                    }
                }
                subtitle?.let {
                    Text(it, color = AppMuted, fontSize = 11.sp, maxLines = 1)
                }
                DeviceListSensorBand(device)
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AppMuted,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(22.dp),
            )
        }
    }
}

@Composable
private fun LiveMapScreen(
    devices: List<DeviceSummary>,
    mapLivePositions: List<DeviceLivePosition>,
    selectedDeviceId: String?,
    selectedDeviceDetail: DeviceDetail?,
    commands: UiState<List<CommandTemplate>>,
    isSendingCommand: Boolean = false,
    mapFeed: MapFeedState,
    mapCapability: MapCapability,
    search: String,
    onDeviceSelected: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onRetry: () -> Unit,
    onGoDevices: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
    showDeviceBottomSheet: Boolean = false,
    onDismissBottomSheet: () -> Unit = {},
    onSendCommand: (CommandTemplate, String) -> Unit,
    vehicleMapIconUrl: (DeviceSummary) -> String? = { null },
) {
    val mapDevices = mapLivePositions
    val selectedDevice = devices.firstOrNull { it.id == selectedDeviceId }
    val showDetail = showDeviceBottomSheet && selectedDeviceDetail != null
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(AppBg)) {
        if (showDetail && selectedDeviceDetail != null) {
            val summary = selectedDeviceDetail.summary
            if (summary.hasRenderableCoordinates()) {
                PlatformDeviceMap(
                    devices = listOf(
                        DeviceLivePosition(
                            id = summary.id,
                            title = summary.name,
                            latitude = summary.latitude,
                            longitude = summary.longitude,
                            status = deviceStatusLabel(summary.onlineStatus),
                            iconUrl = vehicleMapIconUrl(summary),
                            iconColor = summary.iconColor,
                            courseDegrees = summary.course.toCourseDegrees(),
                        ),
                    ),
                    selectedDeviceId = summary.id,
                    showLabels = false,
                    showControls = true,
                    controlsBottomExtraPadding = 80.dp,
                    onDeviceSelected = {},
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                    ) {
                        Icon(Icons.Default.Warning, null, tint = AppMuted, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Sin coordenadas válidas", color = AppText, fontWeight = FontWeight.Bold)
                        Text("Esta unidad no reporta ubicación.", color = AppMuted, fontSize = 12.sp)
                    }
                }
            }
            DeviceQuickSheet(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = deviceQuickSheetBottomPadding()),
                device = selectedDeviceDetail,
                commands = commands,
                isSendingCommand = isSendingCommand,
                onSendCommand = onSendCommand,
                onDismiss = onDismissBottomSheet,
            )
        } else {
            PlatformDeviceMap(
                devices = mapDevices,
                selectedDeviceId = selectedDeviceId,
                showControls = true,
                controlsBottomExtraPadding = 80.dp,
                capability = mapCapability,
                onDeviceSelected = onDeviceSelected,
                modifier = Modifier.fillMaxSize(),
            )
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AppCard.copy(alpha = 0.92f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.2f)),
            ) {
                SearchBar(
                    value = search,
                    onChange = onSearchChange,
                    placeholder = "Buscar unidad...",
                )
            }
        }
        AppBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedTab = RootTab.Map,
            onGoDevices = onGoDevices,
            onGoMap = {},
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
}

@Composable
private fun AlertsMenuScreen(
    events: UiState<List<AlertEventItem>>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = "Alertas",
            subtitle = "Eventos recientes de tus equipos",
            left = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
            right = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, null, tint = AppPrimary)
                    }
                    Icon(Icons.Default.NotificationsActive, null, tint = AppPrimary, modifier = Modifier.size(20.dp))
                }
            },
        )
        when (events) {
            UiState.Loading -> Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ScreenLoader("Cargando alertas...")
            }
            is UiState.Error -> Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ScreenError(events.message)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onRefresh) { Text("Reintentar") }
            }
            UiState.Empty -> Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ScreenEmpty("No hay eventos de alerta para mostrar.")
            }
            is UiState.Success -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(events.data, key = { it.id }) { event ->
                    AlertEventCard(event)
                }
            }
        }
        AppBottomBar(
            selectedTab = RootTab.Alerts,
            onGoDevices = onGoDevices,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
}

@Composable
private fun AlertEventCard(event: AlertEventItem) {
    val accent = when (event.severity) {
        AlertSeverity.Critical -> AppDanger
        AlertSeverity.Warning -> AppWarning
        AlertSeverity.Info -> AppPrimary
    }
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = AppCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.20f)),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(event.deviceName.ifBlank { "Unidad #${event.deviceId}" }, color = AppText, fontWeight = FontWeight.Bold)
                StatusPill(
                    text = when (event.severity) {
                        AlertSeverity.Critical -> "Crítica"
                        AlertSeverity.Warning -> "Advertencia"
                        AlertSeverity.Info -> "Info"
                    },
                    color = accent,
                )
            }
            Text(event.message, color = AppText, style = MaterialTheme.typography.bodyMedium)
            if (event.address.isNotBlank()) {
                Text(event.address, color = AppMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(event.timestamp.ifBlank { "--" }, color = AppMuted, fontSize = 11.sp)
                event.speed?.let { speed ->
                    Text("${speed.roundToInt()} km/h", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun MapVehicleSheet(
    modifier: Modifier = Modifier,
    compactLayout: Boolean,
    devices: List<DeviceSummary>,
    selectedDevice: DeviceSummary?,
    selectedDeviceId: String?,
    search: String,
    onlineCount: Int,
    mapFeed: MapFeedState,
    onRetry: () -> Unit,
    onGoDevices: () -> Unit,
    onDeviceSelected: (String) -> Unit,
    collapsedHeight: androidx.compose.ui.unit.Dp,
    expandedHeight: androidx.compose.ui.unit.Dp,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var dragDelta by remember { mutableStateOf(0f) }
    val animatedHeight by animateDpAsState(if (expanded) expandedHeight else collapsedHeight)
    val dragState = rememberDraggableState { delta -> dragDelta += delta }

    ElevatedCard(
        modifier = modifier
            .height(animatedHeight)
            .animateContentSize()
            .draggable(
                orientation = Orientation.Vertical,
                state = dragState,
                onDragStopped = { velocity ->
                    when {
                        dragDelta < -48f || velocity < -900f -> expanded = true
                        dragDelta > 48f || velocity > 900f -> expanded = false
                    }
                    dragDelta = 0f
                },
            ),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 28.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = AppCard.copy(alpha = 0.96f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            MapVehicleSheetHeader(
                compactLayout = compactLayout,
                selectedDevice = selectedDevice,
                search = search,
                deviceCount = devices.size,
                onlineCount = onlineCount,
                expanded = expanded,
                onToggle = { expanded = !expanded },
            )
            if (expanded) {
                HorizontalDivider(color = AppDivider)
                if (devices.isEmpty()) {
                    EmptyMapListState(
                        search = search,
                        mapFeed = mapFeed,
                        onRetry = onRetry,
                        onGoDevices = onGoDevices,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp),
                    ) {
                        items(devices, key = { it.id }) { device ->
                            MapUnitCard(
                                device = device,
                                selected = device.id == selectedDeviceId,
                                onClick = {
                                    onDeviceSelected(device.id)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapVehicleSheetHeader(
    compactLayout: Boolean,
    selectedDevice: DeviceSummary?,
    search: String,
    deviceCount: Int,
    onlineCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Surface(
        onClick = onToggle,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (expanded) "Vehículos" else "Vehículos en mapa",
                            color = AppText,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (compactLayout) 16.sp else 18.sp,
                        )
                        Badge(containerColor = AppPrimary.copy(alpha = 0.28f), contentColor = AppPrimary) {
                            Text("$deviceCount")
                        }
                        StatusPill("$onlineCount online", AppSuccess)
                    }
                    Text(
                        text = when {
                            selectedDevice != null -> buildString {
                                append(selectedDevice.name)
                                val subtitle = selectedDevice.address.ifBlank { deviceStatusLabel(selectedDevice.onlineStatus) }
                                if (subtitle.isNotBlank()) {
                                    append(" · ")
                                    append(subtitle)
                                }
                            }
                            search.isNotBlank() -> "$deviceCount coincidencias para \"$search\""
                            expanded -> "Toca una unidad para centrarla y volver al mapa"
                            else -> "Desliza hacia arriba para ver la lista completa"
                        },
                        color = AppMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                FilledTonalIconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = if (expanded) "Ocultar vehículos" else "Ver vehículos",
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceQuickSheet(
    modifier: Modifier = Modifier,
    device: DeviceDetail?,
    commands: UiState<List<CommandTemplate>>,
    isSendingCommand: Boolean,
    onSendCommand: (CommandTemplate, String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (device == null) {
        return
    }
    var pendingCommand by remember(device.summary.id) { mutableStateOf<CommandTemplate?>(null) }
    val quickEnabled = !isSendingCommand
    val summary = device.summary
    val hasAlarm = hasRenderableAlarm(summary.alarm)
    val subtitle = deviceSubtitle(summary.address, summary.lastUpdate)

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = AppCard.copy(alpha = 0.96f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = summary.name,
                            color = AppText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 2,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f).padding(end = 6.dp),
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DeviceListOnlineStatusIndicator(summary.onlineStatus)
                            if (hasAlarm) {
                                StatusPill("ALARMA", AppWarning)
                            }
                        }
                    }
                    subtitle?.let {
                        Text(it, color = AppMuted, fontSize = 11.sp, maxLines = 1)
                    }
                    DeviceListSensorBand(summary)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Cerrar", tint = AppMuted)
                }
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = AppDivider)
            Spacer(Modifier.height(8.dp))
            Text(
                "Comandos para este equipo",
                color = AppPrimary.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
            )
            Spacer(Modifier.height(6.dp))
            when (commands) {
                UiState.Loading ->
                    Text("Cargando comandos…", color = AppMuted, fontSize = 12.sp)

                is UiState.Error ->
                    Text(commands.message, color = AppDanger, fontSize = 12.sp)

                UiState.Empty ->
                    Text(
                        "No hay comandos disponibles para este GPS.",
                        color = AppMuted,
                        fontSize = 12.sp,
                    )

                is UiState.Success ->
                    if (commands.data.isEmpty()) {
                        Text(
                            "No hay comandos disponibles para este GPS.",
                            color = AppMuted,
                            fontSize = 12.sp,
                        )
                    } else {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            items(commands.data, key = { it.type }) { cmd ->
                                CommandChip(
                                    label = cmd.title,
                                    icon = commandIcon(cmd),
                                    onClick = { pendingCommand = cmd },
                                    enabled = quickEnabled,
                                    modifier = Modifier.widthIn(max = 148.dp),
                                )
                            }
                        }
                    }
            }
        }
    }
    pendingCommand?.let { cmd ->
        CommandTemplateDialog(
            command = cmd,
            isSending = isSendingCommand,
            onSend = onSendCommand,
            onDismiss = { pendingCommand = null },
        )
    }
}

@Composable
private fun CommandChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val contentAlpha = if (enabled) 1f else 0.45f
    Surface(
        modifier = modifier
            .height(36.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = AppSurfaceSoft,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, label, tint = AppPrimary.copy(alpha = contentAlpha), modifier = Modifier.size(14.dp))
            Text(
                label,
                color = AppText.copy(alpha = contentAlpha),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MapFeedBanner(
    mapFeed: MapFeedState,
    mapCapability: MapCapability,
    compact: Boolean,
    onRetry: () -> Unit,
) {
    val capabilityMessage = (mapCapability as? MapCapability.Unavailable)?.reason
    val state = when (mapFeed) {
        MapFeedState.Loading -> BannerState(
            message = "Actualizando posiciones en tiempo real...",
            tone = AppPrimary,
            icon = Icons.Default.Refresh,
            details = "Sincronizando feed de mapa",
        )

        is MapFeedState.Ready -> BannerState(
            message = if (mapFeed.stale) "Datos en cache, pueden estar desactualizados" else "Mapa sincronizado",
            tone = if (mapFeed.stale) AppWarning else AppSuccess,
            icon = if (mapFeed.stale) Icons.Default.Warning else Icons.Default.CheckCircle,
            details = "Fuente: ${if (mapFeed.source == DataSource.Cache) "cache" else "red"}",
            actionLabel = if (mapFeed.stale) "Reintentar" else null,
        )

        is MapFeedState.Empty -> BannerState(
            message = "No hay unidades con coordenadas para mostrar",
            tone = AppMuted,
            icon = Icons.Default.Info,
            details = "Fuente: ${if (mapFeed.source == DataSource.Cache) "cache" else "red"}",
            actionLabel = "Reintentar",
        )

        is MapFeedState.Degraded -> BannerState(
            message = mapFeed.error.toBannerMessage(),
            tone = AppWarning,
            icon = Icons.Default.Report,
            details = "Mostrando ultima informacion disponible",
            actionLabel = "Reintentar",
        )

        is MapFeedState.Error -> BannerState(
            message = mapFeed.error.toBannerMessage(),
            tone = AppDanger,
            icon = Icons.Default.Warning,
            details = "No pudimos sincronizar el mapa",
            actionLabel = "Reintentar",
        )
    }

    val effectiveMessage = capabilityMessage ?: state.message
    val containerColor = when {
        capabilityMessage != null -> AppCard.copy(alpha = 0.90f)
        state.tone == AppDanger -> AppDangerLight.copy(alpha = 0.93f)
        state.tone == AppWarning -> AppWarningLight.copy(alpha = 0.93f)
        state.tone == AppSuccess -> AppSuccessLight.copy(alpha = 0.91f)
        else -> AppPrimaryLight.copy(alpha = 0.90f)
    }

    Card(
        shape = RoundedCornerShape(if (compact) 13.dp else 14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, state.tone.copy(alpha = 0.24f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = if (compact) 9.dp else 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 20.dp else 22.dp)
                        .clip(CircleShape)
                        .background(state.tone.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        state.icon,
                        null,
                        tint = state.tone,
                        modifier = Modifier.size(if (compact) 12.dp else 13.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = effectiveMessage,
                    color = state.tone,
                    fontSize = if (compact) 10.sp else 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                    state.details?.let {
                        Text(
                            it,
                            color = AppText.copy(alpha = 0.7f),
                            fontSize = if (compact) 9.sp else 10.sp,
                        )
                    }
                }
            }
            state.actionLabel?.let {
                TextButton(onClick = onRetry) {
                    Text(it, color = AppPrimary, fontSize = if (compact) 10.sp else 11.sp)
                }
            }
        }
    }
}

@Composable
private fun EmptyMapListState(
    search: String,
    mapFeed: MapFeedState,
    onRetry: () -> Unit,
    onGoDevices: () -> Unit,
) {
    val state = mapEmptyVisualState(search = search, mapFeed = mapFeed)
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(state.tone.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(state.icon, null, tint = state.tone)
        }
        Text(
            text = state.title,
            color = AppText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = state.description,
            color = AppMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onGoDevices, modifier = Modifier.weight(1f)) {
                Text("Ir a unidades", color = AppText)
            }
            Button(onClick = onRetry, modifier = Modifier.weight(1f)) {
                Text(state.retryLabel)
            }
        }
    }
}

private fun MapaError.toBannerMessage(): String =
    when (this) {
        MapaError.Timeout -> "El mapa tarda mas de lo esperado"
        MapaError.Offline -> "Sin conectividad para actualizar posiciones"
        MapaError.Unauthorized -> "Sesion invalida para el feed de mapa"
        MapaError.InvalidPayload -> "La respuesta del mapa llego con datos invalidos"
        MapaError.Unknown -> "No pudimos actualizar el mapa"
    }

private data class BannerState(
    val message: String,
    val tone: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val details: String? = null,
    val actionLabel: String? = null,
)

private data class MapEmptyVisualState(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tone: Color,
    val retryLabel: String,
)

private fun mapEmptyVisualState(search: String, mapFeed: MapFeedState): MapEmptyVisualState =
    when {
        search.isNotBlank() -> MapEmptyVisualState(
            title = "Sin resultados para \"$search\"",
            description = "Ajusta el texto de busqueda o limpia el filtro para volver al listado completo.",
            icon = Icons.Default.Search,
            tone = AppPrimary,
            retryLabel = "Actualizar",
        )

        mapFeed is MapFeedState.Loading -> MapEmptyVisualState(
            title = "Sincronizando unidades",
            description = "Estamos cargando posiciones actuales para poblar el mapa.",
            icon = Icons.Default.Refresh,
            tone = AppPrimary,
            retryLabel = "Actualizar",
        )

        mapFeed is MapFeedState.Error -> MapEmptyVisualState(
            title = "No pudimos cargar el mapa",
            description = "Verifica conectividad o reintenta para recuperar las unidades activas.",
            icon = Icons.Default.Warning,
            tone = AppDanger,
            retryLabel = "Reintentar",
        )

        mapFeed is MapFeedState.Degraded -> MapEmptyVisualState(
            title = "Datos parciales disponibles",
            description = "Mostramos informacion limitada. Reintenta para obtener la version completa.",
            icon = Icons.Default.Report,
            tone = AppWarning,
            retryLabel = "Reintentar",
        )

        else -> MapEmptyVisualState(
            title = "No hay unidades para mostrar",
            description = "Proba refrescar o revisar unidades sin coordenadas desde la pestaña de unidades.",
            icon = Icons.Default.Info,
            tone = AppMuted,
            retryLabel = "Actualizar",
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportViewScreen(
    selectedDeviceId: String?,
    devices: List<DeviceSummary>,
    reports: UiState<ReportCatalog>,
    reportFromDate: String,
    reportFromTime: String,
    reportToDate: String,
    reportToTime: String,
    reportBusyAction: ReportKind?,
    reportFeedback: String?,
    reportFeedbackIsError: Boolean,
    onDeviceSelected: (String) -> Unit,
    onGenerateVehicleHistoryReport: () -> Unit,
    onGenerateDrivesStopsReport: () -> Unit,
    onBack: () -> Unit,
    onRefreshReports: () -> Unit,
    onReportFromDateTimeSelected: (String, String) -> Unit,
    onReportToDateTimeSelected: (String, String) -> Unit,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedDevice = devices.firstOrNull { it.id == selectedDeviceId }

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = "Historial",
            subtitle = selectedDevice?.name,
            left = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = AppText) } },
            right = {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Exportar historial de reportes",
                    tint = AppText,
                    modifier = Modifier.size(22.dp),
                )
            },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedDevice?.name ?: "Selecciona un vehículo",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehículo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppPrimary,
                            unfocusedBorderColor = AppMuted,
                            focusedTextColor = AppText,
                            unfocusedTextColor = if (selectedDeviceId != null) AppText else AppMuted,
                            focusedLabelColor = AppPrimary,
                            unfocusedLabelColor = AppMuted,
                            cursorColor = AppPrimary,
                            focusedContainerColor = AppCard,
                            unfocusedContainerColor = AppCard,
                        ),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        devices.forEach { device ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (device.onlineStatus == "online") AppPrimary else AppMuted),
                                        )
                                        Text(device.name, color = AppText)
                                    }
                                },
                                onClick = {
                                    expanded = false
                                    onDeviceSelected(device.id)
                                },
                            )
                        }
                    }
                }
            }
            if (selectedDeviceId != null) {
                item {
                    ReportExportCard(
                        reports = reports,
                        reportFromDate = reportFromDate,
                        reportFromTime = reportFromTime,
                        reportToDate = reportToDate,
                        reportToTime = reportToTime,
                        reportBusyAction = reportBusyAction,
                        reportFeedback = reportFeedback,
                        reportFeedbackIsError = reportFeedbackIsError,
                        onRefreshReports = onRefreshReports,
                        onReportFromDateTimeSelected = onReportFromDateTimeSelected,
                        onReportToDateTimeSelected = onReportToDateTimeSelected,
                        onGenerateVehicleHistoryReport = onGenerateVehicleHistoryReport,
                        onGenerateDrivesStopsReport = onGenerateDrivesStopsReport,
                    )
                }
                item {
                    ReportHistorialContextCard(
                        reports = reports,
                        reportFromDate = reportFromDate,
                        reportFromTime = reportFromTime,
                        reportToDate = reportToDate,
                        reportToTime = reportToTime,
                        onRefreshReports = onRefreshReports,
                    )
                }
            } else {
                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = AppSurfaceMuted),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppMuted.copy(alpha = 0.2f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(28.dp))
                            Text(
                                "Selecciona un vehículo arriba para elegir fechas y generar el reporte en Excel.",
                                color = AppMuted,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
        AppBottomBar(
            selectedTab = RootTab.Reports,
            onGoDevices = onGoDevices,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
}

@Composable
private fun DeviceDetailScreen(
    detail: DeviceDetail?,
    commands: UiState<List<CommandTemplate>>,
    history: UiState<List<HistoryTrip>>,
    reports: UiState<ReportCatalog>,
    reportFromDate: String,
    reportFromTime: String,
    reportToDate: String,
    reportToTime: String,
    reportBusyAction: ReportKind?,
    reportFeedback: String?,
    reportFeedbackIsError: Boolean,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    onRefreshReports: () -> Unit,
    onReportFromDateTimeSelected: (String, String) -> Unit,
    onReportToDateTimeSelected: (String, String) -> Unit,
    onGenerateVehicleHistoryReport: () -> Unit,
    onGenerateDrivesStopsReport: () -> Unit,
    onOpenCommands: () -> Unit,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    if (detail == null) {
        ScreenEmpty("Selecciona una unidad para ver el detalle.")
        return
    }
    val statusLabel = deviceStatusLabel(detail.summary.onlineStatus)
    val quickActions = (commands as? UiState.Success)?.data.orEmpty().take(3)
    val mapAddress = detail.summary.address.ifBlank { "Ubicación pendiente de geocodificación" }
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = detail.summary.name,
            subtitle = detail.summary.address.ifBlank { "Último reporte: ${detail.lastReport ?: "--"}" },
            left = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
            right = { Icon(Icons.Default.MoreVert, null, tint = AppMuted, modifier = Modifier.size(20.dp)) },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp),
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(304.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(AppSurfaceMuted),
                    ) {
                        if (detail.summary.hasRenderableCoordinates()) {
                            PlatformDeviceMap(
                                devices = listOf(DeviceLivePosition(detail.summary.id, detail.summary.name, detail.summary.latitude, detail.summary.longitude, detail.summary.onlineStatus)),
                                selectedDeviceId = detail.summary.id,
                                showLabels = false,
                                showControls = true,
                                onDeviceSelected = {},
                                modifier = Modifier.fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                AppBg.copy(alpha = 0.12f),
                                                Color.Transparent,
                                                AppBg.copy(alpha = 0.48f),
                                            ),
                                        ),
                                    ),
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                StatusPill(statusLabel, deviceStatusColor(detail.summary))
                                DetailMapMetaPill(
                                    icon = Icons.Default.Update,
                                    text = detail.lastReport ?: "--",
                                )
                            }
                            DetailMapCallout(
                                title = "Vista en vivo",
                                subtitle = mapAddress,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(14.dp),
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Icon(Icons.Default.Warning, null, tint = AppMuted, modifier = Modifier.size(30.dp))
                                Text("La unidad no reporta coordenadas válidas.", color = AppText, textAlign = TextAlign.Center)
                                Text(
                                    "Revisa el último estado en UNIDADES o espera una nueva posición.",
                                    color = AppMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (quickActions.isEmpty()) {
                            ActionChip("Ver comandos", Icons.Default.Terminal, true, onClick = onOpenCommands)
                        } else {
                            quickActions.forEachIndexed { index, command ->
                                ActionChip(
                                    label = command.title,
                                    icon = commandIcon(command),
                                    primary = index == 0,
                                    onClick = onOpenCommands,
                                )
                            }
                        }
                    }
                    Text("Estado de la Unidad", color = AppText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            "Velocidad",
                            "${detail.summary.speedKph.toInt()} km/h",
                            speedBadge(detail.summary.speedKph),
                            AppPrimary,
                            Icons.Default.Speed,
                            Modifier.weight(1f),
                        )
                        MetricCard(
                            "Batería",
                            detail.batteryPercent ?: "--",
                            batteryBadge(detail.batteryPercent),
                            AppSuccess,
                            Icons.Default.PhoneAndroid,
                            Modifier.weight(1f),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            "Ignición",
                            detail.ignitionState ?: "--",
                            ignitionBadge(detail.ignitionState),
                            AppWarning,
                            Icons.Default.Key,
                            Modifier.weight(1f),
                        )
                        MetricCard(
                            "Último Reporte",
                            detail.lastReport ?: "--",
                            "",
                            AppPrimary,
                            Icons.Default.Update,
                            Modifier.weight(1f),
                        )
                    }
                }
            }
        }
        AppBottomBar(
            selectedTab = RootTab.Devices,
            onGoDevices = onGoDevices,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryScreen(
    selectedDevice: DeviceDetail?,
    devices: List<DeviceSummary>,
    history: UiState<List<HistoryTrip>>,
    selectedDate: String,
    playbackPointIndex: Int,
    isPlaying: Boolean,
    resolvedAddresses: Map<String, String>,
    resolvingAddressKeys: Set<String>,
    onDeviceSelected: (String) -> Unit,
    onBack: () -> Unit,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onSelectDate: (String) -> Unit,
    onTogglePlayback: () -> Unit,
    onSeekPlayback: (Float) -> Unit,
    onResolveAddress: (String, Double, Double) -> Unit,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    if (selectedDevice == null) {
        var expanded by remember { mutableStateOf(false) }
        Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
            HeaderBar(
                title = "Recorrido",
                subtitle = "Selecciona un vehículo",
                left = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
                right = { Icon(Icons.Default.Route, null, tint = AppMuted, modifier = Modifier.size(20.dp)) },
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(Modifier.height(12.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = "Selecciona un vehículo",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehículo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppPrimary,
                            unfocusedBorderColor = AppMuted,
                            focusedTextColor = AppText,
                            unfocusedTextColor = AppMuted,
                            focusedLabelColor = AppPrimary,
                            unfocusedLabelColor = AppMuted,
                            cursorColor = AppPrimary,
                            focusedContainerColor = AppCard,
                            unfocusedContainerColor = AppCard,
                        ),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        devices.forEach { device ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (device.onlineStatus == "online") AppPrimary else AppMuted),
                                        )
                                        Text(device.name, color = AppText)
                                    }
                                },
                                onClick = {
                                    expanded = false
                                    onDeviceSelected(device.id)
                                },
                            )
                        }
                    }
                }
            }
            AppBottomBar(
                selectedTab = RootTab.Reports,
                onGoDevices = onGoDevices,
                onGoMap = onGoMap,
                onGoAlerts = onGoAlerts,
                onGoReports = onGoReports,
                onGoProfile = onGoProfile,
            )
        }
        return
    }
    val trips = (history as? UiState.Success)?.data.orEmpty()
    val playbackPoints = trips.flatMap { it.points }
    val effectivePlaybackIndex = playbackPointIndex.coerceIn(0, (playbackPoints.size - 1).coerceAtLeast(0))
    val playbackPoint = playbackPoints.getOrNull(effectivePlaybackIndex)
    val mapMarkers = historyMapMarkers(trips, playbackPoint)
    val historyRoutePath = playbackPoints.map { it.latitude to it.longitude }
    val headerTitle = historyDateHeader(selectedDate)
    val totalDistance = historyTotalDistance(trips)
    val totalDuration = historyTotalDuration(trips)
    val playbackProgress = if (playbackPoints.size <= 1) 0f else effectivePlaybackIndex.toFloat() / playbackPoints.lastIndex.toFloat()
    var vehicleExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = "Historial de Recorrido",
            subtitle = selectedDevice?.summary?.name,
            left = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
            right = { Icon(Icons.Default.CalendarMonth, null, tint = AppText, modifier = Modifier.size(20.dp)) },
        )
        ExposedDropdownMenuBox(expanded = vehicleExpanded, onExpandedChange = { vehicleExpanded = it }) {
            OutlinedTextField(
                value = selectedDevice?.summary?.name ?: "Selecciona un vehículo",
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehículo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppPrimary,
                    unfocusedBorderColor = AppMuted,
                    focusedTextColor = AppText,
                    unfocusedTextColor = if (selectedDevice != null) AppText else AppMuted,
                    focusedLabelColor = AppPrimary,
                    unfocusedLabelColor = AppMuted,
                    cursorColor = AppPrimary,
                    focusedContainerColor = AppCard,
                    unfocusedContainerColor = AppCard,
                ),
            )
            ExposedDropdownMenu(expanded = vehicleExpanded, onDismissRequest = { vehicleExpanded = false }) {
                devices.forEach { device ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (device.onlineStatus == "online") AppPrimary else AppMuted),
                                )
                                Text(device.name, color = AppText)
                            }
                        },
                        onClick = {
                            vehicleExpanded = false
                            onDeviceSelected(device.id)
                        },
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = AppSurfaceMuted), shape = RoundedCornerShape(18.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        Text(headerTitle.first, color = AppMuted, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                        Text(headerTitle.second, color = AppText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Rango consultado: 00:00 - 23:59", color = AppMuted, fontSize = 10.sp)
                        Spacer(Modifier.height(3.dp))
                        HistoryDateSelector(
                            selectedDate = selectedDate,
                            onPreviousDate = onPreviousDate,
                            onNextDate = onNextDate,
                            onSelectDate = onSelectDate,
                        )
                    }
                }
            }
            item {
                HistoryRouteMapCard(
                    mapMarkers = mapMarkers,
                    historyRoutePath = historyRoutePath,
                    playbackPoint = playbackPoint,
                    totalDistance = totalDistance,
                    totalDuration = totalDuration,
                    trips = trips,
                    isPlaying = isPlaying,
                    playbackProgress = playbackProgress,
                    playbackEnabled = playbackPoints.size > 1,
                    onTogglePlayback = onTogglePlayback,
                    onSeekPlayback = onSeekPlayback,
                )
            }
            item { Text("Segmentos del Historial", color = AppText, fontWeight = FontWeight.Bold, fontSize = 19.sp) }
            when (history) {
                UiState.Loading -> item { ScreenLoader("Consultando historial...") }
                is UiState.Error -> item { ScreenError(history.message) }
                UiState.Empty -> item { ScreenEmpty("No hay recorrido registrado para ${headerTitle.second.lowercase()}.") }
                is UiState.Success -> item {
                    HistoryTimeline(
                        trips = history.data,
                        resolvedAddresses = resolvedAddresses,
                        resolvingAddressKeys = resolvingAddressKeys,
                        onResolveAddress = onResolveAddress,
                    )
                }
            }
        }
        AppBottomBar(
            selectedTab = RootTab.Map,
            onGoDevices = onGoDevices,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
}

@Composable
private fun HeaderBar(
    title: String,
    subtitle: String? = null,
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    DrSecurityTopBar(
        title = title,
        subtitle = subtitle,
        navigationContent = left,
        actionsContent = { right() },
        containerColor = AppBg,
        titleContentColor = AppText,
        subtitleContentColor = AppMuted,
        navigationIconContentColor = AppText,
    )
}

@Composable
private fun SearchBar(value: String, onChange: (String) -> Unit, placeholder: String = "Buscar unidad o dirección...", trailing: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        placeholder = { Text(placeholder, color = AppMuted, fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = AppMuted) },
        trailingIcon = {
            when {
                value.isNotBlank() -> {
                    IconButton(onClick = { onChange("") }) {
                        Icon(Icons.Default.Close, null, tint = AppMuted)
                    }
                }
                trailing != null -> Icon(trailing, null, tint = AppMuted)
            }
        },
        singleLine = true,
        shape = AppInputShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppCard,
            unfocusedContainerColor = AppCard,
            focusedIndicatorColor = AppPrimary.copy(alpha = 0.35f),
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = AppText,
            unfocusedTextColor = AppText,
            cursorColor = AppPrimary,
        ),
    )
}

@Composable
private fun FilterPill(label: String, selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, onClick: () -> Unit) {
    DrFilterChip(
        label = label,
        selected = selected,
        onSelectedChange = { onClick() },
        leadingIcon = icon,
    )
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.16f),
        contentColor = color,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (color == AppSuccess) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun MapUnitCard(
    device: DeviceSummary,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val online = isOnlineStatus(device.onlineStatus)
    val statusLabel = deviceStatusLabel(device.onlineStatus)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = if (selected) AppPrimaryLight else AppCard),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 1.dp else 0.8.dp,
            if (selected) AppPrimary.copy(alpha = 0.5f) else AppBorderLight,
        ),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(if (online) AppPrimary.copy(alpha = 0.28f) else AppSurfaceMuted),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.DirectionsCar, null, tint = if (online) AppPrimary else AppText)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(device.name, color = AppText, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(device.lastUpdate.ifBlank { "--" }, color = AppMuted, fontSize = 10.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmallMeta(Icons.Default.Speed, "${device.speedKph.toInt()} km/h")
                    SmallMeta(Icons.Default.Info, statusLabel)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = AppPrimary)
        }
    }
}

@Composable
private fun SmallMeta(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = AppMuted, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(4.dp))
        Text(value, color = AppMuted, fontSize = 11.sp)
    }
}

@Composable
private fun ActionChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, primary: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (primary) AppPrimary else AppSurfaceSoft,
        contentColor = if (primary) AppTextOnPrimary else AppPrimary,
        shape = MaterialTheme.shapes.large,
        tonalElevation = if (primary) 3.dp else 0.dp,
        shadowElevation = if (primary) 1.dp else 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = if (primary) AppPrimary else AppPrimary.copy(alpha = 0.28f),
            shape = MaterialTheme.shapes.large,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(label, color = LocalContentColor.current, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, badge: String, tint: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    val compactValue = value.length > 14 || value.contains('\n')
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = AppCard),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(17.dp))
                if (badge.isNotBlank()) StatusPill(badge, tint)
            }
            Text(label, color = AppMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(
                value,
                color = AppText,
                fontWeight = FontWeight.Bold,
                fontSize = if (compactValue) 18.sp else 20.sp,
                lineHeight = if (compactValue) 22.sp else 24.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DetailMapMetaPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        color = AppBg.copy(alpha = 0.58f),
        contentColor = AppText,
        shape = RoundedCornerShape(100.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, null, modifier = Modifier.size(13.dp), tint = AppPrimary)
            Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DetailMapCallout(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = AppBg.copy(alpha = 0.68f),
        contentColor = AppText,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(title, color = AppPrimary.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                color = AppText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HistoryPreviewCard(history: UiState<List<HistoryTrip>>, fallbackAddress: String) {
    val trip = (history as? UiState.Success)?.data?.firstOrNull()
    val firstPoint = trip?.points?.firstOrNull()
    val lastPoint = trip?.points?.lastOrNull()
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = AppCard),
        shape = MaterialTheme.shapes.large,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.28f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppPrimary))
                Box(modifier = Modifier.width(2.dp).height(36.dp).background(AppBorderLight))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AppChevronGray))
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text(
                        "ORIGEN (${trip?.startTime?.ifBlank { "--" } ?: "--"})",
                        color = AppMuted,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        firstPoint?.address?.ifBlank { fallbackAddress.ifBlank { "Sin dirección" } } ?: fallbackAddress.ifBlank { "Sin dirección" },
                        color = AppText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Column {
                    Text(
                        "DESTINO (${trip?.endTime?.ifBlank { "--" } ?: "--"})",
                        color = AppMuted,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        lastPoint?.address?.ifBlank { fallbackAddress.ifBlank { "Sin eventos de hoy" } } ?: fallbackAddress.ifBlank { "Sin eventos de hoy" },
                        color = AppText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportHistorialContextCard(
    reports: UiState<ReportCatalog>,
    reportFromDate: String,
    reportFromTime: String,
    reportToDate: String,
    reportToTime: String,
    onRefreshReports: () -> Unit,
) {
    val rangeLabel = "$reportFromDate $reportFromTime  →  $reportToDate $reportToTime"
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = AppSurfaceMuted),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppMuted.copy(alpha = 0.22f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = "Contexto del reporte. Rango: $rangeLabel."
                },
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = AppPrimary,
                    modifier = Modifier.size(24.dp),
                )
                Column {
                    Text(
                        "Resumen del pedido",
                        color = AppText,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Rango seleccionado",
                        color = AppMuted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Surface(
                color = AppCard,
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AppMuted, modifier = Modifier.size(20.dp))
                    Text(
                        rangeLabel,
                        color = AppText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            when (reports) {
                UiState.Loading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = AppPrimary,
                    )
                    Text("Sincronizando catálogo de reportes…", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                }
                is UiState.Error -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(reports.message, color = AppDanger, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = onRefreshReports) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Reintentar", color = AppPrimary)
                    }
                }
                UiState.Empty -> Text("Aún no hay catálogo de reportes. Pulsa ACTUALIZAR en la tarjeta superior.", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                is UiState.Success -> {
                    val catalog = reports.data
                    val formatOk = catalog.resolveFormat() != null
                    val historyOk = catalog.resolveType(ReportKind.VehicleHistory) != null && formatOk
                    val drivesOk = catalog.resolveType(ReportKind.DrivesStops) != null && formatOk
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportAvailabilityRow(
                            label = "Historial de vehículos (Excel)",
                            available = historyOk,
                        )
                        ReportAvailabilityRow(
                            label = "Recorridos y paradas (Excel)",
                            available = drivesOk,
                        )
                        if (!formatOk) {
                            Text(
                                "Falta un formato de exportación compatible (p. ej. Excel) en el catálogo.",
                                color = AppWarning,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Text(
                            "Ajusta el rango de fechas si necesitas otro día. Los archivos se abren o descargan al generar el reporte.",
                            color = AppMuted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportAvailabilityRow(
    label: String,
    available: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = if (available) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = if (available) "Disponible" else "No disponible",
            tint = if (available) AppPrimary else AppWarning,
            modifier = Modifier.size(20.dp),
        )
        Text(
            label,
            color = AppText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        Text(
            if (available) "Listo" else "No ofrecido",
            color = if (available) AppPrimary else AppMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ReportExportCard(
    reports: UiState<ReportCatalog>,
    reportFromDate: String,
    reportFromTime: String,
    reportToDate: String,
    reportToTime: String,
    reportBusyAction: ReportKind?,
    reportFeedback: String?,
    reportFeedbackIsError: Boolean,
    onRefreshReports: () -> Unit,
    onReportFromDateTimeSelected: (String, String) -> Unit,
    onReportToDateTimeSelected: (String, String) -> Unit,
    onGenerateVehicleHistoryReport: () -> Unit,
    onGenerateDrivesStopsReport: () -> Unit,
) {
    val catalog = (reports as? UiState.Success)?.data
    val loading = reports is UiState.Loading
    val errorMessage = (reports as? UiState.Error)?.message
    val isRangeValid = "${reportFromDate} ${reportFromTime}" <= "${reportToDate} ${reportToTime}"
    val reportFormatAvailable = catalog?.resolveFormat() != null
    val historyAvailable = catalog?.resolveType(ReportKind.VehicleHistory) != null && reportFormatAvailable
    val drivesAvailable = catalog?.resolveType(ReportKind.DrivesStops) != null && reportFormatAvailable
    val historyInlineNote = when {
        loading || catalog == null || !isRangeValid -> null
        !historyAvailable -> "Reporte no disponible en este servidor."
        else -> null
    }
    val drivesInlineNote = when {
        loading || catalog == null || !isRangeValid -> null
        !drivesAvailable -> "Reporte no disponible en este servidor."
        else -> null
    }
    val sharedServerUnavailable = historyInlineNote != null && historyInlineNote == drivesInlineNote
    val availabilityMessage = when {
        loading -> "Cargando catalogo de reportes..."
        errorMessage != null -> errorMessage
        !isRangeValid -> "La fecha/hora de inicio no puede ser mayor que la final."
        else -> null
    }
    val statusMessage = reportFeedback ?: availabilityMessage
    val statusColor = when {
        reportFeedback != null && !reportFeedbackIsError -> AppPrimary
        reportFeedback != null && reportFeedbackIsError -> AppDanger
        statusMessage == errorMessage -> AppDanger
        statusMessage?.contains("no", ignoreCase = true) == true -> AppDanger
        else -> AppMuted
    }
    val statusIcon = when {
        statusColor == AppDanger -> Icons.Default.Warning
        statusColor == AppPrimary -> Icons.Default.CheckCircle
        else -> Icons.Default.Info
    }
    val statusSurfaceColor = when {
        statusColor == AppDanger -> AppDanger.copy(alpha = 0.08f)
        statusColor == AppPrimary -> AppPrimary.copy(alpha = 0.10f)
        else -> AppSurfaceMuted.copy(alpha = 0.85f)
    }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = AppCard),
        shape = MaterialTheme.shapes.large,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.28f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.Default.Report,
                        contentDescription = null,
                        tint = AppPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                    Text("Reportes", color = AppText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                TextButton(onClick = onRefreshReports) {
                    Text("ACTUALIZAR", color = AppPrimary)
                }
            }

            PlatformReportDateTimePickerField(
                label = "Desde",
                date = reportFromDate,
                time = reportFromTime,
                onDateTimeSelected = onReportFromDateTimeSelected,
            )
            PlatformReportDateTimePickerField(
                label = "Hasta",
                date = reportToDate,
                time = reportToTime,
                onDateTimeSelected = onReportToDateTimeSelected,
            )

            statusMessage?.let { msg ->
                Surface(
                    color = statusSurfaceColor,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
                        Text(
                            msg,
                            color = statusColor,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onGenerateVehicleHistoryReport,
                        enabled = !loading && isRangeValid && catalog != null && historyAvailable && reportBusyAction == null,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    ) {
                        Text(
                            if (reportBusyAction == ReportKind.VehicleHistory) "Generando..." else "Historial de vehículos",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (!sharedServerUnavailable) {
                        historyInlineNote?.let {
                            Text(it, color = AppDanger, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onGenerateDrivesStopsReport,
                        enabled = !loading && isRangeValid && catalog != null && drivesAvailable && reportBusyAction == null,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    ) {
                        Text(
                            if (reportBusyAction == ReportKind.DrivesStops) "Generando..." else "Recorridos y Paradas",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (!sharedServerUnavailable) {
                        drivesInlineNote?.let {
                            Text(it, color = AppDanger, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            if (sharedServerUnavailable && historyInlineNote != null) {
                Surface(
                    color = AppDanger.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AppDanger, modifier = Modifier.size(20.dp))
                        Text(
                            historyInlineNote,
                            color = AppDanger,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(text, color = AppText, fontSize = 11.sp)
    }
}

@Composable
private fun SummaryCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = AppCard),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = AppPrimary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(title, color = AppMuted, style = MaterialTheme.typography.labelMedium)
            }
            Text(value, color = AppText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HistoryDateSelector(
    selectedDate: String,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onSelectDate: (String) -> Unit,
) {
    val centerDate = LocalDate.parse(selectedDate)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        IconButton(
            onClick = onPreviousDate,
            modifier = Modifier.size(44.dp),
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Día anterior", tint = AppMuted, modifier = Modifier.size(22.dp))
        }
        Row(
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            (-2..2).forEach { offset ->
                val date = centerDate + DatePeriod(days = offset)
                val isSelected = date.toString() == selectedDate
                HistoryDateChip(
                    date = date,
                    selected = isSelected,
                    onClick = { onSelectDate(date.toString()) },
                )
            }
        }
        IconButton(
            onClick = onNextDate,
            modifier = Modifier.size(44.dp),
        ) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Día siguiente", tint = AppMuted, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun HistoryDateChip(date: LocalDate, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(historyWeekdayShort(date), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text(
                    date.dayOfMonth.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AppPrimary,
            selectedLabelColor = AppTextOnPrimary,
            containerColor = AppSurfaceMuted,
            labelColor = AppText,
        ),
    )
}

@Composable
private fun HistoryMapOverlayStat(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val desc = "$label: $value"
    Surface(
        modifier = modifier.semantics { contentDescription = desc },
        color = AppCard.copy(alpha = 0.94f),
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(18.dp))
            Column {
                Text(label, color = AppMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                Text(
                    value,
                    color = AppText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun HistoryRouteMapCard(
    mapMarkers: List<DeviceLivePosition>,
    historyRoutePath: List<Pair<Double, Double>>,
    playbackPoint: HistoryPoint?,
    totalDistance: String?,
    totalDuration: String?,
    trips: List<HistoryTrip>,
    isPlaying: Boolean,
    playbackProgress: Float,
    playbackEnabled: Boolean,
    onTogglePlayback: () -> Unit,
    onSeekPlayback: (Float) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.88f)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
            ) {
                PlatformDeviceMap(
                    devices = mapMarkers,
                    routePath = historyRoutePath,
                    selectedDeviceId = playbackPoint?.let { "history-current" } ?: mapMarkers.firstOrNull()?.id,
                    showControls = true,
                    onDeviceSelected = {},
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(88.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, AppBg.copy(alpha = 0.78f)),
                            ),
                        ),
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    HistoryMapOverlayStat(
                        label = "Distancia",
                        value = totalDistance ?: "--",
                        icon = Icons.Default.Route,
                    )
                    HistoryMapOverlayStat(
                        label = "Duración",
                        value = totalDuration ?: "--",
                        icon = Icons.Default.Timer,
                    )
                    HistoryMapOverlayStat(
                        label = "Velocidad",
                        value = historyPlaybackSpeedLabel(playbackPoint),
                        icon = Icons.Default.Speed,
                    )
                }
            }
            HorizontalDivider(color = AppMuted.copy(alpha = 0.12f))
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlaybackMetaColumn(
                        modifier = Modifier.weight(1f),
                        color = AppPrimary,
                        label = "Inicio",
                        value = historyOverallStart(trips)?.let(::compactPlaybackTime) ?: "--",
                    )
                    PlaybackMetaColumn(
                        modifier = Modifier.weight(1f),
                        color = AppDanger,
                        label = "Fin",
                        value = historyOverallEnd(trips)?.let(::compactPlaybackTime) ?: "--",
                    )
                }
                HistoryPlaybackControls(
                    enabled = playbackEnabled,
                    isPlaying = isPlaying,
                    progress = playbackProgress,
                    currentPoint = playbackPoint,
                    onTogglePlayback = onTogglePlayback,
                    onSeekPlayback = onSeekPlayback,
                    embedded = true,
                )
            }
        }
    }
}

@Composable
private fun PlaybackMetaColumn(
    modifier: Modifier = Modifier,
    color: Color,
    label: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.20f),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(modifier = Modifier.padding(top = 4.dp).size(7.dp).clip(CircleShape).background(color))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, color = AppMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                Text(
                    value,
                    color = AppText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun HistoryPlaybackControls(
    enabled: Boolean,
    isPlaying: Boolean,
    progress: Float,
    currentPoint: HistoryPoint?,
    onTogglePlayback: () -> Unit,
    onSeekPlayback: (Float) -> Unit,
    embedded: Boolean = false,
) {
    @Composable
    fun PlaybackBody() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (embedded) Modifier.padding(0.dp) else Modifier.padding(12.dp)),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            currentPoint?.let { point ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = AppPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            point.timestamp.let(::compactPlaybackTime).ifBlank { "--" },
                            color = AppPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                    Text(
                        playbackLocationLabel(point),
                        color = AppMuted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(start = 10.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FilledTonalIconButton(
                    onClick = onTogglePlayback,
                    enabled = enabled,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar reproducción del recorrido" else "Reproducir recorrido",
                        modifier = Modifier.size(26.dp),
                    )
                }
                Slider(
                    value = progress,
                    onValueChange = onSeekPlayback,
                    valueRange = 0f..1f,
                    enabled = enabled,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        activeTrackColor = AppPrimary,
                        inactiveTrackColor = AppPrimary.copy(alpha = 0.22f),
                        thumbColor = AppPrimary,
                    ),
                )
            }
        }
    }
    if (embedded) {
        PlaybackBody()
    } else {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(containerColor = AppCard),
            shape = MaterialTheme.shapes.large,
        ) {
            PlaybackBody()
        }
    }
}

private enum class HistorySegmentKind {
    Drive,
    Stop,
    EventLike,
}

private data class HistorySegmentUiModel(
    val id: String,
    val kind: HistorySegmentKind,
    val label: String,
    val title: String,
    val primaryTime: String,
    val secondaryMeta: String,
    val address: String,
    val hasNativeAddress: Boolean,
    val duration: String,
    val distance: String,
    val accentColor: Color,
    val previewPoints: List<HistoryPoint>,
    val mapPoints: List<HistoryPoint>,
    val requestPoint: HistoryPoint?,
)

@Composable
private fun HistoryTimeline(
    trips: List<HistoryTrip>,
    resolvedAddresses: Map<String, String>,
    resolvingAddressKeys: Set<String>,
    onResolveAddress: (String, Double, Double) -> Unit,
) {
    if (trips.isEmpty()) {
        ScreenEmpty("No hay recorrido registrado para hoy.")
        return
    }
    val segments = remember(trips) { trips.toHistorySegmentUiModels() }
    var expandedSegmentId by rememberSaveable(segments.map { it.id }.joinToString(separator = "|")) { mutableStateOf<String?>(null) }
    Column(modifier = Modifier.fillMaxWidth()) {
        segments.forEachIndexed { index, segment ->
            HistorySegmentCard(
                segment = segment,
                resolvedAddress = resolvedAddresses[segment.id],
                isResolvingAddress = segment.id in resolvingAddressKeys,
                isExpanded = expandedSegmentId == segment.id,
                onToggleExpanded = {
                    expandedSegmentId = if (expandedSegmentId == segment.id) null else segment.id
                },
                onResolveAddress = segment.requestPoint?.let { point ->
                    { onResolveAddress(segment.id, point.latitude, point.longitude) }
                },
                modifier = Modifier.padding(bottom = if (index == segments.lastIndex) 0.dp else 12.dp),
            )
        }
    }
}

@Composable
private fun HistorySegmentCard(
    segment: HistorySegmentUiModel,
    resolvedAddress: String?,
    isResolvingAddress: Boolean,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onResolveAddress: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val displayAddress = resolvedAddress ?: segment.address
    val canResolveAddress = !segment.hasNativeAddress && resolvedAddress == null && onResolveAddress != null
    val showGeoapifyAttribution = !segment.hasNativeAddress && (canResolveAddress || isResolvingAddress || resolvedAddress != null)
    val previewHeight = when {
        isExpanded && segment.kind == HistorySegmentKind.Stop -> 184.dp
        isExpanded -> 220.dp
        segment.kind == HistorySegmentKind.Stop -> 116.dp
        else -> 156.dp
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = AppCard),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Surface(
                        color = segment.accentColor.copy(alpha = 0.14f),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = historySegmentIcon(segment.kind),
                                contentDescription = null,
                                tint = segment.accentColor,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = segment.primaryTime,
                                color = AppText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            StatusPill(segment.label, segment.accentColor)
                        }
                        Text(
                            text = segment.title,
                            color = AppText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = displayAddress,
                            color = AppMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (canResolveAddress || isResolvingAddress) {
                            OutlinedButton(
                                onClick = { onResolveAddress?.invoke() },
                                enabled = !isResolvingAddress && onResolveAddress != null,
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = AppPrimary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (isResolvingAddress) "Consultando dirección..." else "Mostrar dirección")
                            }
                        }
                        if (showGeoapifyAttribution) {
                            Text(
                                text = ApiEnvironment.geoapifyAttribution,
                                color = AppMuted,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
            if (segment.previewPoints.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable(onClick = onToggleExpanded)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlatformHistorySegmentMapPreview(
                        points = segment.mapPoints,
                        isStopSegment = segment.kind == HistorySegmentKind.Stop,
                        expanded = isExpanded,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(previewHeight),
                        fallback = {
                            HistorySegmentFallbackPreview(
                                segment = segment,
                                modifier = Modifier.fillMaxSize(),
                            )
                        },
                    )
                    Text(
                        text = if (isExpanded) "Toca la tarjeta para cerrar el mapa" else "Toca la tarjeta para abrir el mapa OSM",
                        color = AppMuted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    if (isExpanded) {
                        Text(
                            text = "© OpenStreetMap contributors",
                            color = AppMuted,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                HistorySegmentMetaChip(
                    label = if (segment.kind == HistorySegmentKind.Stop) "Duración" else "Tiempo",
                    value = segment.duration,
                    accentColor = segment.accentColor,
                    modifier = Modifier.weight(1f),
                )
                HistorySegmentMetaChip(
                    label = if (segment.kind == HistorySegmentKind.Stop) "Estado" else "Distancia",
                    value = if (segment.kind == HistorySegmentKind.Stop) segment.secondaryMeta else segment.distance,
                    accentColor = if (segment.kind == HistorySegmentKind.Stop) AppMuted else AppPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
            if (segment.kind == HistorySegmentKind.Stop && segment.secondaryMeta.isNotBlank()) {
                Text(
                    text = segment.secondaryMeta,
                    color = AppMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun HistorySegmentMetaChip(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = accentColor.copy(alpha = 0.20f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, color = AppMuted, style = MaterialTheme.typography.labelSmall)
            Text(
                text = value.ifBlank { "--" },
                color = AppText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun HistorySegmentFallbackPreview(
    segment: HistorySegmentUiModel,
    modifier: Modifier = Modifier,
) {
    val normalizedPoints = remember(segment.previewPoints) { normalizeHistoryPreviewPoints(segment.previewPoints) }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = AppSurfaceMuted,
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@Canvas

            repeat(3) { index ->
                val y = height * ((index + 1) / 4f)
                drawLine(
                    color = AppBorderLight.copy(alpha = 0.6f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            repeat(4) { index ->
                val x = width * ((index + 1) / 5f)
                drawLine(
                    color = AppBorderLight.copy(alpha = 0.4f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            if (normalizedPoints.isEmpty()) return@Canvas

            val mappedPoints = normalizedPoints.map { point ->
                Offset(
                    x = point.x * width,
                    y = point.y * height,
                )
            }

            if (mappedPoints.size == 1) {
                drawCircle(
                    color = segment.accentColor,
                    radius = 7.dp.toPx(),
                    center = mappedPoints.first(),
                )
                drawCircle(
                    color = AppBorderLight,
                    radius = 14.dp.toPx(),
                    center = mappedPoints.first(),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
                )
                return@Canvas
            }

            val routePath = Path().apply {
                moveTo(mappedPoints.first().x, mappedPoints.first().y)
                mappedPoints.drop(1).forEach { lineTo(it.x, it.y) }
            }

            drawPath(
                path = routePath,
                color = AppText.copy(alpha = 0.18f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
            drawPath(
                path = routePath,
                color = segment.accentColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
            drawCircle(color = AppSuccess, radius = 5.dp.toPx(), center = mappedPoints.first())
            drawCircle(color = AppDanger, radius = 5.dp.toPx(), center = mappedPoints.last())
        }
    }
}

private data class NormalizedPreviewPoint(val x: Float, val y: Float)

private fun List<HistoryTrip>.toHistorySegmentUiModels(): List<HistorySegmentUiModel> =
    mapIndexed { index, trip ->
        val kind = trip.toHistorySegmentKind()
        val firstPoint = trip.points.firstOrNull()
        val lastPoint = trip.points.lastOrNull()
        val requestPoint = firstPoint ?: lastPoint
        val primaryTime = compactPlaybackTime(trip.startTime.ifBlank { firstPoint?.timestamp.orEmpty().ifBlank { trip.title } })
            .ifBlank { "--" }
        val title = when (kind) {
            HistorySegmentKind.Drive -> trip.title.ifBlank { "Recorrido" }
            HistorySegmentKind.Stop -> trip.title.ifBlank { "Parada detectada" }
            HistorySegmentKind.EventLike -> trip.title.ifBlank { historyStatusLabel(trip.statusCode) }
        }
        val nativeAddress = firstPoint?.address
            ?.takeIf { it.isNotBlank() }
            ?: lastPoint?.address?.takeIf { it.isNotBlank() }
        val address = nativeAddress
            ?: historyCoordinateFallback(firstPoint ?: lastPoint)
            ?: "Sin dirección disponible"
        val derivedStopEnd = this.getOrNull(index + 1)
            ?.takeIf { kind == HistorySegmentKind.Stop }
            ?.startTime
            ?.takeIf { it.isNotBlank() }
            ?.let(::compactPlaybackTime)
            .orEmpty()

        HistorySegmentUiModel(
            id = buildString {
                append(index)
                append('|')
                append(kind.name)
                append('|')
                append(firstPoint?.timestamp.orEmpty())
                append('|')
                append(lastPoint?.timestamp.orEmpty())
                append('|')
                append(requestPoint?.latitude ?: 0.0)
                append('|')
                append(requestPoint?.longitude ?: 0.0)
            },
            kind = kind,
            label = historyStatusLabel(trip.statusCode),
            title = title,
            primaryTime = primaryTime,
            secondaryMeta = when (kind) {
                HistorySegmentKind.Stop -> derivedStopEnd.takeIf { it.isNotBlank() }?.let { "Fin estimado: $it" } ?: "Parada registrada"
                HistorySegmentKind.Drive -> compactPlaybackTime(trip.endTime).takeIf { it.isNotBlank() }?.let { "Hasta $it" } ?: "Trayecto activo"
                HistorySegmentKind.EventLike -> "Detalle del segmento"
            },
            address = address,
            hasNativeAddress = nativeAddress != null,
            duration = trip.durationLabel.ifBlank { "--" },
            distance = trip.distanceLabel.ifBlank { "--" },
            accentColor = when (kind) {
                HistorySegmentKind.Drive -> AppPrimary
                HistorySegmentKind.Stop -> AppWarning
                HistorySegmentKind.EventLike -> AppMuted
            },
            previewPoints = historyPreviewPointsForSegment(trip, kind),
            mapPoints = trip.points,
            requestPoint = requestPoint,
        )
    }

private fun HistoryTrip.toHistorySegmentKind(): HistorySegmentKind =
    when (statusCode) {
        1, 3, 4 -> HistorySegmentKind.Drive
        2 -> HistorySegmentKind.Stop
        else -> HistorySegmentKind.EventLike
    }

private fun historyPreviewPointsForSegment(
    trip: HistoryTrip,
    kind: HistorySegmentKind,
): List<HistoryPoint> =
    when {
        trip.points.isEmpty() -> emptyList()
        kind == HistorySegmentKind.Stop -> listOfNotNull(trip.points.firstOrNull(), trip.points.lastOrNull()).distinct()
        trip.points.size <= 16 -> trip.points
        else -> {
            val step = max(1, trip.points.size / 12)
            buildList {
                trip.points.forEachIndexed { index, point ->
                    if (index == 0 || index == trip.points.lastIndex || index % step == 0) add(point)
                }
            }
        }
    }

private fun historyCoordinateFallback(point: HistoryPoint?): String? =
    point?.let { "Lat ${"%.5f".format(it.latitude)}, Lng ${"%.5f".format(it.longitude)}" }

private fun historySegmentIcon(kind: HistorySegmentKind) = when (kind) {
    HistorySegmentKind.Drive -> Icons.Default.Route
    HistorySegmentKind.Stop -> Icons.Default.Pause
    HistorySegmentKind.EventLike -> Icons.Default.Info
}

private fun normalizeHistoryPreviewPoints(points: List<HistoryPoint>): List<NormalizedPreviewPoint> {
    if (points.isEmpty()) return emptyList()

    val minLat = points.minOf { it.latitude }
    val maxLat = points.maxOf { it.latitude }
    val minLng = points.minOf { it.longitude }
    val maxLng = points.maxOf { it.longitude }
    val latRange = (maxLat - minLat).takeIf { it > 0.0 } ?: 0.0001
    val lngRange = (maxLng - minLng).takeIf { it > 0.0 } ?: 0.0001
    val padding = 0.10f

    return points.map { point ->
        val normalizedX = ((point.longitude - minLng) / lngRange).toFloat()
        val normalizedY = ((point.latitude - minLat) / latRange).toFloat()
        NormalizedPreviewPoint(
            x = padding + normalizedX * (1f - padding * 2),
            y = 1f - (padding + normalizedY * (1f - padding * 2)),
        )
    }
}

private fun deviceStatusColor(device: DeviceSummary): Color = when {
    hasRenderableAlarm(device.alarm) -> AppWarning
    isOnlineStatus(device.onlineStatus) -> AppPrimary
    else -> AppMuted
}

private fun speedBadge(speedKph: Double): String = when {
    speedKph >= 90 -> "ALTA"
    speedKph > 0 -> "NORMAL"
    else -> "DETENIDO"
}

private fun batteryBadge(level: String?): String {
    val value = level
        ?.filter { it.isDigit() }
        ?.toIntOrNull()
        ?: return ""
    return when {
        value >= 70 -> "ÓPTIMO"
        value >= 30 -> "MEDIO"
        else -> "BAJO"
    }
}

private fun ignitionBadge(value: String?): String = when {
    value.isNullOrBlank() -> ""
    value.contains("on", ignoreCase = true) || value.contains("enc", ignoreCase = true) -> "ACTIVO"
    else -> "APAGADO"
}

private fun commandIcon(command: CommandTemplate) = when {
    command.title.contains("ubic", true) -> Icons.Default.LocationOn
    command.title.contains("rein", true) -> Icons.Default.RestartAlt
    command.title.contains("bloq", true) || command.title.contains("apag", true) || command.title.contains("inmov", true) -> Icons.Default.NoEncryption
    command.title.contains("panic", true) || command.title.contains("pánic", true) -> Icons.Default.NotificationsActive
    command.title.contains("abr", true) || command.title.contains("lock", true) -> Icons.Default.LockOpen
    else -> Icons.Default.Terminal
}

private fun profileDisplayName(email: String): String =
    email.substringBefore("@")
        .split('.', '_', '-')
        .filter { it.isNotBlank() }
        .joinToString(" ") { token ->
            token.lowercase().replaceFirstChar { char -> char.uppercase() }
        }
        .ifBlank { "Usuario ${AppBrand.displayName}" }

private fun profileInitials(email: String): String =
    profileDisplayName(email)
        .split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "DG" }

private fun historyDateHeader(dateIso: String): Pair<String, String> {
    val date = LocalDate.parse(dateIso)
    val month = when (date.monthNumber) {
        1 -> "ENERO"
        2 -> "FEBRERO"
        3 -> "MARZO"
        4 -> "ABRIL"
        5 -> "MAYO"
        6 -> "JUNIO"
        7 -> "JULIO"
        8 -> "AGOSTO"
        9 -> "SEPTIEMBRE"
        10 -> "OCTUBRE"
        11 -> "NOVIEMBRE"
        else -> "DICIEMBRE"
    }
    val weekday = historyWeekdayFull(date)
    return month to "$weekday, ${date.dayOfMonth.toString().padStart(2, '0')}"
}

private fun historyWeekdayFull(date: LocalDate): String =
    when (date.dayOfWeek.name) {
        "MONDAY" -> "Lunes"
        "TUESDAY" -> "Martes"
        "WEDNESDAY" -> "Miércoles"
        "THURSDAY" -> "Jueves"
        "FRIDAY" -> "Viernes"
        "SATURDAY" -> "Sábado"
        else -> "Domingo"
    }

private fun historyWeekdayShort(date: LocalDate): String =
    when (date.dayOfWeek.name) {
        "MONDAY" -> "LUN"
        "TUESDAY" -> "MAR"
        "WEDNESDAY" -> "MIE"
        "THURSDAY" -> "JUE"
        "FRIDAY" -> "VIE"
        "SATURDAY" -> "SAB"
        else -> "DOM"
    }

private fun historyMapMarkers(trips: List<HistoryTrip>, playbackPoint: HistoryPoint?): List<DeviceLivePosition> {
    val points = trips.flatMap { it.points }
    if (points.isEmpty()) return emptyList()
    val start = points.first()
    val end = points.last()
    return buildList {
        add(
            DeviceLivePosition(
                id = "history-start",
                title = "Inicio",
                latitude = start.latitude,
                longitude = start.longitude,
                status = "marker-start",
            ),
        )
        playbackPoint?.let {
            val timeLabel = it.timestamp.let(::compactPlaybackTime).ifBlank { "Actual" }
            add(
                DeviceLivePosition(
                    id = "history-current",
                    title = timeLabel,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    status = "marker-current",
                ),
            )
        }
        if (end != start) {
            add(
                DeviceLivePosition(
                    id = "history-end",
                    title = "Fin",
                    latitude = end.latitude,
                    longitude = end.longitude,
                    status = "marker-end",
                ),
            )
        }
    }
}

private fun historyStatusLabel(statusCode: Int?): String = when (statusCode) {
    1 -> "Conducción"
    2 -> "Parada"
    3 -> "Inicio"
    4 -> "Fin"
    5 -> "Evento"
    else -> "Segmento"
}

private fun historyOverallStart(trips: List<HistoryTrip>): String? =
    trips.flatMap { it.points }.firstOrNull()?.timestamp?.takeIf { it.isNotBlank() }

private fun historyOverallEnd(trips: List<HistoryTrip>): String? =
    trips.flatMap { it.points }.lastOrNull()?.timestamp?.takeIf { it.isNotBlank() }

private fun compactPlaybackTime(value: String): String {
    val match = Regex("(\\d{2}:\\d{2}:\\d{2})").find(value)
    return match?.value ?: value
}

private fun playbackLocationLabel(point: HistoryPoint): String =
    point.address.ifBlank {
        "Lat ${"%.5f".format(point.latitude)}, Lng ${"%.5f".format(point.longitude)}"
    }

private fun historyPlaybackSpeedLabel(point: HistoryPoint?): String {
    val s = point?.speed ?: return "--"
    if (!s.isFinite() || s < 0.5) return "--"
    return "${s.roundToInt()} km/h"
}

private fun historyTotalDistance(trips: List<HistoryTrip>): String? {
    val total = trips.sumOf { trip ->
        trip.distanceLabel
            .replace(',', '.')
            .filter { it.isDigit() || it == '.' }
            .toDoubleOrNull()
            ?: 0.0
    }
    return total.takeIf { it > 0 }?.let { "%.2f km".format(it) }
}

private fun historyTotalDuration(trips: List<HistoryTrip>): String? {
    val totalSeconds = trips.sumOf { parseDurationToSeconds(it.durationLabel) }
    return totalSeconds.takeIf { it > 0 }?.let(::formatDuration)
}

private fun parseDurationToSeconds(label: String): Int {
    val normalized = label.lowercase()
    val hours = Regex("(\\d+)h").find(normalized)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val minutes = Regex("(\\d+)min").find(normalized)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val seconds = Regex("(\\d+)s").find(normalized)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    return (hours * 3600) + (minutes * 60) + seconds
}

private fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}min ")
        append("${seconds}s")
    }.trim()
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = AppMuted, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun AlertCard(alert: AlertItem, bright: Boolean) {
    val accent = when (alert.severity) {
        AlertSeverity.Critical -> AppDanger
        AlertSeverity.Warning -> AppWarning
        AlertSeverity.Info -> AppPrimary
    }
    val notificationChannels = alert.notifications.channelStates()
    val activeDestinations = alert.notifications.activeDestinations()
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = if (bright) AppDangerLight else AppCard),
        shape = MaterialTheme.shapes.large,
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.4f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (alert.severity) {
                            AlertSeverity.Critical -> Icons.Default.Report
                            AlertSeverity.Warning -> Icons.Default.Warning
                            AlertSeverity.Info -> Icons.Default.Info
                        },
                        null,
                        tint = accent,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        when (alert.severity) {
                            AlertSeverity.Critical -> "CRÍTICA"
                            AlertSeverity.Warning -> "ADVERTENCIA"
                            AlertSeverity.Info -> "INFO"
                        },
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Text(alert.timestamp.ifBlank { "--" }, color = AppMuted, style = MaterialTheme.typography.labelMedium)
            }
            Text(alert.title, color = AppText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AlertStatePill(
                    label = if (alert.active) "Alerta activa" else "Alerta inactiva",
                    active = alert.active,
                )
            }
            Text(
                "Dispositivos: ${alert.deviceIds.joinToString(", ").ifBlank { "Sin referencia" }} • ${alert.message.ifBlank { "Evento del sistema" }}",
                color = AppMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("Canales de notificacion", color = AppMuted, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                notificationChannels.forEach { (label, channel) ->
                    AlertStatePill(
                        label = "$label ${if (channel.active) "activo" else "inactivo"}",
                        active = channel.active,
                    )
                }
            }
            activeDestinations.takeIf { it.isNotBlank() }?.let { destinations ->
                Text(
                    destinations,
                    color = AppMuted,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AlertStatePill(label: String, active: Boolean) {
    val contentColor = if (active) AppSuccess else AppMuted
    Surface(
        color = contentColor.copy(alpha = 0.20f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = if (active) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun AlertNotifications.channelStates(): List<Pair<String, AlertNotificationChannel>> = listOf(
    "Sonido" to sound,
    "Push" to push,
    "Email" to email,
    "SMS" to sms,
    "Webhook" to webhook,
)

private fun AlertNotifications.activeDestinations(): String = listOfNotNull(
    email.input?.takeIf { email.active && it.isNotBlank() }?.let { "Email: $it" },
    sms.input?.takeIf { sms.active && it.isNotBlank() }?.let { "SMS: $it" },
    webhook.input?.takeIf { webhook.active && it.isNotBlank() }?.let { "Webhook: $it" },
).joinToString(" • ")

@Composable
private fun SectionText(text: String) {
    Text(text, color = AppPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
}

@Composable
private fun CommandActionCard(command: CommandTemplate, onClick: () -> Unit) {
    val critical = command.title.contains("apag", true) || command.title.contains("inmov", true)
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.outlinedCardColors(containerColor = if (critical) AppDanger.copy(alpha = 0.20f) else AppCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (critical) AppDanger.copy(alpha = 0.28f) else AppPrimary.copy(alpha = 0.20f)),
        shape = MaterialTheme.shapes.large,
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = {
                Surface(
                    color = if (critical) AppDanger.copy(alpha = 0.28f) else AppPrimary.copy(alpha = 0.20f),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            when {
                                command.title.contains("ubic", true) -> Icons.Default.LocationOn
                                command.title.contains("rein", true) -> Icons.Default.RestartAlt
                                critical -> Icons.Default.Warning
                                else -> Icons.Default.Terminal
                            },
                            null,
                            tint = if (critical) AppDanger else AppPrimary,
                        )
                    }
                }
            },
            headlineContent = {
                Text(command.title, color = if (critical) AppDanger else AppText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            },
            supportingContent = {
                Text(
                    commandAvailabilityLine(command),
                    color = if (critical) AppDanger.copy(alpha = 0.8f) else AppMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingContent = {
                Icon(if (critical) Icons.Default.Warning else Icons.Default.ChevronRight, null, tint = if (critical) AppDanger else AppMuted)
            },
        )
    }
}

@Composable
private fun CommandHistoryCard(title: String, time: String, badge: String, tint: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = AppCard), shape = MaterialTheme.shapes.large) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = { Icon(icon, null, tint = tint) },
            headlineContent = { Text(title, color = AppText, fontWeight = FontWeight.Medium) },
            supportingContent = { Text(time, color = AppMuted, style = MaterialTheme.typography.labelMedium) },
            trailingContent = { StatusPill(badge, tint) },
        )
    }
}

private fun buildCommandMessage(fields: List<CommandField>, values: Map<String, String>): String {
    val entries = fields.mapNotNull { field ->
        val value = values[field.name]?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        field.name to value
    }
    if (entries.isEmpty()) return ""
    return buildString {
        append("{")
        entries.forEachIndexed { i, (k, v) ->
            if (i > 0) append(",")
            append("\"$k\":\"$v\"")
        }
        append("}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommandFieldInput(
    field: CommandField,
    value: String,
    onValueChange: (String) -> Unit,
) {
    val label = "${field.label.ifBlank { field.name }}${if (field.required) " *" else ""}"
    val helperText = commandFieldDescription(field.description)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = AppText,
        unfocusedTextColor = AppText,
        focusedContainerColor = AppCard,
        unfocusedContainerColor = AppCard,
        focusedBorderColor = AppPrimary,
        unfocusedBorderColor = AppMuted.copy(alpha = 0.4f),
        focusedLabelColor = AppPrimary,
        unfocusedLabelColor = AppMuted,
        cursorColor = AppPrimary,
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        when (field.type.lowercase()) {
            "select" -> {
                var expanded by remember { mutableStateOf(false) }
                val selectedOption = field.options.find { it.id == value }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedOption?.title ?: value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(label) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = textFieldColors,
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        field.options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.title.ifBlank { option.id }) },
                                onClick = { onValueChange(option.id); expanded = false },
                            )
                        }
                    }
                }
            }
            "integer" -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }
            else -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true,
                )
            }
        }
        helperText?.let {
            Text(it, color = AppMuted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommandConfirmationDialog(
    commandTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.border(
                width = 1.dp,
                color = AppDanger.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.extraLarge,
            ),
            color = AppCard,
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = AppDanger,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Estás a punto de enviar el comando \"$commandTitle\". Esta acción puede afectar el funcionamiento del vehículo.",
                    color = AppText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = AppDanger),
                ) {
                    Text("Confirmar envío", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text("Cancelar", color = AppText)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommandTemplateDialog(
    command: CommandTemplate,
    isSending: Boolean,
    onSend: (CommandTemplate, String) -> Unit,
    onDismiss: () -> Unit,
) {
    val fieldValues = remember { mutableStateMapOf<String, String>() }
    var showConfirmation by remember { mutableStateOf(false) }
    val critical = command.title.contains("apag", true) ||
        command.title.contains("inmov", true) ||
        command.title.contains("bloqueo", true)
    val allRequiredFilled = command.attributes.filter { it.required }
        .all { fieldValues[it.name]?.isNotBlank() == true }
    val canSend = allRequiredFilled && command.connection != CommandConnection.Unknown && !isSending

    val wasSending = remember { mutableStateOf(false) }
    LaunchedEffect(isSending) {
        if (wasSending.value && !isSending) onDismiss()
        wasSending.value = isSending
    }

    LaunchedEffect(command) {
        command.attributes.forEach { field ->
            if (field.defaultValue != null && !fieldValues.containsKey(field.name)) {
                fieldValues[field.name] = field.defaultValue
            }
        }
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.border(
                width = 1.dp,
                color = AppPrimary.copy(alpha = 0.22f),
                shape = MaterialTheme.shapes.extraLarge,
            ),
            color = AppCard,
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background((if (critical) AppDanger else AppPrimary).copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (critical) Icons.Default.Report else Icons.Default.Terminal,
                        null,
                        tint = if (critical) AppDanger else AppPrimary,
                        modifier = Modifier.size(34.dp),
                    )
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    command.title,
                    color = AppText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Configurá los parámetros y enviá el comando a la unidad.",
                    color = AppMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    commandAvailabilityLine(command),
                    color = AppPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (command.connection == CommandConnection.Unknown) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "El API no informó el canal de este comando. No se puede enviar.",
                        color = AppDanger,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                if (command.attributes.isNotEmpty()) {
                    Spacer(Modifier.height(22.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        command.attributes.forEach { field ->
                            CommandFieldInput(
                                field = field,
                                value = fieldValues[field.name].orEmpty(),
                                onValueChange = { fieldValues[field.name] = it },
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "No requiere parámetros adicionales.",
                        color = AppMuted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = {
                        if (critical) {
                            showConfirmation = true
                        } else {
                            onSend(command, buildCommandMessage(command.attributes, fieldValues))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    enabled = canSend,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (critical) AppDanger else AppPrimary,
                        disabledContainerColor = (if (critical) AppDanger else AppPrimary).copy(alpha = 0.38f),
                    ),
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = AppText,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Enviando…", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Enviar Comando", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar", color = AppMuted)
                }
            }
        }
    }

    if (showConfirmation) {
        CommandConfirmationDialog(
            commandTitle = command.title,
            onConfirm = {
                showConfirmation = false
                onSend(command, buildCommandMessage(command.attributes, fieldValues))
            },
            onDismiss = { showConfirmation = false },
        )
    }
}

private fun commandAvailabilityLine(command: CommandTemplate): String {
    val channelLabel = when (command.connection) {
        CommandConnection.Gprs -> "Canal GPRS"
        CommandConnection.Sms -> "Canal SMS"
        CommandConnection.Unknown -> "Canal no informado por API"
    }
    val requiredFields = command.attributes.count { it.required }
    val fieldsLabel = when {
        command.attributes.isEmpty() -> "sin parámetros"
        requiredFields == 0 -> "${command.attributes.size} parámetros opcionales"
        requiredFields == 1 -> "1 parámetro obligatorio"
        else -> "$requiredFields parámetros obligatorios"
    }
    return "$channelLabel · $fieldsLabel"
}

private fun commandFieldDescription(value: String): String? =
    value
        .replace("<br>", "\n", ignoreCase = true)
        .replace(Regex("<[^>]+>"), "")
        .replace("&nbsp;", " ")
        .trim()
        .takeIf { it.isNotBlank() }

private data class SettingsRow(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val trailingText: String = "",
    val trailingPrimary: Boolean = false,
)

@Composable
private fun SettingsGroup(rows: List<SettingsRow>) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = AppCard), shape = MaterialTheme.shapes.large) {
        Column {
            rows.forEachIndexed { index, row ->
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    leadingContent = {
                        Surface(
                            color = AppPrimary.copy(alpha = 0.20f),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                Icon(row.icon, null, tint = AppPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                    },
                    headlineContent = { Text(row.title, color = AppText, style = MaterialTheme.typography.bodyLarge) },
                    trailingContent = {
                        Text(
                            row.trailingText.ifBlank { "--" },
                            color = if (row.trailingPrimary) AppPrimary else AppMuted,
                            fontWeight = if (row.trailingPrimary) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                )
                if (index != rows.lastIndex) {
                    HorizontalDivider(color = AppPrimary.copy(alpha = 0.20f))
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    modifier: Modifier = Modifier,
    selectedTab: RootTab,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    DrSecurityNavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = AppCard,
        items = listOf(
            MaterialNavigationItem("Unidades", Icons.Default.DirectionsCar, selectedTab == RootTab.Devices, onGoDevices),
            MaterialNavigationItem("Mapa", Icons.Default.Map, selectedTab == RootTab.Map, onGoMap),
            MaterialNavigationItem("Alertas", Icons.Default.NotificationsActive, selectedTab == RootTab.Alerts, onGoAlerts),
            MaterialNavigationItem("Reportes", Icons.Default.Assessment, selectedTab == RootTab.Reports, onGoReports),
            MaterialNavigationItem("Perfil", Icons.Default.Person, selectedTab == RootTab.Profile, onGoProfile),
        ),
    )
}

@Composable
private fun CommandsScreen(
    selectedDevice: DeviceDetail?,
    commands: UiState<List<CommandTemplate>>,
    isSendingCommand: Boolean,
    onSendCommand: (CommandTemplate, String) -> Unit,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    
    onGoReports: () -> Unit,
    onGoProfile: () -> Unit,
) {
    var pending by remember { mutableStateOf<CommandTemplate?>(null) }
    val selectedOnline = selectedDevice?.let { isOnlineStatus(it.summary.onlineStatus) } == true
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = "Comandos Remotos",
            left = { IconButton(onClick = onGoMap) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
            right = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = if (selectedOnline) AppSuccess else AppMuted
                    val statusLabel = if (selectedOnline) "En línea" else "Sin conexión"
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                    Spacer(Modifier.width(8.dp))
                    Text(statusLabel, color = statusColor, fontWeight = FontWeight.Medium, fontSize = 11.sp)
                }
            },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { SectionText("COMANDOS DISPONIBLES") }
            item {
                Text(
                    "Seleccioná un comando para configurar y enviar a la unidad.",
                    color = AppMuted,
                    fontSize = 12.sp,
                )
            }
            when (commands) {
                UiState.Loading -> item { ScreenLoader("Cargando comandos...") }
                is UiState.Error -> item { ScreenError(commands.message) }
                UiState.Empty -> item { ScreenEmpty("No hay comandos disponibles para esta unidad.") }
                is UiState.Success -> items(commands.data, key = { it.type }) { command ->
                    CommandActionCard(command) { pending = command }
                }
            }
        }
        AppBottomBar(
            selectedTab = RootTab.Commands,
            onGoDevices = onGoDevices,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = onGoProfile,
        )
    }
    pending?.let { command ->
        CommandTemplateDialog(
            command = command,
            isSending = isSendingCommand,
            onSend = onSendCommand,
            onDismiss = { pending = null },
        )
    }
}

@Composable
private fun ProfileScreen(
    profile: UiState<UserProfile>,
    onLogout: () -> Unit,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoReports: () -> Unit,
) {
    val profileData = (profile as? UiState.Success)?.data
    val email = profileData?.email.orEmpty()
    val plan = profileData?.plan.orEmpty().ifBlank { "Sin plan reportado" }
    val displayName = profileDisplayName(email)
    val initials = profileInitials(email)
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = "Perfil y Configuración",
            left = { IconButton(onClick = onGoMap) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
            right = { Icon(Icons.Default.MoreHoriz, null, tint = AppMuted, modifier = Modifier.size(20.dp)) },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(AppPrimary),
                        contentAlignment = Alignment.Center,
                    ) { Text(initials, color = AppTextOnPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(displayName, color = AppText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(email, color = AppMuted, fontSize = 12.sp)
                        Text(plan, color = AppPrimary, fontSize = 11.sp)
                    }
                }
            }
            item { SectionLabel("CONFIGURACIÓN") }
            item {
                SettingsGroup(
                    listOf(
                        SettingsRow("Notificaciones push", Icons.Default.Notifications, trailingText = "Activadas"),
                        SettingsRow("Ubicación en segundo plano", Icons.Default.LocationOn, trailingText = "Siempre"),
                        SettingsRow("Precisión GPS", Icons.Default.Info, trailingText = "Alta"),
                    ),
                )
            }
            item { SectionLabel("SOPORTE") }
            item {
                SettingsGroup(
                    listOf(
                        SettingsRow("Centro de ayuda", Icons.Default.Info),
                        SettingsRow("Reportar problema", Icons.Default.Warning),
                        SettingsRow("Términos y condiciones", Icons.Default.Info),
                    ),
                )
            }
            item { SectionLabel("DATOS") }
            item {
                SettingsGroup(
                    listOf(
                        SettingsRow("Idioma", Icons.Default.Info, trailingText = "Español"),
                    ),
                )
            }
            item {
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Default.Close, null, tint = AppDanger)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión", color = AppDanger, fontWeight = FontWeight.Bold)
                }
            }
            item { Text("Versión 2.4.0 (Build 42)", color = AppMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
        }
        AppBottomBar(
            selectedTab = RootTab.Profile,
            onGoDevices = onGoDevices,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = onGoReports,
            onGoProfile = {},
        )
    }
}

@Composable
private fun ReportsScreen(
    devices: UiState<List<DeviceSummary>>,
    reports: UiState<ReportCatalog>,
    alerts: UiState<List<AlertItem>>,
    onRefreshReports: () -> Unit,
    onGoDevices: () -> Unit,
    onGoMap: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReport: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        HeaderBar(
            title = "Reportes",
            left = { IconButton(onClick = onGoMap) { Icon(Icons.Default.ArrowBack, null, tint = AppText) } },
            right = { Icon(Icons.Default.Map, null, tint = AppPrimary, modifier = Modifier.size(20.dp)) },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ReportsHomeSummary(
                    devices = devices,
                    reports = reports,
                    alerts = alerts,
                    onRefreshReports = onRefreshReports,
                )
            }
            item {
                Text("Selecciona el tipo de reporte", color = AppMuted, fontSize = 14.sp)
            }
            item {
                ReportOptionCard(
                    title = "Recorrido",
                    description = "Ver historial de recorrido del vehículo",
                    icon = Icons.Default.Route,
                    onClick = onNavigateToHistory,
                )
            }
            item {
                ReportOptionCard(
                    title = "Historial",
                    description = "Reporte detallado de eventos y estados",
                    icon = Icons.Default.History,
                    onClick = onNavigateToReport,
                )
            }
        }
        AppBottomBar(
            selectedTab = RootTab.Reports,
            onGoDevices = onGoDevices,
            onGoMap = onGoMap,
            onGoAlerts = onGoAlerts,
            onGoReports = {},
            onGoProfile = onGoProfile,
        )
    }
}

@Composable
private fun ReportsHomeSummary(
    devices: UiState<List<DeviceSummary>>,
    reports: UiState<ReportCatalog>,
    alerts: UiState<List<AlertItem>>,
    onRefreshReports: () -> Unit,
) {
    val fleetLine = when (devices) {
        is UiState.Loading -> null to "Cargando unidades…"
        is UiState.Empty -> Icons.Default.DirectionsCar to "Sin unidades registradas"
        is UiState.Error -> Icons.Default.Warning to devices.message
        is UiState.Success -> {
            val list = devices.data
            if (list.isEmpty()) {
                Icons.Default.DirectionsCar to "Sin unidades registradas"
            } else {
                val online = list.count { isOnlineStatus(it.onlineStatus) }
                Icons.Default.DirectionsCar to "${list.size} unidades · $online en línea"
            }
        }
    }
    val catalogTriple = when (reports) {
        is UiState.Loading -> Triple(Icons.Default.Update, "Cargando catálogo de reportes…", false)
        is UiState.Empty -> Triple(Icons.Default.Report, "Catálogo de reportes no disponible", false)
        is UiState.Error -> Triple(Icons.Default.CloudOff, reports.message, true)
        is UiState.Success -> {
            val n = reports.data.types.size
            val suffix = if (n > 0) " ($n tipos)" else ""
            Triple(Icons.Default.CloudDone, "Reportes listos$suffix", false)
        }
    }
    val alertLine: Pair<ImageVector, String>? = when (alerts) {
        is UiState.Success -> {
            val list = alerts.data
            if (list.isEmpty()) null
            else {
                val active = list.count { it.active }
                Icons.Default.Notifications to
                    if (active > 0) "$active alertas activas · ${list.size} en total"
                    else "${list.size} alertas configuradas"
            }
        }
        else -> null
    }
    val summaryDescription = buildString {
        append("Resumen. Flota: ${fleetLine.second}. ")
        append("Catálogo: ${catalogTriple.second}.")
        alertLine?.let { append(" Alertas: ${it.second}.") }
    }
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = summaryDescription },
        colors = CardDefaults.outlinedCardColors(containerColor = AppCard),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Resumen", color = AppText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            SummaryRow(icon = fleetLine.first ?: Icons.Default.DirectionsCar, text = fleetLine.second, loading = fleetLine.first == null)
            SummaryRow(icon = catalogTriple.first, text = catalogTriple.second, loading = reports is UiState.Loading)
            if (catalogTriple.third) {
                TextButton(onClick = onRefreshReports, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Reintentar catálogo", color = AppPrimary)
                }
            }
            alertLine?.let { (icon, text) ->
                SummaryRow(icon = icon, text = text, loading = false)
            }
        }
    }
}

@Composable
private fun SummaryRow(
    icon: ImageVector,
    text: String,
    loading: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = AppPrimary,
            )
        } else {
            Icon(icon, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(22.dp))
        }
        Text(text, color = AppMuted, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ReportOptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$title. $description" }
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppCard),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = AppPrimary, modifier = Modifier.size(28.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = AppText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(description, color = AppMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ScreenLoader(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, color = AppMuted)
    }
}

@Composable
private fun ScreenEmpty(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, color = AppMuted)
    }
}

@Composable
private fun ScreenError(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, color = AppDanger)
    }
}
