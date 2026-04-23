package com.drsecuritygps.app.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.drsecuritygps.app.storage.DatabaseDriverFactory
import com.drsecuritygps.app.storage.db.AppDatabase

class AndroidDatabaseDriverFactory(
    private val context: Context,
) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver = AndroidSqliteDriver(AppDatabase.Schema, context, "drsecuritygps.db")
}
