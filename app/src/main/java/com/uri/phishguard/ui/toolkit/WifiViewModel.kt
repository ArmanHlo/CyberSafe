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
                    val info: WifiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        wifiManager.connectionInfo
                    } else {
                        @Suppress("DEPRECATION")
                        wifiManager.connectionInfo
                    }

                    var ssid = info.ssid.replace("\"", "")
                    if (ssid == "<unknown ssid>") {
                        _uiState.value = WifiUiState(error = "SSID is hidden. Please ensure Location (GPS) is turned ON in system settings.")
                        return@launch
                    }

                    // Better security detection using scan results
                    val scanResults = wifiManager.scanResults
                    val currentScan = scanResults.find { it.BSSID == info.bssid }
                    
                    val securityType: String
                    val isSecure: Boolean
                    
                    if (currentScan != null) {
                        val caps = currentScan.capabilities
                        securityType = caps
                        isSecure = !(caps.contains("OPEN", true) || caps.isEmpty())
                    } else {
                        // Fallback logic
                        isSecure = info.networkId != -1
                        securityType = if (isSecure) "Encrypted (Assumed)" else "Open / Unsecured"
                    }
                    
                    val suggestions = mutableListOf<String>()
                    var threat = ThreatLevel.SAFE
                    
                    if (!isSecure) {
                        threat = ThreatLevel.HIGH
                        suggestions.add("This network is UNENCRYPTED. Anyone nearby can potentially intercept your data.")
                        suggestions.add("Use a VPN if you must use this network.")
                    } else if (securityType.contains("WEP", true)) {
                        threat = ThreatLevel.HIGH
                        suggestions.add("This network uses WEP, which is obsolete and easily hacked.")
                        suggestions.add("Upgrade your router security to WPA2 or WPA3.")
                    }
                    
                    if (ssid.contains("Free", ignoreCase = true) || ssid.contains("Public", ignoreCase = true)) {
                        if (threat == ThreatLevel.SAFE) threat = ThreatLevel.MEDIUM
                        suggestions.add("Public hotspots are frequent targets for Man-in-the-Middle attacks.")
                    }

                    val wifiData = WifiInfoData(
                        ssid = ssid,
                        bssid = info.bssid ?: "N/A",
                        isSecure = isSecure,
                        securityType = securityType,
                        threatLevel = threat,
                        suggestions = suggestions
                    )
                    
                    _uiState.value = WifiUiState(wifiInfo = wifiData)

                    // Save result to history
                    val result = ScanResult(
                        type = "WIFI",
                        content = ssid,
                        threatLevel = threat,
                        resultSummary = if (isSecure) "Secure ($securityType)" else "Insecure Network",
                        detailedReport = "Analyzed WiFi security for $ssid. BSSID: ${info.bssid}. Threat Level: ${threat.name}"
                    )
                    databaseRepository.insertScan(result)

                } else {
                    _uiState.value = WifiUiState(error = "Not connected to WiFi. Please connect and try again.")
                }
            } catch (e: Exception) {
                _uiState.value = WifiUiState(error = "Failed to analyze WiFi: ${e.localizedMessage}")
            }
        }
    }
}
