package com.uri.phishguard.ui.toolkit

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
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
    val riskLevel: RiskLevel
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

    private val sensitivePermissions = listOf(
        "android.permission.READ_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.RECORD_AUDIO",
        "android.permission.CAMERA",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.READ_CONTACTS",
        "android.permission.READ_CALL_LOG",
        "android.permission.PROCESS_OUTGOING_CALLS"
    )

    fun scanApps(context: Context) {
        viewModelScope.launch {
            _uiState.value = PermissionUiState(isLoading = true)
            val apps = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val installedApps = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                
                installedApps.filter { packageInfo ->
                    val appInfo = packageInfo.applicationInfo
                    appInfo != null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 
                }.map { packageInfo ->
                    val appInfo = packageInfo.applicationInfo!!
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    val dangerous = packageInfo.requestedPermissions?.filter { 
                        sensitivePermissions.contains(it) 
                    } ?: emptyList()
                    
                    val risk = when {
                        dangerous.size >= 3 -> RiskLevel.HIGH
                        dangerous.isNotEmpty() -> RiskLevel.MEDIUM
                        else -> RiskLevel.LOW
                    }
                    
                    AppPermissionInfo(
                        appName = appName,
                        packageName = packageInfo.packageName,
                        icon = pm.getApplicationIcon(appInfo),
                        dangerousPermissions = dangerous,
                        riskLevel = risk
                    )
                }.sortedByDescending { it.riskLevel }
            }
            _uiState.value = PermissionUiState(apps = apps, isLoading = false)
        }
    }
}
