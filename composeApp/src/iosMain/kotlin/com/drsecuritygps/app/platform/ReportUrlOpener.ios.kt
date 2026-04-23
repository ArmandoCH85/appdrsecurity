package com.drsecuritygps.app.platform

actual object ReportUrlOpener {
    actual fun open(url: String): Boolean = false
}
