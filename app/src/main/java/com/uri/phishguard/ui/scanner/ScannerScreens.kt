package com.uri.phishguard.ui.scanner

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uri.phishguard.data.model.ImageScanResult
import com.uri.phishguard.data.model.TextScanResult
import com.uri.phishguard.data.model.UrlScanResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextScannerScreen(
    viewModel: TextScannerViewModel,
    onBack: () -> Unit
) {
    var textToAnalyze by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Text Scanner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = textToAnalyze,
                onValueChange = { textToAnalyze = it },
                label = { Text("Paste message here") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                leadingIcon = { Icon(Icons.Default.ContentPaste, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.analyzeText(textToAnalyze) },
                modifier = Modifier.fillMaxWidth(),
                enabled = textToAnalyze.isNotBlank() && uiState !is TextScannerUiState.Loading
            ) {
                if (uiState is TextScannerUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Analyze with AI")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is TextScannerUiState.Success -> TextResultCard(state.result)
                is TextScannerUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                else -> {}
            }
        }
    }
}

@Composable
fun TextResultCard(result: TextScanResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (result.verdict) {
                "SAFE" -> Color(0xFFE8F5E9)
                "SUSPICIOUS" -> Color(0xFFFFF3E0)
                "DANGEROUS" -> Color(0xFFFFEBEE)
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = Color(0xFF1A1C1E)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Verdict: ${result.verdict}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = when (result.verdict) {
                    "SAFE" -> Color(0xFF2E7D32)
                    "SUSPICIOUS" -> Color(0xFFEF6C00)
                    "DANGEROUS" -> Color(0xFFC62828)
                    else -> Color.Black
                }
            )
            Text("Confidence: ${result.confidence}%", style = MaterialTheme.typography.bodyMedium)
            Text("Scam Type: ${result.scamType}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Explanation:", fontWeight = FontWeight.Bold)
            Text(result.explanation)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Action to take:", fontWeight = FontWeight.Bold)
            Text(result.actionToTake)
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Red Flags:", fontWeight = FontWeight.Bold)
            result.redFlags.forEach { flag ->
                Text("• $flag", fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepfakeScannerScreen(
    viewModel: DeepfakeViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            viewModel.analyzeImage(bitmap)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deepfake Detector") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image from Gallery")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is DeepfakeUiState.Loading -> CircularProgressIndicator()
                is DeepfakeUiState.Success -> ImageResultCard(state.result)
                is DeepfakeUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                else -> {}
            }
        }
    }
}

@Composable
fun ImageResultCard(result: ImageScanResult) {
    val scrollState = rememberScrollState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (result.verdict) {
                "AUTHENTIC" -> Color(0xFFE8F5E9)
                "UNCERTAIN" -> Color(0xFFFFF3E0)
                else -> Color(0xFFFFEBEE)
            },
            contentColor = Color(0xFF1A1C1E)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Verdict: ${result.verdict}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = when (result.verdict) {
                    "AUTHENTIC" -> Color(0xFF2E7D32)
                    "UNCERTAIN" -> Color(0xFFEF6C00)
                    else -> Color(0xFFC62828)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Probabilities:", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text("• Real: ${result.isRealProbability}%", color = Color(0xFF1A1C1E))
            Text("• AI Generated: ${result.aiGeneratedProbability}%", color = Color(0xFF1A1C1E))
            Text("• Deepfake: ${result.deepfakeProbability}%", color = Color(0xFF1A1C1E))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Analysis:", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text(result.simpleExplanation, color = Color(0xFF1A1C1E))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Risk Level: ${result.useCaseRisk}", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlScannerScreen(
    viewModel: UrlScannerViewModel,
    onBack: () -> Unit
) {
    var urlToScan by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("URL / Link Scanner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = urlToScan,
                onValueChange = { urlToScan = it },
                label = { Text("Enter URL to scan") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.scanUrl(context, urlToScan) },
                modifier = Modifier.fillMaxWidth(),
                enabled = urlToScan.isNotBlank() && uiState !is UrlScannerUiState.Loading
            ) {
                if (uiState is UrlScannerUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Scan URL")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is UrlScannerUiState.Success -> UrlResultCard(state.result, state.safetyNetSafe)
                is UrlScannerUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                else -> {}
            }
        }
    }
}

@Composable
fun UrlResultCard(result: UrlScanResult, safetyNetSafe: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (result.urlVerdict) {
                "SAFE" -> Color(0xFFE8F5E9)
                "SUSPICIOUS" -> Color(0xFFFFF3E0)
                else -> Color(0xFFFFEBEE)
            },
            contentColor = Color(0xFF1A1C1E)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Verdict: ${result.urlVerdict}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = when (result.urlVerdict) {
                    "SAFE" -> Color(0xFF2E7D32)
                    "SUSPICIOUS" -> Color(0xFFEF6C00)
                    else -> Color(0xFFC62828)
                }
            )
            Text("SafetyNet Status: ${if (safetyNetSafe) "SAFE" else "DANGEROUS"}", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Recommendation: ${result.recommendation}", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text(result.explanation, color = Color(0xFF1A1C1E))
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Red Flags:", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            result.redFlags.forEach { flag ->
                Text("• $flag", color = Color(0xFF1A1C1E))
            }
        }
    }
}
