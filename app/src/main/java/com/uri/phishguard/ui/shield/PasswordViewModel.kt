package com.uri.phishguard.ui.shield

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.data.repository.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

data class PasswordStrength(
    val score: Int, // 0-100
    val label: String, // Weak, Fair, Strong, Very Strong
    val color: String, // Hex or name
    val suggestions: List<String>
)

data class PasswordUiState(
    val strength: PasswordStrength? = null,
    val breachCount: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PasswordViewModel(
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun analyzePassword(password: String) {
        if (password.isBlank()) {
            _uiState.value = PasswordUiState()
            return
        }

        val strength = calculateStrength(password)
        _uiState.value = _uiState.value.copy(strength = strength)
    }

    fun checkBreach(password: String) {
        if (password.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, breachCount = null, error = null)
            try {
                val fullHash = sha1(password)
                val first5 = fullHash.substring(0, 5).uppercase()
                val remaining = fullHash.substring(5).uppercase()

                val response = networkRepository.checkPasswordBreach(first5)
                val breachCount = parseBreachResponse(response, remaining)

                _uiState.value = _uiState.value.copy(isLoading = false, breachCount = breachCount)

                // Save to history
                val threatLevel = if (breachCount > 0) ThreatLevel.HIGH else ThreatLevel.SAFE
                val result = ScanResult(
                    type = "PASSWORD",
                    content = "Password Check",
                    threatLevel = threatLevel,
                    resultSummary = if (breachCount > 0) "Found in $breachCount breaches!" else "No breaches found.",
                    detailedReport = "Analyzed password strength and checked HIBP database. Strength Score: ${_uiState.value.strength?.score}"
                )
                databaseRepository.insertScan(result)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to check breach: ${e.localizedMessage}")
            }
        }
    }

    private fun calculateStrength(password: String): PasswordStrength {
        var score = 0
        val suggestions = mutableListOf<String>()

        if (password.length >= 8) score += 20 else suggestions.add("Use at least 8 characters")
        if (password.length >= 12) score += 10
        if (password.any { it.isUpperCase() }) score += 20 else suggestions.add("Add uppercase letters")
        if (password.any { it.isLowerCase() }) score += 10
        if (password.any { it.isDigit() }) score += 20 else suggestions.add("Add numbers")
        if (password.any { !it.isLetterOrDigit() }) score += 20 else suggestions.add("Add special characters")

        val (label, color) = when {
            score < 40 -> "Weak" to "#F44336"
            score < 70 -> "Fair" to "#FF9800"
            score < 90 -> "Strong" to "#8BC34A"
            else -> "Very Strong" to "#4CAF50"
        }

        return PasswordStrength(score, label, color, suggestions)
    }

    private fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun parseBreachResponse(response: String, remaining: String): Int {
        val lines = response.lines()
        for (line in lines) {
            val parts = line.split(":")
            if (parts.size == 2 && parts[0] == remaining) {
                return parts[1].trim().toInt()
            }
        }
        return 0
    }
}
