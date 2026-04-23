package com.drsecuritygps.app.network

import com.drsecuritygps.app.core.model.AlertItem
import com.drsecuritygps.app.core.model.AlertEventItem
import com.drsecuritygps.app.core.model.AlertNotificationChannel
import com.drsecuritygps.app.core.model.AlertNotifications
import com.drsecuritygps.app.core.model.AlertSeverity
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

internal fun parseAlertItem(alert: JsonObject): AlertItem {
    val type = alert.string("type").orEmpty()
    return AlertItem(
        id = alert["id"]?.jsonPrimitive?.content.orEmpty(),
        title = alert.string("name").orEmpty().ifBlank { type },
        severity = mapAlertSeverity(type),
        active = alert.booleanFlag("active") ?: false,
        deviceIds = alert.array("devices").mapNotNull { it.stringValue() },
        notifications = alert.obj("notifications")?.let(::parseAlertNotifications) ?: AlertNotifications(),
        message = type,
        timestamp = alert.string("updated_at").orEmpty(),
    )
}

internal fun parseAlertEventItem(event: JsonObject): AlertEventItem {
    val message = event.string("message").orEmpty()
    return AlertEventItem(
        id = event["id"]?.jsonPrimitive?.content.orEmpty(),
        deviceId = event["device_id"]?.jsonPrimitive?.content.orEmpty(),
        alertId = event["alert_id"]?.jsonPrimitive?.contentOrNull,
        message = message,
        address = event.string("address").orEmpty(),
        latitude = event.double("latitude"),
        longitude = event.double("longitude"),
        speed = event.double("speed"),
        timestamp = event.string("time").orEmpty().ifBlank { event.string("created_at").orEmpty() },
        severity = mapAlertEventSeverity(message),
    )
}

internal fun parseAlertNotifications(obj: JsonObject): AlertNotifications =
    AlertNotifications(
        sound = obj.parseChannel("sound"),
        push = obj.parseChannel("push"),
        email = obj.parseChannel("email"),
        sms = obj.parseChannel("sms"),
        webhook = obj.parseChannel("webhook"),
    )

internal fun parseAlertNotificationChannel(obj: JsonObject): AlertNotificationChannel =
    AlertNotificationChannel(
        active = obj.booleanFlag("active") ?: false,
        input = obj.string("input"),
    )

internal fun JsonObject.booleanFlag(name: String): Boolean? = this[name].booleanFlag()

internal fun JsonElement?.booleanFlag(): Boolean? = when (this) {
    is JsonPrimitive -> booleanOrNull
        ?: intOrNull?.let { it != 0 }
        ?: contentOrNull?.trim()?.lowercase()?.let {
            when (it) {
                "1", "true" -> true
                "0", "false" -> false
                else -> null
            }
        }

    else -> null
}

private fun JsonObject.parseChannel(name: String): AlertNotificationChannel =
    obj(name)?.let(::parseAlertNotificationChannel) ?: AlertNotificationChannel()

internal fun mapAlertEventSeverity(message: String): AlertSeverity {
    val normalized = message.trim().lowercase()
    return when {
        normalized.contains("sos") ||
            normalized.contains("alarm") ||
            normalized.contains("panic") ||
            normalized.contains("alert") -> AlertSeverity.Critical
        normalized.contains("idle") ||
            normalized.contains("offline") ||
            normalized.contains("stop") ||
            normalized.contains("zone") -> AlertSeverity.Warning
        else -> AlertSeverity.Info
    }
}
