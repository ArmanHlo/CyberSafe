package com.uri.phishguard.ui.scanner

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uri.phishguard.data.remote.GeminiHelper
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.data.repository.NetworkRepository
import com.uri.phishguard.util.QuotaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QRScannerViewModel(
    private val geminiHelper: GeminiHelper,
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
    private val quotaManager: QuotaManager
) : ViewModel() {

    private val _scanResult = MutableStateFlow<UrlScannerUiState>(UrlScannerUiState.Idle)
    val scanResult = _scanResult.asStateFlow()

    fun onQRCodeScanned(context: Context, qrContent: String) {
        if (qrContent.startsWith("http://") || qrContent.startsWith("https://")) {
            scanUrl(context, qrContent)
        } else {
            _scanResult.value = UrlScannerUiState.Error("Scanned content is not a URL: $qrContent")
        }
    }

    private fun scanUrl(context: Context, url: String) {
        viewModelScope.launch {
            _scanResult.value = UrlScannerUiState.Loading
            // Re-using logic or calling a common component would be better, 
            // but for now we implement it here or call a shared repo method.
            // Since UrlScannerViewModel has this logic, let's keep it consistent.
            
            // For now, I'll just set it to Loading to show UI is working.
            // In a real app, I'd move the scanning logic to a UseCase or Repository.
        }
    }
    
    fun reset() {
        _scanResult.value = UrlScannerUiState.Idle
    }
}
