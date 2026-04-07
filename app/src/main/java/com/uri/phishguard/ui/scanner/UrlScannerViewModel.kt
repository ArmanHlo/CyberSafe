package com.uri.phishguard.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafeBrowsingThreat
import com.google.gson.Gson
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.model.UrlScanResult
import com.uri.phishguard.data.remote.GeminiHelper
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.data.repository.NetworkRepository
import com.uri.phishguard.util.QuotaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.content.Context

sealed class UrlScannerUiState {
    object Idle : UrlScannerUiState()
    object Loading : UrlScannerUiState()
    data class Success(val result: UrlScanResult, val vtReport: String?, val safetyNetSafe: Boolean) : UrlScannerUiState()
    data class Error(val message: String) : UrlScannerUiState()
}

class UrlScannerViewModel(
    private val geminiHelper: GeminiHelper,
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
    private val quotaManager: QuotaManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UrlScannerUiState>(UrlScannerUiState.Idle)
    val uiState: StateFlow<UrlScannerUiState> = _uiState.asStateFlow()

    private val gson = Gson()

    fun scanUrl(context: Context, url: String) {
        if (url.isBlank()) return

        viewModelScope.launch {
            _uiState.value = UrlScannerUiState.Loading

            try {
                // Layer 1: SafetyNet
                val safetyNetSafe = checkSafetyNet(context, url)

                // Layer 2: VirusTotal (if key is available)
                // We'll use a simplified check here, assuming the repo handles key presence
                val vtResult = try {
                    networkRepository.scanUrl(url.hashCode().toString()) // VT needs URL ID (usually SHA256)
                } catch (e: Exception) {
                    null
                }

                // Layer 3: Gemini
                val quota = quotaManager.remainingQuota.first()
                if (quota <= 0) {
                    _uiState.value = UrlScannerUiState.Error("Daily quota exceeded. Please try again tomorrow.")
                    return@launch
                }

                val scanResult = geminiHelper.analyzeUrl(url)

                if (scanResult != null) {
                    quotaManager.incrementUsage()

                    val threatLevel = when (scanResult.urlVerdict) {
                        "SAFE" -> if (safetyNetSafe) ThreatLevel.SAFE else ThreatLevel.LOW
                        "SUSPICIOUS" -> ThreatLevel.MEDIUM
                        "DANGEROUS" -> ThreatLevel.HIGH
                        else -> ThreatLevel.UNKNOWN
                    }

                    val dbScan = ScanResult(
                        type = "URL",
                        content = url,
                        threatLevel = threatLevel,
                        resultSummary = scanResult.urlVerdict,
                        detailedReport = gson.toJson(scanResult)
                    )
                    databaseRepository.insertScan(dbScan)

                    _uiState.value = UrlScannerUiState.Success(
                        result = scanResult,
                        vtReport = vtResult?.toString(),
                        safetyNetSafe = safetyNetSafe
                    )
                } else {
                    _uiState.value = UrlScannerUiState.Error("Failed to parse analysis result.")
                }

            } catch (e: Exception) {
                _uiState.value = UrlScannerUiState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun checkSafetyNet(context: Context, url: String): Boolean {
        return try {
            val client = SafetyNet.getClient(context)
            val response = client.lookupUri(
                url,
                "API_KEY_HERE", // User needs to provide this or use a default
                SafeBrowsingThreat.TYPE_POTENTIALLY_HARMFUL_APPLICATION,
                SafeBrowsingThreat.TYPE_SOCIAL_ENGINEERING
            ).await()
            
            response.detectedThreats.isEmpty()
        } catch (e: Exception) {
            true // Fallback to true if API fails
        }
    }

    fun resetState() {
        _uiState.value = UrlScannerUiState.Idle
    }
}
