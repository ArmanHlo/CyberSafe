package com.uri.phishguard.data.remote

import okhttp3.MultipartBody
import retrofit2.http.*

interface RealityDefenderApi {
    @Multipart
    @POST("media/detect")
    suspend fun detectDeepfake(
        @Header("x-api-key") apiKey: String,
        @Part file: MultipartBody.Part
    ): RealityDefenderResponse
}

data class RealityDefenderResponse(
    val status: String,
    val results: List<DetectionResult>?
)

data class DetectionResult(
    val score: Double,
    val label: String,
    val type: String
)
