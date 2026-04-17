package com.uri.phishguard.data.remote

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.GsonBuilder
import com.uri.phishguard.BuildConfig
import com.uri.phishguard.data.model.ImageScanResult
import com.uri.phishguard.data.model.TextScanResult
import com.uri.phishguard.data.model.UrlScanResult
import com.uri.phishguard.util.QuotaManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class GeminiHelper(private val quotaManager: QuotaManager) {
    private val gson = GsonBuilder().setLenient().create()
    private val TAG = "GeminiHelper"
    private val MAX_RETRIES = 3
    private val INITIAL_DELAY = 1000L // 1 second
    
    private val config = generationConfig {
        responseMimeType = "application/json"
    }

    private val primaryModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config
    )

    private suspend fun <T> retryIO(
        times: Int = MAX_RETRIES,
        initialDelay: Long = INITIAL_DELAY,
        block: suspend () -> T?
    ): T? {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                val result = block()
                if (result != null) return result
            } catch (e: Exception) {
                Log.e(TAG, "Retry failed: ${e.message}")
            }
            delay(currentDelay)
            currentDelay *= 2
        }
        return try { block() } catch (e: Exception) { 
            Log.e(TAG, "Final attempt failed: ${e.message}")
            null 
        }
    }

    private suspend fun generateString(prompt: String): String? {
        return retryIO {
            try {
                primaryModel.generateContent(prompt).text
            } catch (e: Exception) {
                Log.e(TAG, "Model failed: ${e.message}")
                null
            }
        }
    }

    private suspend fun generateWithImage(bitmap: Bitmap, promptText: String): String? {
        val inputContent = content {
            image(bitmap)
            text(promptText)
        }
        return retryIO {
            try {
                primaryModel.generateContent(inputContent).text
            } catch (e: Exception) {
                Log.e(TAG, "Model with image failed: ${e.message}")
                null
            }
        }
    }

    private fun extractJson(text: String): String {
        return try {
            val start = text.indexOf("{")
            val end = text.lastIndexOf("}")
            if (start != -1 && end != -1 && end > start) {
                text.substring(start, end + 1)
            } else {
                text
            }
        } catch (e: Exception) {
            text
        }
    }

    suspend fun analyzeScamText(text: String): TextScanResult? = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze the message below for scams and phishing.
            You MUST return ONLY a JSON object. No preamble, no markdown formatting (like ```json), and no closing remarks.
            Ensure all arrays and objects are properly closed.
            
            Message: $text

            Expected JSON Format:
            {
              "verdict": "SAFE" | "SUSPICIOUS" | "DANGEROUS",
              "confidence": 0-100,
              "scam_type": "string",
              "red_flags": ["list of strings"],
              "safe_indicators": ["list of strings"],
              "psychological_tricks": ["list of strings"],
              "what_they_want": "string",
              "action_to_take": "string",
              "report_to": "string",
              "explanation": "string"
            }
        """.trimIndent()

        val responseText = generateString(prompt)
        responseText?.let {
            try {
                gson.fromJson(extractJson(it), TextScanResult::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "JSON Parse Error: ${e.message}")
                null
            }
        }
    }

    suspend fun analyzeImage(bitmap: Bitmap): ImageScanResult? = withContext(Dispatchers.IO) {
        val promptText = """
            Examine this image for deepfake or AI manipulation.
            Respond ONLY with valid JSON.

            {
              "verdict": "AUTHENTIC" | "AI_GENERATED" | "DEEPFAKE_FACESWAP" | "MANIPULATED" | "UNCERTAIN",
              "is_real_probability": 0-100,
              "ai_generated_probability": 0-100,
              "deepfake_probability": 0-100,
              "faces_detected": true|false,
              "likely_ai_tool": "string",
              "confidence": 0-100,
              "simple_explanation": "string",
              "use_case_risk": "LOW" | "MEDIUM" | "HIGH"
            }
        """.trimIndent()

        val responseText = generateWithImage(bitmap, promptText)
        responseText?.let {
            try {
                gson.fromJson(extractJson(it), ImageScanResult::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun analyzeUrl(url: String): UrlScanResult? = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze this URL for phishing.
            Respond ONLY with valid JSON:
            {
              "url_verdict": "SAFE" | "SUSPICIOUS" | "DANGEROUS",
              "confidence": 0-100,
              "checks": {
                "is_ip_address": bool, "has_typosquatting": bool, "https_present": bool
              },
              "red_flags": ["list"],
              "recommendation": "string",
              "explanation": "string"
            }
            URL: $url
        """.trimIndent()

        val responseText = generateString(prompt)
        responseText?.let {
            try {
                gson.fromJson(extractJson(it), UrlScanResult::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
