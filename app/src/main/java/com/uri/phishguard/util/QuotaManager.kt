package com.uri.phishguard.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

val Context.dataStore by preferencesDataStore(name = "quota_prefs")

class QuotaManager(private val context: Context) {
    private val DAILY_LIMIT = 15 // Example limit for free tier
    private val USAGE_KEY = intPreferencesKey("gemini_usage")
    private val LAST_RESET_KEY = longPreferencesKey("last_reset_timestamp")

    val remainingQuota: Flow<Int> = context.dataStore.data.map { prefs ->
        val lastReset = prefs[LAST_RESET_KEY] ?: 0L
        if (isNewDay(lastReset)) {
            resetQuota()
            DAILY_LIMIT
        } else {
            DAILY_LIMIT - (prefs[USAGE_KEY] ?: 0)
        }
    }

    suspend fun incrementUsage() {
        context.dataStore.edit { prefs ->
            val currentUsage = prefs[USAGE_KEY] ?: 0
            prefs[USAGE_KEY] = currentUsage + 1
        }
    }

    private suspend fun resetQuota() {
        context.dataStore.edit { prefs ->
            prefs[USAGE_KEY] = 0
            prefs[LAST_RESET_KEY] = System.currentTimeMillis()
        }
    }

    private fun isNewDay(lastReset: Long): Boolean {
        val lastResetCal = Calendar.getInstance().apply { timeInMillis = lastReset }
        val nowCal = Calendar.getInstance()
        return lastResetCal.get(Calendar.DAY_OF_YEAR) != nowCal.get(Calendar.DAY_OF_YEAR) ||
                lastResetCal.get(Calendar.YEAR) != nowCal.get(Calendar.YEAR)
    }
}
