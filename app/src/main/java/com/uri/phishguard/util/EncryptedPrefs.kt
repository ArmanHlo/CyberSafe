package com.uri.phishguard.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPrefs(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveApiKey(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getApiKey(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    companion object {
        const val VIRUSTOTAL_KEY = "vt_api_key"
        const val HIBP_KEY = "hibp_api_key"
    }
}
