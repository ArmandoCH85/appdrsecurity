package com.drsecuritygps.app.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import kotlin.math.min
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drsecuritygps.app.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeUiColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import com.drsecuritygps.app.core.model.DeviceLivePosition
import com.drsecuritygps.app.core.model.MapCapability
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private object NativeColors {
    const val FallbackBackground = "#F3F3F3"
    const val FallbackText = "#7A7A7A"
    const val Polyline = "#58A8E0"
    const val Shadow = "#000000"
    const val MarkerBody = "#222222"
    const val SelectedLabelBackground = "#EAF4FD"
    const val LabelBorder = "#E6E6E6"
    const val StatusStart = "#48A850"
    const val StatusCurrent = "#58A8E0"
    const val StatusEnd = "#EA5954"
    const val StatusOffline = "#6F6F6F"
    /** Texto de etiqueta mapa: gris-azulado, legible sobre OSM. */
    const val MapLabelText = "#455A64"
    const val MapLabelTextSelected = "#1C3A4A"
    const val MapLabelBorder = "#B0BEC5"
}

@Composable
actual fun PlatformDeviceMap(
    devices: List<DeviceLivePosition>,
    routePath: List<Pair<Double, Double>>,
    selectedDeviceId: String?,
    showLabels: Boolean,
    showControls: Boolean,
    controlsBottomExtraPadding: Dp,
    capability: MapCapability,
    onDeviceSelected: (String) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onRecenter: () -> Unit,
    onToggleLayer: () -> Unit,
    onUnavailableAction: () -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val vehicleBitmap: Bitmap? = null
    val focusDevice = remember(devices, selectedDeviceId) {
        val visibleDevices = devices.filter( DeviceLivePosition::hasValidCoordinate)
        visibleDevices.firstOrNull { it.id == selectedDeviceId } ?: visibleDevices.firstOrNull()
    }

    // State for loaded icons (triggers recomposition when updated)
    var iconCache by remember { mutableStateOf<Map<String, Bitmap>>(emptyMap()) }

    val mapView = remember(context) {
        runCatching {
            Configuration.getInstance().load(appContext, appContext.getSharedPreferences("osmdroid", 0))
            Configuration.getInstance().userAgentValue = appContext.packageName

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(11.0)
            }
        }.getOrNull()
    }

    // Zoom del mapa: osmdroid no escala el icono con el pellizco; al acercar mucho hay que redibujar el dardo.
    var mapZoom by remember { mutableStateOf(11.0) }
    DisposableEffect(mapView) {
        val mv = mapView
        if (mv == null) {
            return@DisposableEffect onDispose { }
        }
        val listener = object : MapListener {
            override fun onScroll(p0: ScrollEvent): Boolean = true
            override fun onZoom(p0: ZoomEvent): Boolean {
                val z = mv.zoomLevelDouble
                if ((z * 2.0).toInt() != (mapZoom * 2.0).toInt()) {
                    mapZoom = z
                }
                return true
            }
        }
        mapZoom = mv.zoomLevelDouble
        mv.addMapListener(listener)
        onDispose {
            mv.removeMapListener(listener)
        }
    }

    LaunchedEffect(capability, mapView) {
        if (capability is MapCapability.Unavailable || mapView == null) {
            onUnavailableAction()
        }
    }

    // Red + decodificación fuera del hilo UI (si no: NetworkOnMainThreadException → siempre icono vectorial).
    LaunchedEffect(devices) {
        val newCache = iconCache.toMutableMap()
        devices.forEach { device ->
            device.iconUrl?.let { url ->
                if (!newCache.containsKey(url)) {
                    val bitmap = withContext(Dispatchers.IO) {
                        runCatching { loadBitmapFromUrl(url) }.getOrNull()
                    }
                    if (bitmap != null) {
                        val capped = capBitmapForMap(context, bitmap)
                        if (capped !== bitmap) {
                            bitmap.recycle()
                        }
                        newCache[url] = capped
                    }
                }
            }
        }
        iconCache = newCache
    }

    if (capability is MapCapability.Unavailable || mapView == null) {
        Box(
            modifier = modifier.background(ComposeColor(0xFFF3F3F3)),
            contentAlignment = Alignment.Center,
        ) {
            Text("Mapa no disponible", color = ComposeColor(0xFF7A7A7A), fontSize = 14.sp)
        }
        return
    }

    // Nunca unir [mapZoom] con el efecto que ajusta cámara: re-ejecutaría setZoom / zoomToBoundingBox y anularía el pellizco.
    LaunchedEffect(devices, routePath, selectedDeviceId, iconCache) {
        runCatching {
            val visibleDevices = devices.filter(DeviceLivePosition::hasValidCoordinate)
            val focus = visibleDevices.firstOrNull { it.id == selectedDeviceId } ?: visibleDevices.firstOrNull()
            val zoomForIcons = mapView.zoomLevelDouble
            mapView.overlays.removeAll { it is Marker || it is Polyline }

            val polylinePoints = routePath
                .filter { (lat, lng) -> lat.isFinite() && lng.isFinite() && lat in -90.0..90.0 && lng in -180.0..180.0 && (lat != 0.0 || lng != 0.0) }
                .map { (lat, lng) -> GeoPoint(lat, lng) }
            if (polylinePoints.size >= 2) {
                mapView.overlays.add(
                    Polyline().apply {
                        setPoints(polylinePoints)
                        outlinePaint.color = Color.parseColor(NativeColors.Polyline)
                        outlinePaint.strokeWidth = 5f
                    },
                )
            }

            visibleDevices.forEach { device ->
                val selected = device.id == selectedDeviceId
                val point = GeoPoint(device.latitude, device.longitude)

                val iconDrawable: Drawable? = device.iconUrl?.let { url ->
                    iconCache[url]?.let { bitmap ->
                        BitmapDrawable(appContext.resources, bitmap)
                    }
                }
                val useHistoryRoutePin = device.id.startsWith("history-")

                val iconMarker = Marker(mapView).apply {
                    id = "icon-${device.id}"
                    position = point
                    title = device.title
                    subDescription = device.status
                    when {
                        iconDrawable != null -> {
                            icon = iconDrawable
                            rotation = 0f
                            setAnchor(Marker.ANCHOR_CENTER, 0.86f)
                        }
                        useHistoryRoutePin -> {
                            icon = buildUnitMarkerIcon(context, vehicleBitmap, device.status, selected)
                            rotation = 0f
                            setAnchor(Marker.ANCHOR_CENTER, 0.86f)
                        }
                        else -> {
                            // Sin icono de imagen: flecha (p. ej. icon_type=arrow → LEILA) según [course] e icon_color
                            icon = buildCourseArrowIcon(
                                context,
                                device.status,
                                device.iconColor,
                                selected,
                                mapZoom = zoomForIcons,
                            )
                            rotation = (device.courseDegrees ?: 0.0).toFloat()
                            setAnchor(0.5f, 0.5f)
                        }
                    }
                    setOnMarkerClickListener { _, _ ->
                        onDeviceSelected(device.id)
                        true
                    }
                }

                mapView.overlays.add(iconMarker)

                if (showLabels && device.title.isNotBlank()) {
                    val labelMarker = Marker(mapView).apply {
                        id = "label-${device.id}"
                        position = point
                        title = device.title
                        icon = buildUnitLabelIcon(context, device.title, selected)
                        setAnchor(Marker.ANCHOR_CENTER, 1.08f)
                        setOnMarkerClickListener { _, _ ->
                            onDeviceSelected(device.id)
                            true
                        }
                    }
                    mapView.overlays.add(labelMarker)
                }
            }

            when {
                visibleDevices.size == 1 -> {
                    focus?.let {
                        mapView.controller.setZoom(15.0)
                        mapView.controller.animateTo(GeoPoint(it.latitude, it.longitude))
                    }
                }
                visibleDevices.size > 1 -> {
                    val lats = visibleDevices.map { it.latitude }
                    val lngs = visibleDevices.map { it.longitude }
                    val bounds = BoundingBox(
                        lats.max(), lngs.max(), lats.min(), lngs.min(),
                    )
                    mapView.zoomToBoundingBox(bounds.increaseByScale(1.3f), true)
                }
            }
            mapView.invalidate()
        }.onFailure {
            mapView.overlays.removeAll { overlay -> overlay is Marker || overlay is Polyline }
            mapView.invalidate()
        }
    }

    LaunchedEffect(mapZoom) {
        if (mapView == null) return@LaunchedEffect
        val z = mapView.zoomLevelDouble
        val visible = devices.filter(DeviceLivePosition::hasValidCoordinate)
        runCatching {
            visible.forEach { device ->
                if (device.iconUrl != null) return@forEach
                if (device.id.startsWith("history-")) return@forEach
                val m = mapView.overlays.find { o ->
                    o is Marker && (o as Marker).id == "icon-${device.id}"
                } as? Marker
                    ?: return@forEach
                val selected = device.id == selectedDeviceId
                m.icon = buildCourseArrowIcon(
                    context = context,
                    status = device.status,
                    iconColor = device.iconColor,
                    selected = selected,
                    mapZoom = z,
                )
                m.rotation = (device.courseDegrees ?: 0.0).toFloat()
            }
            mapView.invalidate()
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                if (view.tileProvider.tileSource.name() != TileSourceFactory.MAPNIK.name()) {
                    view.setTileSource(TileSourceFactory.MAPNIK)
                }
            },
        )
        if (showControls) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .padding(bottom = controlsBottomExtraPadding),
                horizontalAlignment = Alignment.End,
            ) {
                MapZoomControlButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Acercar mapa",
                ) {
                    mapView.controller.zoomIn()
                    onZoomIn()
                }
                MapZoomControlButton(
                    icon = Icons.Default.Remove,
                    contentDescription = "Alejar mapa",
                ) {
                    mapView.controller.zoomOut()
                    onZoomOut()
                }
                MapZoomControlButton(
                    icon = Icons.Default.MyLocation,
                    contentDescription = "Centrar en unidad",
                ) {
                    focusDevice?.let { device ->
                        mapView.controller.animateTo(GeoPoint(device.latitude, device.longitude))
                    }
                    onRecenter()
                }
            }
        }
    }
}

