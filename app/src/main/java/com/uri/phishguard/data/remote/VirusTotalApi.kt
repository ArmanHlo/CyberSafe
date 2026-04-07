package com.uri.phishguard.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface VirusTotalApi {
    @GET("urls/{id}")
    suspend fun getUrlReport(
        @Header("x-apikey") apiKey: String,
        @Path("id") id: String
    ): VirusTotalResponse
}

data class VirusTotalResponse(
    val data: VirusTotalData
)

data class VirusTotalData(
    val attributes: VirusTotalAttributes
)

data class VirusTotalAttributes(
    val last_analysis_stats: AnalysisStats,
    val last_analysis_results: Map<String, AnalysisResult>
)

data class AnalysisStats(
    val harmless: Int,
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
    val timeout: Int
)

data class AnalysisResult(
    val method: String,
    val engine_name: String,
    val category: String,
    val result: String
)
