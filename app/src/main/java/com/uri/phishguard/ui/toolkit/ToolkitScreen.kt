package com.uri.phishguard.ui.toolkit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uri.phishguard.ui.dashboard.DashboardItem
import com.uri.phishguard.ui.dashboard.FeatureCard
import com.uri.phishguard.ui.navigation.Screen
import com.uri.phishguard.ui.theme.NeonGreen
import com.uri.phishguard.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolkitScreen(
    onNavigateToTool: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safety Toolkit", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val tools = listOf(
                DashboardItem("Email Breach", Icons.Default.Email, Screen.EmailBreach.route, Color(0xFF64B5F6)),
                DashboardItem("Permissions", Icons.Default.AppRegistration, Screen.PermissionAnalyzer.route, Color(0xFFBA68C8)),
                DashboardItem("WiFi Check", Icons.Default.Wifi, Screen.WifiSafety.route, NeonGreen),
                DashboardItem("Emergency SOS", Icons.Default.Emergency, "emergency", Color(0xFFE57373))
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tools) { tool ->
                    FeatureCard(tool, onNavigateToTool)
                }
            }
        }
    }
}
