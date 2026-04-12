package com.uri.phishguard

import android.app.Application
import com.uri.phishguard.data.local.PhishDatabase
import com.uri.phishguard.data.remote.GeminiHelper
import com.uri.phishguard.data.remote.HibpApi
import com.uri.phishguard.data.remote.RealityDefenderApi
import com.uri.phishguard.data.remote.VirusTotalApi
import com.uri.phishguard.data.repository.AuthRepository
import com.uri.phishguard.data.repository.DatabaseRepository
import com.uri.phishguard.data.repository.NetworkRepository
import com.uri.phishguard.util.QuotaManager
import com.uri.phishguard.util.EncryptedPrefs
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhishGuardApplication : Application() {

    lateinit var databaseRepository: DatabaseRepository
    lateinit var networkRepository: NetworkRepository
    lateinit var authRepository: AuthRepository
    lateinit var geminiHelper: GeminiHelper
    lateinit var quotaManager: QuotaManager
    lateinit var encryptedPrefs: EncryptedPrefs

    override fun onCreate() {
        super.onCreate()

        val database = PhishDatabase.getDatabase(this)
        databaseRepository = DatabaseRepository(database.scanDao())

        // VirusTotal & HIBP use the same/similar base or different ones? 
        // VirusTotal: https://www.virustotal.com/api/v3/
        // HIBP: https://haveibeenpwned.com/api/v3/
        // Reality Defender: https://api.realitydefender.com/v1/ (Example base URL)

        val vtRetrofit = Retrofit.Builder()
            .baseUrl("https://www.virustotal.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        val hibpRetrofit = Retrofit.Builder()
            .baseUrl("https://haveibeenpwned.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val rdRetrofit = Retrofit.Builder()
            .baseUrl("https://api.realitydefender.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val vtApi = vtRetrofit.create(VirusTotalApi::class.java)
        val hibpApi = hibpRetrofit.create(HibpApi::class.java)
        val rdApi = rdRetrofit.create(RealityDefenderApi::class.java)

        networkRepository = NetworkRepository(vtApi, hibpApi, rdApi)
        authRepository = AuthRepository()
        quotaManager = QuotaManager(this)
        geminiHelper = GeminiHelper(quotaManager)
        encryptedPrefs = EncryptedPrefs(this)
    }
}
