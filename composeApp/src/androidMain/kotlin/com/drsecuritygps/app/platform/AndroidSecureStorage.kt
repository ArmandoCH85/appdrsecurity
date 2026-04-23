package com.drsecuritygps.app.platform

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.drsecuritygps.app.storage.SecureStorage

class AndroidSecureStorage(
    context: Context,
) : SecureStorage {
    private val prefs = createEncryptedPrefs(context)

    override suspend fun getString(key: String): String? = prefs.getString(key, null)

    override suspend fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override suspend fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    companion object {
        private const val TAG = "AndroidSecureStorage"
        private const val PREFS_NAME = "drsecuritygps_secure_prefs"

        private fun createEncryptedPrefs(context: Context): SharedPreferences {
            return try {
                buildEncryptedPrefs(context)
            } catch (e: Exception) {
                Log.w(TAG, "Encrypted prefs corrupted, resetting", e)
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit().clear().commit()
                buildEncryptedPrefs(context)
            }
        }

        private fun buildEncryptedPrefs(context: Context): SharedPreferences =
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
    }
}
