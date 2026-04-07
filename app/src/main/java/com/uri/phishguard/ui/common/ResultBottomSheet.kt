package com.uri.phishguard.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uri.phishguard.data.local.ThreatLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheet(
    onDismiss: () -> Unit,
    threatLevel: ThreatLevel,
    title: String,
    summary: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = getThreatColor(threatLevel)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = getThreatColor(threatLevel).copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Threat Level: ${threatLevel.name}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = getThreatColor(threatLevel)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            content()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Got it")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun getThreatColor(threatLevel: ThreatLevel): Color {
    return when (threatLevel) {
        ThreatLevel.SAFE -> Color(0xFF4CAF50) // Neon Green / Safe Green
        ThreatLevel.LOW -> Color(0xFF8BC34A)
        ThreatLevel.MEDIUM -> Color(0xFFFF9800) // Warning Orange
        ThreatLevel.HIGH -> Color(0xFFF44336) // Danger Red
        ThreatLevel.CRITICAL -> Color(0xFFB71C1C)
        ThreatLevel.UNKNOWN -> Color.Gray
    }
}
