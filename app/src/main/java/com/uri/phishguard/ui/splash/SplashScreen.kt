package com.uri.phishguard.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.uri.phishguard.ui.theme.NeonGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "Alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            onNavigateToDashboard()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .blur(80.dp)
                .clip(CircleShape)
                .background(NeonGreen.copy(alpha = 0.15f))
                .alpha(alphaAnim)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            // Premium Shield Icon with Glow
            Box(contentAlignment = Alignment.Center) {
                // Outer glow
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .blur(12.dp)
                        .alpha(0.3f),
                    tint = NeonGreen
                )
                // Main Icon
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp),
                    tint = NeonGreen
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "PhishGuard",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "SECURE YOUR DIGITAL LIFE",
                style = MaterialTheme.typography.labelLarge,
                color = NeonGreen,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(0.8f)
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Minimal Loading Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = NeonGreen.copy(alpha = 0.5f),
                strokeWidth = 2.dp
            )
        }
    }
}

// Helper for the loading indicator if it was missing in the file
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: androidx.compose.ui.unit.Dp = 4.dp
) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth
    )
}
