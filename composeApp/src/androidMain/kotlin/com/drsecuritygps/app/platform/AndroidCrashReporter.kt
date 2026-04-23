package com.drsecuritygps.app.platform

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

object AndroidCrashReporter {
    private const val TAG = "DrSecurityCrash"
    private const val PREFS = "drsecurity_crash_reporter"
    private const val KEY_LAST_UI_STATE = "last_ui_state"
    private val installed = AtomicBoolean(false)

    fun install(context: Context) {
        if (!installed.compareAndSet(false, true)) return

        val appContext = context.applicationContext
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { persistCrash(appContext, thread, throwable) }
            previousHandler?.uncaughtException(thread, throwable) ?: run {
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(10)
            }
        }
    }

    fun recordUiState(context: Context, value: String) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_UI_STATE, value)
            .apply()
    }

    fun consumeLastCrash(context: Context): String? {
        val crashFile = crashFile(context.applicationContext)
        if (!crashFile.exists()) return null

        return runCatching {
            crashFile.readText().also { crashFile.delete() }
        }.getOrNull()
    }

    private fun persistCrash(context: Context, thread: Thread, throwable: Throwable) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val stackTrace = StringWriter().also { writer ->
            throwable.printStackTrace(PrintWriter(writer))
        }.toString()

        val report = buildString {
            appendLine("timestamp=${System.currentTimeMillis()}")
            appendLine("thread=${thread.name}")
            appendLine("android=${Build.VERSION.RELEASE} sdk=${Build.VERSION.SDK_INT}")
            appendLine("device=${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("last_ui_state=${prefs.getString(KEY_LAST_UI_STATE, "unknown")}")
            appendLine()
            append(stackTrace)
        }

        crashFile(context).apply {
            parentFile?.mkdirs()
            writeText(report)
        }

        Log.e(TAG, report)
    }

    private fun crashFile(context: Context): File =
        File(context.filesDir, "crash/last_crash.txt")
}
