package com.uri.phishguard.ui.textscan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.model.TextScanResult
import com.uri.phishguard.data.remote.GeminiHelper
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.util.QuotaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class TextScannerUiState {
    object Idle : TextScannerUiState()
    object Loading : TextScannerUiState()
    data class Success(val result: TextScanResult, val dbScan: ScanResult) : TextScannerUiState()
    data class Error(val message: String) : TextScannerUiState()
}

class TextScannerViewModel(
    private val geminiHelper: GeminiHelper,
    private val databaseRepository: DatabaseRepository,
    private val quotaManager: QuotaManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<TextScannerUiState>(TextScannerUiState.Idle)
    val uiState: StateFlow<TextScannerUiState> = _uiState.asStateFlow()

    fun analyzeText(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = TextScannerUiState.Loading

            try {
                val quota = quotaManager.remainingQuota.first()
                if (quota <= 0) {
                    _uiState.value = TextScannerUiState.Error("Daily quota exceeded. Please try again tomorrow.")
                    return@launch
                }

                val result = geminiHelper.analyzeScamText(text)

                if (result != null) {
                    val threatLevel = when (result.verdict.uppercase()) {
                        "SAFE" -> ThreatLevel.SAFE
                        "SUSPICIOUS" -> ThreatLevel.MEDIUM
                        "DANGEROUS" -> ThreatLevel.HIGH
                        else -> ThreatLevel.UNKNOWN
                    }

                    val dbScan = ScanResult(
                        type = "TEXT",
                        content = text,
                        threatLevel = threatLevel,
                        resultSummary = result.scamType,
                        detailedReport = result.explanation
                    )
                    databaseRepository.insertScan(dbScan)

                    _uiState.value = TextScannerUiState.Success(result, dbScan)
                } else {
                    _uiState.value = TextScannerUiState.Error("Failed to parse analysis result.")
                }
            } catch (e: Exception) {
                _uiState.value = TextScannerUiState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _uiState.value = TextScannerUiState.Idle
    }
}
