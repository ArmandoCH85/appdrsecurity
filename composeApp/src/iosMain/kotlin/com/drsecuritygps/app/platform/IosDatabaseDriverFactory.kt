package com.drsecuritygps.app.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.drsecuritygps.app.storage.DatabaseDriverFactory
import com.drsecuritygps.app.storage.db.AppDatabase

class IosDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver = NativeSqliteDriver(AppDatabase.Schema, "drsecuritygps.db")
}
