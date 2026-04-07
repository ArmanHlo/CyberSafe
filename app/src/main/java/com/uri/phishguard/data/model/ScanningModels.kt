package com.uri.phishguard.data.model

import com.google.gson.annotations.SerializedName

// Text Scan Result
data class TextScanResult(
    val verdict: String = "",
    val confidence: Int = 0,
    @SerializedName("scam_type") val scamType: String = "",
    @SerializedName("red_flags") val redFlags: List<String> = emptyList(),
    @SerializedName("safe_indicators") val safeIndicators: List<String> = emptyList(),
    @SerializedName("psychological_tricks") val psychologicalTricks: List<String> = emptyList(),
    @SerializedName("what_they_want") val whatTheyWant: String = "",
    @SerializedName("action_to_take") val actionToTake: String = "",
    @SerializedName("report_to") val reportTo: String = "",
    val explanation: String = ""
)

// Image Scan Result
data class ImageScanResult(
    val verdict: String = "",
    @SerializedName("is_real_probability") val isRealProbability: Int = 0,
    @SerializedName("ai_generated_probability") val aiGeneratedProbability: Int = 0,
    @SerializedName("deepfake_probability") val deepfakeProbability: Int = 0,
    @SerializedName("faces_detected") val facesDetected: Boolean = false,
    @SerializedName("face_analysis") val faceAnalysis: FaceAnalysis? = null,
    @SerializedName("image_forensics") val imageForensics: ImageForensics? = null,
    @SerializedName("likely_ai_tool") val likelyAiTool: String = "",
    @SerializedName("manipulation_regions") val manipulationRegions: String = "",
    val confidence: Int = 0,
    @SerializedName("simple_explanation") val simpleExplanation: String = "",
    @SerializedName("should_trust_this_image") val shouldTrustThisImage: Boolean = false,
    @SerializedName("use_case_risk") val useCaseRisk: String = ""
)

data class FaceAnalysis(
    @SerializedName("symmetry_score") val symmetryScore: Int = 0,
    @SerializedName("skin_texture_natural") val skinTextureNatural: Boolean = false,
    @SerializedName("eye_reflections_consistent") val eyeReflectionsConsistent: Boolean = false,
    @SerializedName("hair_rendering_natural") val hairRenderingNatural: Boolean = false,
    @SerializedName("teeth_artifacts") val teethArtifacts: Boolean = false,
    @SerializedName("ear_artifacts") val earArtifacts: Boolean = false,
    @SerializedName("neck_boundary_clean") val neckBoundaryClean: Boolean = false
)

data class ImageForensics(
    @SerializedName("lighting_consistent") val lightingConsistent: Boolean = false,
    @SerializedName("shadow_direction_consistent") val shadowDirectionConsistent: Boolean = false,
    @SerializedName("background_blending_natural") val backgroundBlendingNatural: Boolean = false,
    @SerializedName("edge_artifacts_detected") val edgeArtifactsDetected: Boolean = false,
    @SerializedName("jpeg_compression_anomaly") val jpegCompressionAnomaly: Boolean = false,
    @SerializedName("color_distribution_natural") val colorDistributionNatural: Boolean = false,
    @SerializedName("noise_pattern_consistent") val noisePatternConsistent: Boolean = false
)

// URL Scan Result
data class UrlScanResult(
    @SerializedName("url_verdict") val urlVerdict: String = "",
    val confidence: Int = 0,
    val checks: UrlChecks? = null,
    @SerializedName("impersonated_brand") val impersonatedBrand: String? = null,
    @SerializedName("red_flags") val redFlags: List<String> = emptyList(),
    val recommendation: String = "",
    val explanation: String = ""
)

data class UrlChecks(
    @SerializedName("is_ip_address") val isIpAddress: Boolean = false,
    @SerializedName("has_typosquatting") val hasTyposquatting: Boolean = false,
    @SerializedName("suspicious_tld") val suspiciousTld: Boolean = false,
    @SerializedName("excessive_subdomains") val excessiveSubdomains: Boolean = false,
    @SerializedName("misleading_brand") val misleadingBrand: Boolean = false,
    @SerializedName("is_url_shortener") val isUrlShortener: Boolean = false,
    @SerializedName("has_suspicious_path") val hasSuspiciousPath: Boolean = false,
    @SerializedName("https_present") val httpsPresent: Boolean = false,
    @SerializedName("urgency_keywords") val urgencyKeywords: Boolean = false
)
