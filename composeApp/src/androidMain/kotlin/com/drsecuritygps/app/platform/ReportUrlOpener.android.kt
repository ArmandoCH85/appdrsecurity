package com.drsecuritygps.app.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

actual object ReportUrlOpener {
    private var appContext: Context? = null

    fun install(context: Context) {
        appContext = context.applicationContext
    }

    actual fun open(url: String): Boolean {
        val context = appContext ?: return false
        val normalizedUrl = url.trim()
        if (normalizedUrl.isBlank()) return false

        return runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizedUrl))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        }.getOrElse { error ->
            when (error) {
                is ActivityNotFoundException -> false
                else -> false
            }
        }
    }
}
