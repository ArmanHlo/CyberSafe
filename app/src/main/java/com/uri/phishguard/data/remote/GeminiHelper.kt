package com.uri.phishguard.data.remote

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.uri.phishguard.BuildConfig
import com.uri.phishguard.data.model.ImageScanResult
import com.uri.phishguard.data.model.TextScanResult
import com.uri.phishguard.data.model.UrlScanResult
import com.uri.phishguard.util.QuotaManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiHelper(private val quotaManager: QuotaManager) {
    private val gson = Gson()
    private val TAG = "GeminiHelper"
    
    private val config = generationConfig {
        responseMimeType = "application/json"
    }

    // Strictly using the 2.5 model as requested
    private val primaryModel = GenerativeModel(
        modelName = "gemini-2.5-flash", 
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config
    )

    private val fallbackModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config
    )

    private suspend fun generateString(prompt: String): String? {
        return try {
            primaryModel.generateContent(prompt).text
        } catch (e: Exception) {
            Log.e(TAG, "Model failed: ${e.message}")
            null
        }
    }

    private suspend fun generateWithImage(bitmap: Bitmap, promptText: String): String? {
        val inputContent = content {
            image(bitmap)
            text(promptText)
        }
        return try {
            primaryModel.generateContent(inputContent).text
        } catch (e: Exception) {
            Log.e(TAG, "Model with image failed: ${e.message}")
            null
        }
    }

    suspend fun analyzeScamText(text: String): TextScanResult? = withContext(Dispatchers.IO) {
        val prompt = """
            You are India's top cybersecurity analyst specializing in 
            digital scams, phishing, and social engineering attacks.
            Analyze the message below and respond ONLY with a valid 
            JSON object — no markdown, no explanation outside JSON.

            Message to analyze:
            $text

            Respond with this exact JSON structure:
            {
              "verdict": "SAFE" | "SUSPICIOUS" | "DANGEROUS",
              "confidence": 0-100,
              "scam_type": "string",
              "red_flags": ["flag1", "flag2", "flag3"],
              "safe_indicators": ["indicator1"],
              "psychological_tricks": ["urgency", "fear", "greed"],
              "what_they_want": "string",
              "action_to_take": "string",
              "report_to": "string",
              "explanation": "2-3 sentence plain English explanation"
            }
        """.trimIndent()

        val responseText = generateString(prompt)
        Log.d(TAG, "Raw response: ${responseText ?: "NULL"}")
        
        if (responseText != null) {
            quotaManager.incrementUsage()
            try {
                val cleanedJson = if (responseText.contains("{")) {
                    responseText.substring(responseText.indexOf("{"), responseText.lastIndexOf("}") + 1)
                } else {
                    responseText
                }
                gson.fromJson(cleanedJson, TextScanResult::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Parsing failed: ${e.message}")
                null
            }
        } else null
    }

    suspend fun analyzeImage(bitmap: Bitmap): ImageScanResult? = withContext(Dispatchers.IO) {
        val promptText = """
            You are a world-class digital forensics expert and 
            deepfake detection specialist with expertise in computer
            vision, image forensics, and AI-generated content detection.

            Carefully examine every pixel of this image and respond 
            ONLY with a valid JSON object — no markdown, no text outside JSON.

            {
              "verdict": "AUTHENTIC" | "AI_GENERATED" | 
                         "DEEPFAKE_FACESWAP" | "MANIPULATED" | "UNCERTAIN",
              "is_real_probability": 0-100,
              "ai_generated_probability": 0-100,
              "deepfake_probability": 0-100,
              "faces_detected": true|false,
              "face_analysis": {
                "symmetry_score": 0-100,
                "skin_texture_natural": true|false,
                "eye_reflections_consistent": true|false,
                "hair_rendering_natural": true|false,
                "teeth_artifacts": true|false,
                "ear_artifacts": true|false,
                "neck_boundary_clean": true|false
              },
              "image_forensics": {
                "lighting_consistent": true|false,
                "shadow_direction_consistent": true|false,
                "background_blending_natural": true|false,
                "edge_artifacts_detected": true|false,
                "jpeg_compression_anomaly": true|false,
                "color_distribution_natural": true|false,
                "noise_pattern_consistent": true|false
              },
              "likely_ai_tool": "Midjourney" | "DALL-E" | 
                                "Stable Diffusion" | "FaceSwap" |
                                "Kling" | "Runway" | "Unknown" | "None",
              "manipulation_regions": "string describing which parts
                                       appear manipulated, if any",
              "confidence": 0-100,
              "simple_explanation": "2-3 sentences in plain language
                                     explaining findings to a normal user",
              "should_trust_this_image": true|false,
              "use_case_risk": "LOW" | "MEDIUM" | "HIGH"
            }
        """.trimIndent()

        val responseText = generateWithImage(bitmap, promptText)
        if (responseText != null) {
            quotaManager.incrementUsage()
            try {
                val cleanedJson = if (responseText.contains("{")) {
                    responseText.substring(responseText.indexOf("{"), responseText.lastIndexOf("}") + 1)
                } else {
                    responseText
                }
                gson.fromJson(cleanedJson, ImageScanResult::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    suspend fun analyzeUrl(url: String): UrlScanResult? = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze this URL for phishing and scam indicators.
            Respond ONLY with valid JSON:
            {
              "url_verdict": "SAFE" | "SUSPICIOUS" | "DANGEROUS",
              "confidence": 0-100,
              "checks": {
                "is_ip_address": true|false,
                "has_typosquatting": true|false,
                "suspicious_tld": true|false,
                "excessive_subdomains": true|false,
                "misleading_brand": true|false,
                "is_url_shortener": true|false,
                "has_suspicious_path": true|false,
                "https_present": true|false,
                "urgency_keywords": true|false
              },
              "impersonated_brand": "string or null",
              "red_flags": ["list of specific issues found"],
              "recommendation": "Do NOT visit / Proceed with caution / Safe to visit",
              "explanation": "2-3 sentences plain English"
            }
            URL to analyze: $url
        """.trimIndent()

        val responseText = generateString(prompt)
        if (responseText != null) {
            quotaManager.incrementUsage()
            try {
                val cleanedJson = if (responseText.contains("{")) {
                    responseText.substring(responseText.indexOf("{"), responseText.lastIndexOf("}") + 1)
                } else {
                    responseText
                }
                gson.fromJson(cleanedJson, UrlScanResult::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }
}