/**
 * Flecha hacia arriba (Norte = 0°); la rotación del [Marker] aplica el rumbo GPS.
 * Wox aporta [iconColor] p.ej. `red` / `icon_colors` según en línea o parado.
 * SDD map-vehicle-heading: caja táctil ≥44dp, glifo inscrito, borde con [Paint] (no “rim” por scale).
 */
private fun buildCourseArrowIcon(
    context: Context,
    status: String,
    iconColor: String?,
    selected: Boolean,
    mapZoom: Double = 11.0,
): BitmapDrawable {
    val mainColor = courseArrowFillColor(status, iconColor)
    // Bitmap = área táctil; el dardo se escala hacia abajo a zoom alto (el icono no escala con el mapa en osmdroid).
    val boxDp = if (selected) 48 else 44
    val glyphDp = headingGlyphInnerDpForZoom(mapZoom, selected)
    val bitmap = rasterizeHeadingArrow(context, boxDp, glyphDp, mainColor, selected)
    return BitmapDrawable(context.resources, bitmap).apply {
        isFilterBitmap = false
    }
}

/** Tamaño del dardo en función del zoom: calles “grandes” en pantalla (zoom alto) → flecha visual más pequeña. */
private fun headingGlyphInnerDpForZoom(mapZoom: Double, selected: Boolean): Int {
    val base = if (selected) 38 else 32
    val factor = when {
        mapZoom >= 18.0 -> 0.70f
        mapZoom >= 17.0 -> 0.78f
        mapZoom >= 16.0 -> 0.86f
        mapZoom >= 15.0 -> 0.92f
        else -> 1f
    }
    return (base * factor).toInt().coerceIn(20, 40)
}

