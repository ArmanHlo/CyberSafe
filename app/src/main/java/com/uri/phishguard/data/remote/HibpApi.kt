package com.uri.phishguard.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import okhttp3.ResponseBody

interface HibpApi {
    @GET("breachedaccount/{email}")
    suspend fun getBreachedAccount(
        @Header("hibp-api-key") apiKey: String,
        @Path("email") email: String,
        @Header("user-agent") userAgent: String = "PhishGuard-Android-App"
    ): List<BreachResponse>

    @GET("https://api.pwnedpasswords.com/range/{first5}")
    suspend fun getPasswordRange(
        @Path("first5") first5: String
    ): ResponseBody
}

data class BreachResponse(
    val Name: String,
    val Title: String,
    val Domain: String,
    val BreachDate: String,
    val AddedDate: String,
    val ModifiedDate: String,
    val PwnCount: Long,
    val Description: String,
    val LogoPath: String,
    val DataClasses: List<String>,
    val IsVerified: Boolean,
    val IsFabricated: Boolean,
    val IsSensitive: Boolean,
    val IsRetired: Boolean,
    val IsSpamList: Boolean,
    val IsMalware: Boolean
)
