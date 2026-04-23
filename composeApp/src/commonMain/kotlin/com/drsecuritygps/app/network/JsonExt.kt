package com.drsecuritygps.app.network

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

internal fun JsonObject.string(name: String): String? =
    this[name]?.jsonPrimitive?.contentOrNull?.takeIf { it != "null" }

internal fun JsonObject.int(name: String): Int? =
    this[name]?.jsonPrimitive?.intOrNull ?: this[name]?.jsonPrimitive?.contentOrNull?.toIntOrNull()

internal fun JsonObject.double(name: String): Double? =
    this[name]?.jsonPrimitive?.doubleOrNull ?: this[name]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()

internal fun JsonObject.long(name: String): Long? =
    this[name]?.jsonPrimitive?.longOrNull ?: this[name]?.jsonPrimitive?.contentOrNull?.toLongOrNull()

internal fun JsonObject.bool(name: String): Boolean? =
    this[name]?.jsonPrimitive?.booleanOrNull ?: this[name]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull()

internal fun JsonObject.obj(name: String): JsonObject? =
    this[name]?.takeUnless { it is JsonNull }?.jsonObject

internal fun JsonObject.array(name: String): JsonArray =
    (this[name] as? JsonArray) ?: this[name]?.jsonArray ?: JsonArray(emptyList())

internal fun JsonObject.arrayOrJsonString(name: String, json: Json): JsonArray {
    val element = this[name] ?: return JsonArray(emptyList())
    if (element is JsonArray) return element
    val content = (element as? JsonPrimitive)?.contentOrNull ?: return JsonArray(emptyList())
    return runCatching { json.parseToJsonElement(content).jsonArray }.getOrDefault(JsonArray(emptyList()))
}

internal fun JsonElement.objectOrNull(): JsonObject? = this as? JsonObject

internal fun JsonElement.stringValue(): String? = (this as? JsonPrimitive)?.contentOrNull
