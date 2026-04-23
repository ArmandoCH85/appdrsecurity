package com.drsecuritygps.app.platform

expect object ReportUrlOpener {
    fun open(url: String): Boolean
}
