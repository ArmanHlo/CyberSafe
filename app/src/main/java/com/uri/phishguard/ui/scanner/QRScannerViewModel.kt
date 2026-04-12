package com.uri.phishguard.ui.scanner

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafeBrowsingThreat
import com.google.gson.Gson
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.remote.GeminiHelper
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.data.repository.NetworkRepository
import com.uri.phishguard.util.QuotaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QRScannerViewModel(
    private val geminiHelper: GeminiHelper,
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
    private val quotaManager: QuotaManager
) : ViewModel() {

    private val _scanResult = MutableStateFlow<UrlScannerUiState>(UrlScannerUiState.Idle)
    val scanResult = _scanResult.asStateFlow()
    
    private val gson = Gson()

    fun onQRCodeScanned(context: Context, qrContent: String) {
        if (qrContent.startsWith("http://") || qrContent.startsWith("https://") || qrContent.contains("www.")) {
            val url = if (!qrContent.startsWith("http")) "https://$qrContent" else qrContent
            scanUrl(context, url)
        } else {
            _scanResult.value = UrlScannerUiState.Error("Scanned content is not a URL: $qrContent")
        }
    }

    private fun scanUrl(context: Context, url: String) {
        viewModelScope.launch {
            _scanResult.value = UrlScannerUiState.Loading

            try {
                // Layer 1: SafetyNet
                val safetyNetSafe = checkSafetyNet(context, url)

                // Layer 2: VirusTotal
                val vtResult = try {
                    networkRepository.scanUrl(url)
                } catch (e: Exception) {
                    null
                }

                // Layer 3: Gemini
                val quota = quotaManager.remainingQuota.first()
                if (quota <= 0) {
                    _scanResult.value = UrlScannerUiState.Error("Daily quota exceeded. Please try again tomorrow.")
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
                        type = "QR_URL",
                        content = url,
                        threatLevel = threatLevel,
                        resultSummary = scanResult.urlVerdict,
                        detailedReport = gson.toJson(scanResult)
                    )
                    databaseRepository.insertScan(dbScan)

                    _scanResult.value = UrlScannerUiState.Success(
                        result = scanResult,
                        vtReport = vtResult?.toString(),
                        safetyNetSafe = safetyNetSafe
                    )
                } else {
                    _scanResult.value = UrlScannerUiState.Error("Failed to parse analysis result.")
                }

            } catch (e: Exception) {
                _scanResult.value = UrlScannerUiState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun checkSafetyNet(context: Context, url: String): Boolean {
        return try {
            val client = SafetyNet.getClient(context)
            val response = client.lookupUri(
                url,
                "", // Use default or from BuildConfig
                SafeBrowsingThreat.TYPE_POTENTIALLY_HARMFUL_APPLICATION,
                SafeBrowsingThreat.TYPE_SOCIAL_ENGINEERING
            ).await()
            
            response.detectedThreats.isEmpty()
        } catch (e: Exception) {
            true // Fallback
        }
    }
    
    fun reset() {
        _scanResult.value = UrlScannerUiState.Idle
    }
}
