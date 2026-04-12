package com.uri.phishguard.data.repository

import com.uri.phishguard.BuildConfig
import com.uri.phishguard.data.remote.HibpApi
import com.uri.phishguard.data.remote.RealityDefenderApi
import com.uri.phishguard.data.remote.VirusTotalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class NetworkRepository(
    private val vtApi: VirusTotalApi,
    private val hibpApi: HibpApi,
    private val rdApi: RealityDefenderApi
) {
    suspend fun scanUrl(urlId: String) = withContext(Dispatchers.IO) {
        vtApi.getUrlReport(BuildConfig.VIRUSTOTAL_API_KEY, urlId)
    }

    suspend fun checkEmailBreach(email: String, hibpKey: String) = withContext(Dispatchers.IO) {
        hibpApi.getBreachedAccount(hibpKey, email)
    }

    suspend fun checkPasswordBreach(first5: String) = withContext(Dispatchers.IO) {
        hibpApi.getPasswordRange(first5).string()
    }

    suspend fun detectDeepfake(file: MultipartBody.Part) = withContext(Dispatchers.IO) {
        rdApi.detectDeepfake(BuildConfig.REALITY_DEFENDER_API_KEY, file)
    }
}
