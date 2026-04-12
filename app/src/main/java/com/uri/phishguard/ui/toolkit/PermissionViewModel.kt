package com.uri.phishguard.ui.toolkit

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppPermissionInfo(
    val appName: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable?,
    val dangerousPermissions: List<String>,
    val riskLevel: RiskLevel,
    val riskScore: Int
)

enum class RiskLevel {
    LOW, MEDIUM, HIGH
}

data class PermissionUiState(
    val apps: List<AppPermissionInfo> = emptyList(),
    val isLoading: Boolean = false
)

class PermissionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState = _uiState.asStateFlow()

    // Weighted permissions for a more "Premium" analysis
    private val permissionWeights = mapOf(
        "android.permission.READ_SMS" to 40,
        "android.permission.RECEIVE_SMS" to 40,
        "android.permission.READ_CALL_LOG" to 35,
        "android.permission.READ_CONTACTS" to 30,
        "android.permission.RECORD_AUDIO" to 30,
        "android.permission.CAMERA" to 25,
        "android.permission.ACCESS_FINE_LOCATION" to 20,
        "android.permission.ACCESS_COARSE_LOCATION" to 10,
        "android.permission.PROCESS_OUTGOING_CALLS" to 35
    )

    fun scanApps(context: Context) {
        viewModelScope.launch {
            _uiState.value = PermissionUiState(isLoading = true)
            val apps = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val installedApps = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                
                installedApps.filter { packageInfo ->
                    val appInfo = packageInfo.applicationInfo
                    // Filter for user-installed apps only
                    appInfo != null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 
                }.map { packageInfo ->
                    val appInfo = packageInfo.applicationInfo!!
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    
                    val requested = packageInfo.requestedPermissions ?: emptyArray()
                    val dangerous = requested.filter { permissionWeights.containsKey(it) }
                    
                    // Calculate a nuanced risk score
                    var score = 0
                    dangerous.forEach { perm ->
                        score += permissionWeights[perm] ?: 0
                    }

                    // Refined Risk Leveling
                    val risk = when {
                        score >= 60 -> RiskLevel.HIGH
                        score >= 20 -> RiskLevel.MEDIUM
                        else -> RiskLevel.LOW
                    }
                    
                    AppPermissionInfo(
                        appName = appName,
                        packageName = packageInfo.packageName,
                        icon = pm.getApplicationIcon(appInfo),
                        dangerousPermissions = dangerous,
                        riskLevel = risk,
                        riskScore = score
                    )
                }.sortedByDescending { it.riskScore }
            }
            _uiState.value = PermissionUiState(apps = apps, isLoading = false)
        }
    }
}