/** PNG remotos: tamaño unificado para no dominar el mapa (menos "collage" con tiles). */
private fun capBitmapForMap(
    context: Context,
    source: Bitmap,
    maxSideDp: Int = 38,
): Bitmap {
    val maxSidePx = maxSideDp.px(context)
    val sw = source.width
    val sh = source.height
    if (sw <= 0 || sh <= 0) return source
    if (sw <= maxSidePx && sh <= maxSidePx) {
        return source
    }
    val scale = min(maxSidePx.toFloat() / sw, maxSidePx.toFloat() / sh)
    val nw = (sw * scale).toInt().coerceAtLeast(1)
    val nh = (sh * scale).toInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(source, nw, nh, true)
}

/** Geometría alineada con [R.drawable.ic_map_heading_24] (viewBox 24×24, punta hacia -Y de pantalla = Norte). */
private fun headingArrowPath(left: Float, top: Float, edgePx: Float): Path {
    val s = edgePx / 24f
    return Path().apply {
        moveTo(left + 12f * s, top + 2.2f * s)
        lineTo(left + 15.1f * s, top + 20.6f * s)
        lineTo(left + 8.9f * s, top + 20.6f * s)
        close()
    }
}

/**
 * Caja [boxDp]×[boxDp] (toque mín. ~44), glifo [glyphDp] centrado; borde claro = [Paint] STROKE, no capas escaladas.
 */
