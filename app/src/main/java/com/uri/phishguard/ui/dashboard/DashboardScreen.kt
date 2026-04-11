package com.uri.phishguard.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uri.phishguard.ui.navigation.Screen
import com.uri.phishguard.ui.theme.NeonGreen
import com.uri.phishguard.ui.theme.WarningOrange
import com.uri.phishguard.ui.theme.DangerRed
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToFeature: (String) -> Unit
) {
    var showQuickScanSheet by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickScanSheet = true },
                containerColor = NeonGreen,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                // Added padding to move the button UP so it doesn't hit the nav bar
                modifier = Modifier.padding(bottom = 90.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Quick Scan")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item { 
                EmergencyBanner() 
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cyber Safety Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Stay protected from digital threats",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                val features = listOf(
                    DashboardItem("Text Scanner", Icons.Default.Sms, Screen.TextScanner.route, NeonGreen),
                    DashboardItem("Deepfake Detector", Icons.Default.Face, Screen.DeepfakeDetector.route, Color.Cyan),
                    DashboardItem("Password Guard", Icons.Default.Lock, Screen.PasswordGuard.route, WarningOrange),
                    DashboardItem("URL Scanner", Icons.Default.Link, Screen.UrlScanner.route, NeonGreen)
                )

                Box(modifier = Modifier.height(300.dp).padding(horizontal = 8.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        userScrollEnabled = false,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(features) { feature ->
                            FeatureCard(feature, onNavigateToFeature)
                        }
                    }
                }
            }

            // Cyber Tips Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Daily Security Tips",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                CyberTipsPager(uiState.cyberTips)
            }

            // Scam News Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Trending Scam Alerts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(uiState.scamNews) { news ->
                ScamNewsCard(news)
            }
            
            item { Spacer(modifier = Modifier.height(120.dp)) }
        }

        if (showQuickScanSheet) {
            ModalBottomSheet(
                onDismissRequest = { showQuickScanSheet = false },
                sheetState = sheetState
            ) {
                QuickScanContent(
                    onOptionSelected = { route ->
                        showQuickScanSheet = false
                        onNavigateToFeature(route)
                    }
                )
            }
        }
    }
}

@Composable
fun CyberTipsPager(tips: List<CyberTip>) {
    val pagerState = rememberPagerState(pageCount = { tips.size })
    
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val tip = tips[page]
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = tip.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tip.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ScamNewsCard(news: ScamNews) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = news.title, fontWeight = FontWeight.Bold, color = DangerRed, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = news.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = NeonGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Protection: ${news.howToAvoid}",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen
                )
            }
        }
    }
}

@Composable
fun QuickScanContent(onOptionSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        QuickScanItem("Paste Text", Icons.Default.ContentPaste, NeonGreen) {
            onOptionSelected(Screen.TextScanner.route)
        }
        QuickScanItem("Pick Image", Icons.Default.Image, Color.Cyan) {
            onOptionSelected(Screen.DeepfakeDetector.route)
        }
        QuickScanItem("Scan QR Code", Icons.Default.QrCodeScanner, WarningOrange) {
            onOptionSelected(Screen.QRScanner.route)
        }
        QuickScanItem("Check URL", Icons.Default.Link, NeonGreen) {
            onOptionSelected(Screen.UrlScanner.route)
        }
    }
}

@Composable
fun QuickScanItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EmergencyBanner() {
    val alerts = listOf(
        "⚠️ Fake UPI refund scams spreading. Never share OTP!",
        "🚨 KYC update SMS from unknown numbers are frauds.",
        "🛑 Bank will never ask for your password via WhatsApp.",
        "📱 Beware of AI voice clones requesting money."
    )
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % alerts.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DangerRed)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = alerts[currentIndex],
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
            },
            label = "AlertBanner"
        ) { alert ->
            Text(
                text = alert,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FeatureCard(feature: DashboardItem, onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(feature.route) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = feature.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = feature.color,
                    modifier = Modifier.padding(12.dp).size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = feature.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class DashboardItem(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)
