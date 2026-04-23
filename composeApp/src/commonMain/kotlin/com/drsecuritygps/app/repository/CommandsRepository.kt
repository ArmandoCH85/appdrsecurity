package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.CommandRequest
import com.drsecuritygps.app.core.model.CommandTemplate
import com.drsecuritygps.app.network.DrSecurityApi

class CommandsRepository(private val api: DrSecurityApi) {
    suspend fun getDeviceCommands(deviceId: String): List<CommandTemplate> = api.getDeviceCommands(deviceId)

    suspend fun sendCommand(template: CommandTemplate, request: CommandRequest) {
        when (template.connection) {
            com.drsecuritygps.app.core.model.CommandConnection.Gprs -> api.sendGprsCommand(request)
            com.drsecuritygps.app.core.model.CommandConnection.Sms -> api.sendSmsCommand(request)
            com.drsecuritygps.app.core.model.CommandConnection.Unknown ->
                error("La API no informó el canal real del comando.")
        }
    }
}
