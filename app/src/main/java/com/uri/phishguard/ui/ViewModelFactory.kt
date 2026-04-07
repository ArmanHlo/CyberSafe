package com.uri.phishguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uri.phishguard.PhishGuardApplication
import com.uri.phishguard.ui.auth.AuthViewModel
import com.uri.phishguard.ui.dashboard.DashboardViewModel
import com.uri.phishguard.ui.history.HistoryViewModel
import com.uri.phishguard.ui.scanner.DeepfakeViewModel
import com.uri.phishguard.ui.scanner.QRScannerViewModel
import com.uri.phishguard.ui.scanner.TextScannerViewModel
import com.uri.phishguard.ui.scanner.UrlScannerViewModel
import com.uri.phishguard.ui.shield.EmailBreachViewModel
import com.uri.phishguard.ui.shield.PasswordViewModel
import com.uri.phishguard.ui.toolkit.EmergencyViewModel
import com.uri.phishguard.ui.toolkit.PermissionViewModel
import com.uri.phishguard.ui.toolkit.WifiViewModel

class ViewModelFactory(private val application: PhishGuardApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(application.authRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(application.geminiHelper) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(application.databaseRepository) as T
            }
            modelClass.isAssignableFrom(TextScannerViewModel::class.java) -> {
                TextScannerViewModel(application.geminiHelper, application.databaseRepository, application.quotaManager) as T
            }
            modelClass.isAssignableFrom(UrlScannerViewModel::class.java) -> {
                UrlScannerViewModel(application.geminiHelper, application.networkRepository, application.databaseRepository, application.quotaManager) as T
            }
            modelClass.isAssignableFrom(DeepfakeViewModel::class.java) -> {
                DeepfakeViewModel(application.geminiHelper, application.databaseRepository, application.quotaManager) as T
            }
            modelClass.isAssignableFrom(PasswordViewModel::class.java) -> {
                PasswordViewModel(application.networkRepository, application.databaseRepository) as T
            }
            modelClass.isAssignableFrom(EmailBreachViewModel::class.java) -> {
                EmailBreachViewModel(application.networkRepository, application.databaseRepository, application.encryptedPrefs) as T
            }
            modelClass.isAssignableFrom(PermissionViewModel::class.java) -> {
                PermissionViewModel() as T
            }
            modelClass.isAssignableFrom(WifiViewModel::class.java) -> {
                WifiViewModel(application.databaseRepository) as T
            }
            modelClass.isAssignableFrom(EmergencyViewModel::class.java) -> {
                EmergencyViewModel() as T
            }
            modelClass.isAssignableFrom(QRScannerViewModel::class.java) -> {
                QRScannerViewModel(application.geminiHelper, application.networkRepository, application.databaseRepository, application.quotaManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
