package com.drsecuritygps.app.network

import com.drsecuritygps.app.core.model.Session
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Prueba de diagnóstico: imprime en consola una muestra de lo que devuelve `get_sensors` por dispositivo.
 *
 * Ejecutar con credenciales (PowerShell):
 *   $env:DRSECURITYGPS_TEST_EMAIL="tu@correo.com"
 *   $env:DRSECURITYGPS_TEST_PASSWORD="***"
 *   .\gradlew.bat :composeApp:testDebugUnitTest --tests "com.drsecuritygps.app.network.GetSensorsProbeTest" --info
 *
 * Sin variables de entorno el test se omite.
 */
class GetSensorsProbeTest {
    @Test
    fun printGetSensorsSampleForFirstDevices() = runTest {
        val email = System.getenv("DRSECURITYGPS_TEST_EMAIL")?.takeIf { it.isNotBlank() } ?: return@runTest
        val password = System.getenv("DRSECURITYGPS_TEST_PASSWORD")?.takeIf { it.isNotBlank() } ?: return@runTest

        var session: Session? = null
        val api = KtorDrSecurityApi(
            sessionProvider = { session },
            onUnauthorized = { },
        )
        session = api.login(email, password)
        val devices = api.getDevices().take(8)

        println("\n========== get_sensors probe (${devices.size} dispositivos) ==========")
        devices.forEach { d ->
            val rows = runCatching { api.getDeviceSensors(d.id) }.getOrElse { e ->
                println("device_id=${d.id} name=${d.name} ERROR: ${e.message}")
                return@forEach
            }
            println("--- id=${d.id} name=${d.name} filas=${rows.size} ---")
            rows.take(12).forEach { r ->
                println(
                    "  type=${r.type} tag=${r.tagName} name=${r.name} value=${r.value} unit=${r.unit} title=${r.typeTitle}",
                )
            }
            if (rows.size > 12) println("  ... (${rows.size - 12} más)")
        }
        println("========== fin probe ==========\n")
    }
}
