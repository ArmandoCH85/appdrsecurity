package com.drsecuritygps.app.storage

import app.cash.sqldelight.db.SqlDriver

interface SecureStorage {
    suspend fun getString(key: String): String?
    suspend fun putString(key: String, value: String)
    suspend fun remove(key: String)
}

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
