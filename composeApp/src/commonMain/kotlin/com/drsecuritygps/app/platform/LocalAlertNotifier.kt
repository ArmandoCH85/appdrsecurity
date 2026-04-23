package com.drsecuritygps.app.platform

import com.drsecuritygps.app.core.model.AlertEventItem

/** Notificaciones locales para eventos nuevos (diff en SQLite). No requiere FCM ni Wox. */
fun interface LocalAlertNotifier {
    fun showNewAlertEvents(events: List<AlertEventItem>)
}

object NoOpLocalAlertNotifier : LocalAlertNotifier {
    override fun showNewAlertEvents(events: List<AlertEventItem>) = Unit
}
