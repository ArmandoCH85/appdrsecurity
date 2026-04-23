package com.drsecuritygps.app.core.model

import com.drsecuritygps.app.core.UiState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ReportCatalogChoice(
    val id: String,
    val label: String,
)

@Serializable
data class ReportCatalog(
    val types: List<ReportCatalogChoice> = emptyList(),
    val formats: List<ReportCatalogChoice> = emptyList(),
    val stops: List<ReportCatalogChoice> = emptyList(),
    val filters: List<ReportCatalogChoice> = emptyList(),
)

@Serializable
data class ReportGenerationRequest(
    val title: String,
    val deviceId: String,
    val typeId: String,
    val formatId: String,
    val fromDate: String,
    val fromTime: String,
    val toDate: String,
    val toTime: String,
    val stopId: String? = null,
)

@Serializable
data class ReportGenerationResponse(
    val status: Int? = null,
    val url: String? = null,
)

@Serializable
data class ReportRange(
    val fromDate: String,
    val fromTime: String,
    val toDate: String,
    val toTime: String,
)

data class ReportActionUiState(
    val available: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

data class PendingReportOpenRequest(
    val kind: ReportKind,
    val url: String,
)

data class ReportsUiState(
    val catalog: UiState<ReportCatalog> = UiState.Empty,
    val range: ReportRange = defaultReportRange(),
    val validationMessage: String? = validateReportRange(defaultReportRange()),
    val vehicleHistory: ReportActionUiState = ReportActionUiState(),
    val drivesStops: ReportActionUiState = ReportActionUiState(),
    val pendingOpen: PendingReportOpenRequest? = null,
)

enum class ReportKind {
    VehicleHistory,
    DrivesStops,
}

fun defaultReportRange(now: kotlinx.datetime.Instant = Clock.System.now()): ReportRange {
    val dateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = dateTime.date.toString()
    val time = buildString {
        append(dateTime.hour.toString().padStart(2, '0'))
        append(':')
        append(dateTime.minute.toString().padStart(2, '0'))
    }
    return ReportRange(
        fromDate = today,
        fromTime = "00:00",
        toDate = today,
        toTime = time,
    )
}

internal fun validateReportRange(range: ReportRange): String? {
    val from = "${range.fromDate} ${range.fromTime}"
    val to = "${range.toDate} ${range.toTime}"
    return if (from <= to) {
        null
    } else {
        "La fecha Desde debe ser anterior o igual a Hasta."
    }
}

internal fun normalizeReportText(value: String): String =
    value.lowercase()
        .trim()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ü", "u")
        .replace("ñ", "n")
        .replace(Regex("[^a-z0-9]+"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

internal fun ReportCatalog.resolveType(kind: ReportKind): ReportCatalogChoice? {
    fun ReportCatalogChoice.normalizedTokens(): String = normalizeReportText("$id $label")

    return when (kind) {
        ReportKind.VehicleHistory -> {
            types.firstOrNull { choice ->
                val normalized = choice.normalizedTokens()
                normalized.contains("object history") ||
                    normalized.contains("vehicle history") ||
                    normalized.contains("historial de vehiculos") ||
                    normalized.contains("historial vehiculos") ||
                    (normalized.contains("history") && (normalized.contains("vehicle") || normalized.contains("object"))) ||
                    (normalized.contains("historial") && normalized.contains("vehiculo"))
            } ?: types.firstOrNull { choice ->
                val normalized = choice.normalizedTokens()
                normalized == "history" || normalized.endsWith(" history")
            }
        }

        ReportKind.DrivesStops -> {
            types.firstOrNull { choice ->
                val normalized = choice.normalizedTokens()
                normalized.contains("recorridos y paradas") ||
                    normalized.contains("drives and stops") ||
                    normalized.contains("drives stops") ||
                    ((normalized.contains("drive") || normalized.contains("drives") || normalized.contains("recorrido")) &&
                        (normalized.contains("stop") || normalized.contains("stops") || normalized.contains("parada") || normalized.contains("paradas")))
            }
        }
    }
}

internal fun ReportCatalog.resolveFormat(): ReportCatalogChoice? =
    formats.firstOrNull { choice ->
        val normalized = normalizeReportText("${choice.id} ${choice.label}")
        normalized.contains("xls") || normalized.contains("excel")
    }

internal fun ReportCatalog.defaultStop(): ReportCatalogChoice? = stops.firstOrNull()
