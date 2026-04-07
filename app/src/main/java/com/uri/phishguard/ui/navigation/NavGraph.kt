package com.uri.phishguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uri.phishguard.PhishGuardApplication
import com.uri.phishguard.ui.ViewModelFactory
import com.uri.phishguard.ui.splash.SplashScreen
import com.uri.phishguard.ui.dashboard.DashboardScreen
import com.uri.phishguard.ui.dashboard.DashboardViewModel
import com.uri.phishguard.ui.history.HistoryScreen
import com.uri.phishguard.ui.history.HistoryViewModel
import com.uri.phishguard.ui.toolkit.ToolkitScreen
import com.uri.phishguard.ui.settings.SettingsScreen
import com.uri.phishguard.ui.auth.LoginScreen
import com.uri.phishguard.ui.auth.AuthViewModel
import com.uri.phishguard.ui.scanner.TextScannerScreen
import com.uri.phishguard.ui.scanner.TextScannerViewModel
import com.uri.phishguard.ui.scanner.DeepfakeScannerScreen
import com.uri.phishguard.ui.scanner.DeepfakeViewModel
import com.uri.phishguard.ui.scanner.UrlScannerScreen
import com.uri.phishguard.ui.scanner.UrlScannerViewModel
import com.uri.phishguard.ui.scanner.QRScannerScreen
import com.uri.phishguard.ui.scanner.QRScannerViewModel
import com.uri.phishguard.ui.shield.PasswordScreen
import com.uri.phishguard.ui.shield.PasswordViewModel
import com.uri.phishguard.ui.shield.EmailBreachScreen
import com.uri.phishguard.ui.shield.EmailBreachViewModel
import com.uri.phishguard.ui.toolkit.PermissionScreen
import com.uri.phishguard.ui.toolkit.PermissionViewModel
import com.uri.phishguard.ui.toolkit.WifiScreen
import com.uri.phishguard.ui.toolkit.WifiViewModel
import com.uri.phishguard.ui.toolkit.EmergencyScreen
import com.uri.phishguard.ui.toolkit.EmergencyViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as PhishGuardApplication
    val factory = ViewModelFactory(application)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToFeature = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel(factory = factory)
            HistoryScreen(viewModel = historyViewModel)
        }
        composable(Screen.Toolkit.route) {
            ToolkitScreen(
                onNavigateToTool = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
        
        // Feature Screens
        composable(Screen.TextScanner.route) {
            val vm: TextScannerViewModel = viewModel(factory = factory)
            TextScannerScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.DeepfakeDetector.route) {
            val vm: DeepfakeViewModel = viewModel(factory = factory)
            DeepfakeScannerScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.UrlScanner.route) {
            val vm: UrlScannerViewModel = viewModel(factory = factory)
            UrlScannerScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.PasswordGuard.route) {
            val vm: PasswordViewModel = viewModel(factory = factory)
            PasswordScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.EmailBreach.route) {
            val vm: EmailBreachViewModel = viewModel(factory = factory)
            EmailBreachScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.PermissionAnalyzer.route) {
            val vm: PermissionViewModel = viewModel(factory = factory)
            PermissionScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.WifiSafety.route) {
            val vm: WifiViewModel = viewModel(factory = factory)
            WifiScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.QRScanner.route) {
            val vm: QRScannerViewModel = viewModel(factory = factory)
            QRScannerScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable("emergency") {
            val vm: EmergencyViewModel = viewModel(factory = factory)
            EmergencyScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}
