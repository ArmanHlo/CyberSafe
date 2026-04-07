package com.uri.phishguard.ui.shield

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.remote.BreachResponse
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.data.repository.NetworkRepository
import com.uri.phishguard.util.EncryptedPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmailBreachUiState(
    val breaches: List<BreachResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchPerformed: Boolean = false
)

class EmailBreachViewModel(
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
    private val encryptedPrefs: EncryptedPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailBreachUiState())
    val uiState = _uiState.asStateFlow()

    fun checkEmail(email: String) {
        if (email.isBlank()) return

        val hibpKey = encryptedPrefs.getApiKey(EncryptedPrefs.HIBP_KEY)
        if (hibpKey.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "HIBP API Key missing in settings.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, searchPerformed = true)
            try {
                val breaches = networkRepository.checkEmailBreach(email, hibpKey)
                _uiState.value = _uiState.value.copy(isLoading = false, breaches = breaches)

                // Save to history
                val threatLevel = if (breaches.isNotEmpty()) ThreatLevel.HIGH else ThreatLevel.SAFE
                val result = ScanResult(
                    type = "EMAIL",
                    content = email,
                    threatLevel = threatLevel,
                    resultSummary = if (breaches.isNotEmpty()) "Found in ${breaches.size} breaches!" else "No breaches found.",
                    detailedReport = "Checked HIBP database for email exposure."
                )
                databaseRepository.insertScan(result)

            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404) {
                    _uiState.value = _uiState.value.copy(isLoading = false, breaches = emptyList())
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "API Error: ${e.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to check email: ${e.localizedMessage}")
            }
        }
    }
}
