package com.uri.phishguard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uri.phishguard.data.remote.GeminiHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScamNews(
    val title: String,
    val description: String,
    val howToAvoid: String
)

data class CyberTip(
    val title: String,
    val content: String
)

data class DashboardUiState(
    val scamNews: List<ScamNews> = emptyList(),
    val cyberTips: List<CyberTip> = emptyList(),
    val isLoadingNews: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(private val geminiHelper: GeminiHelper) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchScamNews()
        loadCyberTips()
    }

    private fun fetchScamNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingNews = true)
            try {
                // Mocking scam news - in production this could come from Gemini or an RSS feed
                val news = listOf(
                    ScamNews(
                        "Fake Electricity Bill SMS",
                        "Scammers send messages threatening to disconnect power if a 'pending' bill isn't paid via a link.",
                        "Never pay via SMS links. Use official apps like TNEB, BESCOM, or Tata Power."
                    ),
                    ScamNews(
                        "UPI Refund Fraud",
                        "A stranger 'accidentally' sends money to your UPI and asks you to send it back via a link.",
                        "Don't click links for refunds. UPI only requires a PIN for SENDING, never for receiving."
                    ),
                    ScamNews(
                        "Job Offer WhatsApp Scam",
                        "High-paying work-from-home jobs offered via international WhatsApp numbers (+44, +1).",
                        "Legitimate companies don't recruit via unsolicited WhatsApp messages."
                    )
                )
                _uiState.value = _uiState.value.copy(scamNews = news, isLoadingNews = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingNews = false, error = e.localizedMessage)
            }
        }
    }

    private fun loadCyberTips() {
        val tips = listOf(
            CyberTip("Use 2FA", "Enable Two-Factor Authentication on all your accounts for an extra layer of security."),
            CyberTip("Check URLs", "Always check the URL of a website before entering sensitive information like passwords."),
            CyberTip("Update Software", "Keep your operating system and apps updated to protect against known vulnerabilities."),
            CyberTip("Beware of Phishing", "Be cautious of unsolicited emails or messages asking for personal information."),
            CyberTip("Strong Passwords", "Use unique, complex passwords for each of your online accounts."),
            CyberTip("Public WiFi", "Avoid using public WiFi for banking or sensitive transactions. Use a VPN if necessary.")
        )
        _uiState.value = _uiState.value.copy(cyberTips = tips)
    }
}
