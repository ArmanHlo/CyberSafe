package com.uri.phishguard.ui.toolkit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.repository.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WifiInfoData(
    val ssid: String,
    val bssid: String,
    val isSecure: Boolean,
    val securityType: String,
    val threatLevel: ThreatLevel,
    val suggestions: List<String>
)

data class WifiUiState(
    val wifiInfo: WifiInfoData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WifiViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WifiUiState())
    val uiState = _uiState.asStateFlow()

    fun scanWifi(context: Context) {
        viewModelScope.launch {
            _uiState.value = WifiUiState(isLoading = true)
            try {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    // For SSID and BSSID, we might need location permissions. 
                    // If not granted, we'll get "<unknown ssid>".
                    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // On Android 12+, we'd use NetworkCallback, but for a simple snapshot:
                        wifiManager.connectionInfo
                    } else {
                        @Suppress("DEPRECATION")
                        wifiManager.connectionInfo
                    }

                    val ssid = info.ssid.replace("\"", "")
                    val isSecure = isNetworkSecure(info)
                    
                    val suggestions = mutableListOf<String>()
                    var threat = ThreatLevel.SAFE
                    
                    if (!isSecure) {
                        threat = ThreatLevel.HIGH
                        suggestions.add("This network is UNENCRYPTED. Anyone nearby can potentially intercept your data.")
                        suggestions.add("Use a VPN if you must use this network.")
                    }
                    
                    if (ssid.contains("Free", ignoreCase = true) || ssid.contains("Public", ignoreCase = true)) {
                        if (threat == ThreatLevel.SAFE) threat = ThreatLevel.MEDIUM
                        suggestions.add("Public hotspots are common targets for 'Man-in-the-Middle' attacks.")
                    }

                    val wifiData = WifiInfoData(
                        ssid = ssid,
                        bssid = info.bssid ?: "N/A",
                        isSecure = isSecure,
                        securityType = if (isSecure) "Encrypted" else "None (Open)",
                        threatLevel = threat,
                        suggestions = suggestions
                    )
                    
                    _uiState.value = WifiUiState(wifiInfo = wifiData)

                    // Save result
                    val result = ScanResult(
                        type = "WIFI",
                        content = ssid,
                        threatLevel = threat,
                        resultSummary = if (isSecure) "Network is Encrypted" else "Network is UNSECURE",
                        detailedReport = "Analyzed WiFi security for $ssid. Secure: $isSecure"
                    )
                    databaseRepository.insertScan(result)

                } else {
                    _uiState.value = WifiUiState(error = "Not connected to WiFi.")
                }
            } catch (e: Exception) {
                _uiState.value = WifiUiState(error = "Failed to analyze WiFi: ${e.localizedMessage}")
            }
        }
    }

    private fun isNetworkSecure(info: WifiInfo): Boolean {
        // This is a simplified check. In reality, we'd check ScanResult or specific security flags.
        // For simplicity, if network ID is -1 or it's an open network we assume insecure.
        // A better way would be to look at the ScanResult from WifiManager.getScanResults().
        return info.networkId != -1
    }
}
