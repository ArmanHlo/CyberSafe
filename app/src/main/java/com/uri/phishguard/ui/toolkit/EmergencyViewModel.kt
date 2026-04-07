package com.uri.phishguard.ui.toolkit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EmergencyContact(
    val title: String,
    val description: String,
    val phoneNumber: String?,
    val website: String?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
)

data class EmergencyUiState(
    val contacts: List<EmergencyContact> = emptyList()
)

class EmergencyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        val contacts = listOf(
            EmergencyContact(
                title = "National Cybercrime Helpline",
                description = "Immediate assistance for financial fraud and cybercrime reporting.",
                phoneNumber = "1930",
                website = "https://cybercrime.gov.in"
            ),
            EmergencyContact(
                title = "Cybercrime Reporting Portal",
                description = "Official Government of India portal to report all types of cybercrimes.",
                phoneNumber = null,
                website = "https://cybercrime.gov.in"
            ),
            EmergencyContact(
                title = "CERT-In (Indian Computer Emergency Response Team)",
                description = "For reporting security incidents like hacking and phishing.",
                phoneNumber = "1800-11-4433",
                website = "https://www.cert-in.org.in"
            ),
            EmergencyContact(
                title = "Women Helpline",
                description = "Support for women facing online harassment or cyberstalking.",
                phoneNumber = "1091",
                website = null
            )
        )
        _uiState.value = EmergencyUiState(contacts = contacts)
    }
}
