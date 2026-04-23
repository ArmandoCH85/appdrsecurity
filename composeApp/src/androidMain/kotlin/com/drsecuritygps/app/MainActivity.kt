package com.drsecuritygps.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.drsecuritygps.app.platform.AndroidDatabaseDriverFactory
import com.drsecuritygps.app.platform.AndroidCrashReporter
import com.drsecuritygps.app.platform.AndroidSecureStorage
import com.drsecuritygps.app.platform.AndroidLocalAlertNotifier
import com.drsecuritygps.app.platform.ReportUrlOpener
import com.drsecuritygps.app.presentation.AppGraph
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0,
                )
            }
        }
        AndroidCrashReporter.install(this)
        ReportUrlOpener.install(this)

        try {
            val startupCrashReport = AndroidCrashReporter.consumeLastCrash(this)

            val graph = AppGraph(
                secureStorage = AndroidSecureStorage(this),
                databaseDriverFactory = AndroidDatabaseDriverFactory(this),
                localAlertNotifier = AndroidLocalAlertNotifier(this),
            )

            lifecycleScope.launch {
                graph.controller.state.collect { state ->
                    AndroidCrashReporter.recordUiState(
                        context = this@MainActivity,
                        value = "destination=${state.destination} tab=${state.currentTab} selectedDeviceId=${state.selectedDeviceId}",
                    )
                }
            }

            setContent {
                DrSecurityApp(
                    controller = graph.controller,
                    startupCrashReport = startupCrashReport,
                )
            }
        } catch (e: Throwable) {
            val trace = StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString()
            setContent {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "STARTUP ERROR:\n\n${e.message}\n\n$trace",
                            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
