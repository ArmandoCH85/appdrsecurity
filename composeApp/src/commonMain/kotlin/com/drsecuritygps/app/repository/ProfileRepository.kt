package com.drsecuritygps.app.repository

import com.drsecuritygps.app.core.model.UserProfile
import com.drsecuritygps.app.network.DrSecurityApi

class ProfileRepository(private val api: DrSecurityApi) {
    suspend fun getProfile(): UserProfile = api.getUserProfile()

    suspend fun registerFcmToken(token: String, projectId: String? = null) {
        api.registerFcmToken(token, projectId)
    }
}
