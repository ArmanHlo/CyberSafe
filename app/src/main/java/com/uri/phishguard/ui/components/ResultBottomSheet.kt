package com.uri.phishguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.model.TextScanResult
import com.uri.phishguard.data.model.ImageScanResult
import com.uri.phishguard.data.model.UrlScanResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheet(
    scanResult: ScanResult,
    onDismiss: () -> Unit,
    onSaveToHistory: () -> Unit = {},
    onShareReport: () -> Unit = {}
) {
    val gson = remember { Gson() }
    
    // Attempt to parse the detailed report if it's JSON
    val parsedData = remember(scanResult.detailedReport) {
        try {
            when (scanResult.type) {
                "TEXT" -> gson.fromJson(scanResult.detailedReport, TextScanResult::class.java)
                "IMAGE" -> gson.fromJson(scanResult.detailedReport, ImageScanResult::class.java)
                "URL" -> gson.fromJson(scanResult.detailedReport, UrlScanResult::class.java)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color(0xFF0A192F), // Deep Navy
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val threatColor = getThreatColor(scanResult.threatLevel)
            val threatIcon = getThreatIcon(scanResult.threatLevel)

            // Header: Verdict and Confidence
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = threatIcon,
                    contentDescription = null,
                    tint = threatColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = scanResult.threatLevel.name,
                        color = threatColor,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    val confidence = when (parsedData) {
                        is TextScanResult -> parsedData.confidence
                        is ImageScanResult -> parsedData.confidence
                        is UrlScanResult -> parsedData.confidence
                        else -> 0
                    }
                    if (confidence > 0) {
                        Text(
                            text = "$confidence% Confidence",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scam Type / Summary
            Text(
                text = scanResult.resultSummary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Explanation
            val explanation = when (parsedData) {
                is TextScanResult -> parsedData.explanation
                is ImageScanResult -> parsedData.simpleExplanation
                is UrlScanResult -> parsedData.explanation
                else -> scanResult.detailedReport
            }
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = Color.LightGray
            )

            // Red Flags
            val redFlags = when (parsedData) {
                is TextScanResult -> parsedData.redFlags
                is UrlScanResult -> parsedData.redFlags
                else -> emptyList()
            }
            
            if (redFlags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Red Flags Identified",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336) // Danger Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                redFlags.forEach { flag ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = flag,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }

            // Action to Take
            val action = when (parsedData) {
                is TextScanResult -> parsedData.actionToTake
                is UrlScanResult -> parsedData.recommendation
                is ImageScanResult -> if (parsedData.shouldTrustThisImage) "Safe to proceed." else "Do not trust or share this image."
                else -> "Proceed with extreme caution."
            }

            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                color = Color(0xFF1E293B), // Slightly lighter navy
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Action to take",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00FF00) // Neon Green
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = action,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSaveToHistory,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Gray))
                ) {
                    Icon(Icons.Default.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("History")
                }
                Button(
                    onClick = onShareReport,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00), contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
private fun getThreatColor(threatLevel: ThreatLevel): Color {
    return when (threatLevel) {
        ThreatLevel.SAFE -> Color(0xFF00FF00) // Neon Green
        ThreatLevel.LOW -> Color(0xFF8BC34A)
        ThreatLevel.MEDIUM -> Color(0xFFFF9800) // Warning Orange
        ThreatLevel.HIGH, ThreatLevel.CRITICAL -> Color(0xFFF44336) // Danger Red
        else -> Color.Gray
    }
}

private fun getThreatIcon(threatLevel: ThreatLevel): ImageVector {
    return when (threatLevel) {
        ThreatLevel.SAFE -> Icons.Default.CheckCircle
        ThreatLevel.LOW -> Icons.Default.Shield
        ThreatLevel.MEDIUM -> Icons.Default.Warning
        ThreatLevel.HIGH, ThreatLevel.CRITICAL -> Icons.Default.Error
        else -> Icons.Default.Help
    }
}
