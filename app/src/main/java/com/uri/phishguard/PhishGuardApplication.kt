package com.uri.phishguard

import android.app.Application
import com.uri.phishguard.data.local.PhishDatabase
import com.uri.phishguard.data.remote.GeminiHelper
import com.uri.phishguard.data.remote.HibpApi
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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.virustotal.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        val vtApi = retrofit.create(VirusTotalApi::class.java)
        val hibpApi = retrofit.create(HibpApi::class.java)

        networkRepository = NetworkRepository(vtApi, hibpApi)
        authRepository = AuthRepository()
        quotaManager = QuotaManager(this)
        geminiHelper = GeminiHelper(quotaManager)
        encryptedPrefs = EncryptedPrefs(this)
    }
}
