package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.Session
import com.drsecuritygps.app.core.model.UserProfile
import com.drsecuritygps.app.network.DrSecurityApi
import com.drsecuritygps.app.storage.SessionStore

class AuthRepository(
    private val api: DrSecurityApi,
    private val sessionStore: SessionStore,
) {
    suspend fun restoreSession(): Session? = sessionStore.readSession()

    suspend fun login(email: String, password: String): Pair<Session, UserProfile> {
        val session = api.login(email, password)
        sessionStore.saveSession(session.copy(email = email))
        val profile = api.getUserProfile()
        sessionStore.saveSession(session.copy(email = profile.email.ifBlank { email }))
        return session to profile
    }

    suspend fun logout() {
        sessionStore.clearSession()
    }
}