private fun rasterizeHeadingArrow(
    context: Context,
    boxDp: Int,
    glyphDp: Int,
    mainColor: Int,
    selected: Boolean,
): Bitmap {
    val boxPx = boxDp.px(context)
    val glyphPx = glyphDp.px(context).coerceAtMost(boxPx)
    val left = (boxPx - glyphPx) / 2f
    val top = (boxPx - glyphPx) / 2f
    val d = context.resources.displayMetrics.density
    // Trazo en px de pantalla (sin reescalado posterior: evita bordes borrosos al acercar el mapa).
    val strokeW = d * (if (selected) 2.6f else 2.3f)

    val path = headingArrowPath(left, top, glyphPx.toFloat())

    val strokeFirst = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = strokeW
        strokeJoin = Paint.Join.ROUND
    }
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = mainColor
    }

    val bitmap = Bitmap.createBitmap(boxPx, boxPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    // Orden: trazo claro; el relleno tapa la mitad interior del trazo → “keyline” hacia afuera
    canvas.drawPath(path, strokeFirst)
    canvas.drawPath(path, fill)
    return bitmap
}

private fun loadBitmapFromUrl(url: String): Bitmap? {
    return try {
        val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        connection.doInput = true
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.connect()
        if (connection.responseCode == 200) {
            android.graphics.BitmapFactory.decodeStream(connection.inputStream)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

/** Pin de ruta / historial: gota sin punto central; mismo tratamiento que la flecha. */
private fun buildUnitMarkerIcon(
    context: Context,
    @Suppress("UNUSED_PARAMETER") vehicleBitmap: Bitmap?,
    status: String,
    selected: Boolean,
): BitmapDrawable {
    val width = if (selected) 50.px(context) else 44.px(context)
    val height = if (selected) 60.px(context) else 54.px(context)
    val statusColor = statusFillColor(status)
    val ss = 2
    val wPx = width * ss
    val hPx = height * ss
    val hi = Bitmap.createBitmap(wPx, hPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(hi)
    canvas.scale(ss.toFloat(), ss.toFloat())
    val sizePx = (width * 0.78f).toInt().coerceAtLeast(1)
    val left = (width - sizePx) / 2
    val top = height - sizePx
    val base = requireNotNull(
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.ic_map_location_pin_24,
            context.theme,
        ),
    ).mutate()
    val cx = left + sizePx / 2f
    val cy = top + sizePx / 2f
    val rim = requireNotNull(base.constantState?.newDrawable()).mutate()
    rim.setBounds(left, top, left + sizePx, top + sizePx)
    DrawableCompat.setTint(rim, Color.WHITE)
    val rimScale = if (selected) 1.05f else 1.03f
    canvas.save()
    canvas.scale(rimScale, rimScale, cx, cy)
    rim.draw(canvas)
    canvas.restore()
    base.setBounds(left, top, left + sizePx, top + sizePx)
    DrawableCompat.setTint(base, statusColor)
    base.draw(canvas)
    val out = Bitmap.createScaledBitmap(hi, width, height, true)
    if (out !== hi) {
        hi.recycle()
    }
    return BitmapDrawable(context.resources, out)
}

private fun buildUnitLabelIcon(context: Context, title: String, selected: Boolean): BitmapDrawable {
    val safeTitle = title.trim().ifBlank { "Unidad" }.let {
        if (it.length > 22) it.take(19) + "..." else it
    }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (selected) {
            Color.parseColor(NativeColors.MapLabelTextSelected)
        } else {
            Color.parseColor(NativeColors.MapLabelText)
        }
        textSize = if (selected) 8.5f.spPx(context) else 8f.spPx(context)
        typeface = Typeface.create(Typeface.SANS_SERIF, if (selected) Typeface.BOLD else Typeface.NORMAL)
    }
    val horizontalPadding = if (selected) 6.px(context).toFloat() else 5.px(context).toFloat()
    val textWidth = textPaint.measureText(safeTitle)
    val width = (textWidth + horizontalPadding * 2).toInt()
        .coerceAtLeast(28.px(context))
        .coerceAtMost(140.px(context))
    val height = if (selected) 20.px(context) else 18.px(context)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (selected) Color.parseColor(NativeColors.SelectedLabelBackground) else Color.WHITE
        alpha = if (selected) 252 else 248
        style = Paint.Style.FILL
    }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (selected) Color.parseColor(NativeColors.Polyline) else Color.parseColor(NativeColors.MapLabelBorder)
        style = Paint.Style.STROKE
        strokeWidth = if (selected) 1.25f else 0.85f
    }

    val radius = 8.px(context).toFloat()
    canvas.drawRoundRect(rect, radius, radius, backgroundPaint)
    canvas.drawRoundRect(rect, radius, radius, strokePaint)
    canvas.drawText(safeTitle, horizontalPadding, height / 2f - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}

@Composable
private fun MapZoomControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        color = scheme.surface.copy(alpha = 0.94f),
        contentColor = scheme.primary,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 4.dp,
        modifier = Modifier
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(14.dp)),
    ) {
        Box(
            modifier = Modifier
                // Toque mín. recomendable móvil (~48dp)
                .size(48.dp)
                .background(ComposeUiColor.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

private fun DeviceLivePosition.hasValidCoordinate(): Boolean =
    latitude.isFinite() &&
        longitude.isFinite() &&
        latitude in -90.0..90.0 &&
        longitude in -180.0..180.0 &&
        (latitude != 0.0 || longitude != 0.0)

/** Alinea con `icon_color` / estados de Wox p.ej. rojo al estar parado con flecha. */
private fun courseArrowFillColor(status: String, iconColorName: String?): Int {
    val name = iconColorName?.trim()?.lowercase()
    if (!name.isNullOrEmpty()) {
        return when (name) {
            "red" -> Color.parseColor("#C4514C")
            "green" -> Color.parseColor("#3D8C44")
            "yellow" -> Color.parseColor("#B8962E")
            "orange" -> Color.parseColor("#D97A2A")
            "black" -> Color.parseColor("#37474F")
            "blue" -> Color.parseColor("#1565C0")
            else -> statusFillColor(status)
        }
    }
    return statusFillColor(status)
}

private fun statusFillColor(status: String): Int =
    when {
        status == "marker-start" -> Color.parseColor(NativeColors.StatusStart)
        status == "marker-current" -> Color.parseColor(NativeColors.Polyline)
        status == "marker-end" -> Color.parseColor(NativeColors.StatusEnd)
        status.contains("en línea", ignoreCase = true) ||
            status.contains("online", ignoreCase = true) ||
            status.contains("ack", ignoreCase = true) -> Color.parseColor(NativeColors.StatusStart)
        status.contains("fuera de línea", ignoreCase = true) ||
            status.contains("offline", ignoreCase = true) ||
            status.contains("sin estado", ignoreCase = true) -> Color.parseColor(NativeColors.StatusOffline)
        else -> Color.parseColor(NativeColors.Polyline)
    }

private fun Int.px(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()

private fun Float.spPx(context: Context): Float =
    this * context.resources.displayMetrics.scaledDensity
