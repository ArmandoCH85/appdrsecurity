package com.drsecuritygps.app.platform

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.drsecuritygps.app.core.model.AlertEventItem

class AndroidLocalAlertNotifier(
    private val context: Context,
) : LocalAlertNotifier {

    override fun showNewAlertEvents(events: List<AlertEventItem>) {
        if (events.isEmpty()) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alertas",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            nm.createNotificationChannel(channel)
        }
        for (event in events) {
            val body = buildText(event)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(event.deviceName.ifBlank { "Alerta" })
                .setContentText(event.message.ifBlank { body })
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            val id = (event.id.hashCode() and Int.MAX_VALUE)
            nm.notify(id, notification)
        }
    }

    private fun buildText(event: AlertEventItem): String = buildString {
        if (event.message.isNotBlank()) {
            append(event.message.trim())
        }
        if (event.address.isNotBlank()) {
            if (isNotEmpty()) append("\n\n")
            append(event.address.trim())
        }
        if (event.timestamp.isNotBlank()) {
            if (isNotEmpty()) append("\n\n")
            append(event.timestamp)
        }
        if (isEmpty()) append("Nuevo evento de alerta")
    }

    private companion object {
        const val CHANNEL_ID = "drsecurity_alerts"
    }
}
