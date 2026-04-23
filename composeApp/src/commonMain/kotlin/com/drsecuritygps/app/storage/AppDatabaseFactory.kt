package com.drsecuritygps.app.storage

import com.drsecuritygps.app.storage.db.AppDatabase

class AppDatabaseFactory(driverFactory: DatabaseDriverFactory) {
    val database: AppDatabase = AppDatabase(driverFactory.createDriver())
}
