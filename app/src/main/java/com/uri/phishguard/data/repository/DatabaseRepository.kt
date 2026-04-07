package com.uri.phishguard.data.repository

import com.uri.phishguard.data.local.ScanDao
import com.uri.phishguard.data.local.ScanResult
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(private val scanDao: ScanDao) {
    val allScans: Flow<List<ScanResult>> = scanDao.getAllScans()

    suspend fun insertScan(scan: ScanResult) {
        scanDao.insertScan(scan)
    }

    suspend fun deleteScan(scan: ScanResult) {
        scanDao.deleteScan(scan)
    }

    suspend fun clearHistory() {
        scanDao.clearHistory()
    }
}
