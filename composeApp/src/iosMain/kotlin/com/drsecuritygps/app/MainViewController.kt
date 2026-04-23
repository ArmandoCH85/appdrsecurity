package com.drsecuritygps.app

import androidx.compose.ui.window.ComposeUIViewController
import com.drsecuritygps.app.platform.IosDatabaseDriverFactory
import com.drsecuritygps.app.platform.IosSecureStorage
import com.drsecuritygps.app.presentation.AppGraph

fun MainViewController() = ComposeUIViewController {
    val graph = AppGraph(
        secureStorage = IosSecureStorage(),
        databaseDriverFactory = IosDatabaseDriverFactory(),
    )
    DrSecurityApp(graph.controller)
}
