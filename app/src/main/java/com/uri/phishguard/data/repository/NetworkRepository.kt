package com.uri.phishguard.data.repository

import com.uri.phishguard.BuildConfig
import com.uri.phishguard.data.remote.HibpApi
import com.uri.phishguard.data.remote.VirusTotalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkRepository(
    private val vtApi: VirusTotalApi,
    private val hibpApi: HibpApi
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
}
