package com.drsecuritygps.app.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.drsecuritygps.app.core.model.HistoryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
actual fun PlatformHistorySegmentMapPreview(
    points: List<HistoryPoint>,
    isStopSegment: Boolean,
    expanded: Boolean,
    modifier: Modifier,
    fallback: @Composable () -> Unit,
) {
    if (!expanded || points.isEmpty()) {
        fallback()
        return
    }

    val context = LocalContext.current
    val appContext = context.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember(context, expanded) {
        runCatching {
            Configuration.getInstance().load(appContext, appContext.getSharedPreferences("osmdroid", 0))
            Configuration.getInstance().userAgentValue = "${appContext.packageName}.history-preview"

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(false)
                setTilesScaledToDpi(true)
                controller.setZoom(if (isStopSegment) 16.0 else 15.0)
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false
            }
        }.getOrNull()
    }

    if (mapView == null) {
        fallback()
        return
    }

    LaunchedEffect(points, isStopSegment, mapView) {
        runCatching {
            val geoPoints = points.toGeoPoints()
            mapView.overlays.removeAll { it is Marker || it is Polyline }

            if (geoPoints.size >= 2) {
                mapView.overlays.add(
                    Polyline().apply {
                        setPoints(geoPoints)
                        outlinePaint.color = Color.parseColor("#58A8E0")
                        outlinePaint.strokeWidth = if (isStopSegment) 4f else 5f
                    },
                )
            }

            geoPoints.firstOrNull()?.let { start ->
                mapView.overlays.add(
                    Marker(mapView).apply {
                        position = start
                        title = "Inicio"
                        icon = buildHistoryMarkerIcon(context, Color.parseColor("#48A850"), "A")
                        setAnchor(Marker.ANCHOR_CENTER, 0.88f)
                    },
                )
            }

            geoPoints.lastOrNull()?.takeIf { it != geoPoints.firstOrNull() }?.let { end ->
                mapView.overlays.add(
                    Marker(mapView).apply {
                        position = end
                        title = if (isStopSegment) "Parada" else "Fin"
                        icon = buildHistoryMarkerIcon(
                            context = context,
                            color = if (isStopSegment) Color.parseColor("#F89818") else Color.parseColor("#EA5954"),
                            label = if (isStopSegment) "P" else "B",
                        )
                        setAnchor(Marker.ANCHOR_CENTER, 0.88f)
                    },
                )
            }

            when {
                geoPoints.size == 1 -> {
                    mapView.controller.setZoom(if (isStopSegment) 16.0 else 15.0)
                    mapView.controller.animateTo(geoPoints.first())
                }

                geoPoints.size > 1 -> {
                    val bounds = BoundingBox.fromGeoPointsSafe(geoPoints)
                    mapView.zoomToBoundingBox(bounds.increaseByScale(if (isStopSegment) 1.6f else 1.35f), true)
                }
            }

            mapView.invalidate()
        }.onFailure {
            mapView.overlays.removeAll { overlay -> overlay is Marker || overlay is Polyline }
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

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            if (view.tileProvider.tileSource.name() != TileSourceFactory.MAPNIK.name()) {
                view.setTileSource(TileSourceFactory.MAPNIK)
            }
        },
    )
}

private fun List<HistoryPoint>.toGeoPoints(): List<GeoPoint> =
    filter { point ->
        point.latitude.isFinite() &&
            point.longitude.isFinite() &&
            point.latitude in -90.0..90.0 &&
            point.longitude in -180.0..180.0 &&
            (point.latitude != 0.0 || point.longitude != 0.0)
    }.map { GeoPoint(it.latitude, it.longitude) }

private fun buildHistoryMarkerIcon(context: Context, color: Int, label: String): BitmapDrawable {
    val width = 42.px(context)
    val height = 52.px(context)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val headRadius = width * 0.22f
    val centerX = width / 2f
    val centerY = headRadius + 7.px(context)
    val pinTipY = height - 7.px(context).toFloat()

    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.parseColor("#222222")
        style = Paint.Style.FILL
    }
    val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2.6f
    }
    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 11f.spPx(context)
        isFakeBoldText = true
    }

    canvas.drawCircle(centerX, centerY, headRadius + 4f, bodyPaint)
    canvas.drawCircle(centerX, centerY, headRadius + 1.5f, ringPaint)
    canvas.drawCircle(centerX, centerY, headRadius * 0.84f, fillPaint)
    canvas.drawCircle(centerX, pinTipY - 2f, 2.5f, fillPaint)
    canvas.drawLine(centerX, centerY + headRadius * 0.9f, centerX, pinTipY - 4f, bodyPaint)
    canvas.drawText(label, centerX, centerY - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun Int.px(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()

private fun Float.spPx(context: Context): Float =
    this * context.resources.displayMetrics.scaledDensity
