package com.uri.phishguard.ui.scanner

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.local.ThreatLevel
import com.uri.phishguard.data.model.ImageScanResult
import com.uri.phishguard.data.remote.GeminiHelper
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.data.repository.NetworkRepository
import com.uri.phishguard.util.QuotaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

sealed class DeepfakeUiState {
    object Idle : DeepfakeUiState()
    object Loading : DeepfakeUiState()
    data class Success(val result: ImageScanResult) : DeepfakeUiState()
    data class Error(val message: String) : DeepfakeUiState()
}

class DeepfakeViewModel(
    private val geminiHelper: GeminiHelper,
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
    private val quotaManager: QuotaManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeepfakeUiState>(DeepfakeUiState.Idle)
    val uiState: StateFlow<DeepfakeUiState> = _uiState.asStateFlow()

    private val gson = Gson()

    fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = DeepfakeUiState.Loading

            try {
                val quota = quotaManager.remainingQuota.first()
                if (quota <= 0) {
                    _uiState.value = DeepfakeUiState.Error("Daily quota exceeded. Please try again tomorrow.")
                    return@launch
                }

                val resizedBitmap = resizeBitmap(bitmap, 800)
                
                // 1. Try Reality Defender (via NetworkRepository)
                val rdResult = try {
                    val stream = ByteArrayOutputStream()
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                    val byteArray = stream.toByteArray()
                    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
                    val part = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)
                    
                    networkRepository.detectDeepfake(part)
                } catch (e: Exception) {
                    null
                }

                val scanResult: ImageScanResult? = if (rdResult != null && rdResult.results?.isNotEmpty() == true) {
                    // Map Reality Defender response to ImageScanResult
                    val topResult = rdResult.results[0]
                    ImageScanResult(
                        verdict = when {
                            topResult.score > 0.8 -> "DEEPFAKE_FACESWAP"
                            topResult.score > 0.5 -> "MANIPULATED"
                            else -> "AUTHENTIC"
                        },
                        confidence = (topResult.score * 100).toInt(),
                        isRealProbability = ((1 - topResult.score) * 100).toInt(),
                        aiGeneratedProbability = (topResult.score * 100).toInt(),
                        deepfakeProbability = (topResult.score * 100).toInt(),
                        simpleExplanation = "Analysis by Reality Defender: Confidence ${topResult.score}",
                        useCaseRisk = if (topResult.score > 0.7) "HIGH" else "LOW"
                    )
                } else {
                    // 2. Fallback to Gemini if Reality Defender fails or returns no results
                    geminiHelper.analyzeImage(resizedBitmap)
                }

                if (scanResult != null) {
                    quotaManager.incrementUsage()

                    val threatLevel = when (scanResult.verdict) {
                        "AUTHENTIC" -> ThreatLevel.SAFE
                        "UNCERTAIN" -> ThreatLevel.MEDIUM
                        "MANIPULATED" -> ThreatLevel.HIGH
                        "AI_GENERATED", "DEEPFAKE_FACESWAP" -> ThreatLevel.CRITICAL
                        else -> ThreatLevel.UNKNOWN
                    }

                    val dbScan = ScanResult(
                        type = "IMAGE",
                        content = "Image Analysis",
                        threatLevel = threatLevel,
                        resultSummary = scanResult.verdict,
                        detailedReport = gson.toJson(scanResult)
                    )
                    databaseRepository.insertScan(dbScan)

                    _uiState.value = DeepfakeUiState.Success(scanResult)
                } else {
                    _uiState.value = DeepfakeUiState.Error("Deepfake analysis failed. Both Reality Defender and Gemini are currently unavailable.")
                }
            } catch (e: Exception) {
                _uiState.value = DeepfakeUiState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    private fun resizeBitmap(source: Bitmap, maxSize: Int): Bitmap {
        var width = source.width
        var height = source.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(source, width, height, true)
    }

    fun resetState() {
        _uiState.value = DeepfakeUiState.Idle
    }
}
