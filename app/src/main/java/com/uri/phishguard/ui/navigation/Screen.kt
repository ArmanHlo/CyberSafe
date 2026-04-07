package com.uri.phishguard.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    
    // Main Bottom Nav Screens
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object History : Screen("history", "History", Icons.Default.History)
    object Toolkit : Screen("toolkit", "Toolkit", Icons.Default.Security)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    // Feature Screens
    object TextScanner : Screen("text_scanner")
    object DeepfakeDetector : Screen("deepfake_detector")
    object UrlScanner : Screen("url_scanner")
    object PasswordGuard : Screen("password_guard")
    object EmailBreach : Screen("email_breach")
    object PermissionAnalyzer : Screen("permission_analyzer")
    object WifiSafety : Screen("wifi_safety")
    object QRScanner : Screen("qr_scanner")
}
