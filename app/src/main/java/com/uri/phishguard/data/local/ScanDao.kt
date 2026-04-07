package com.uri.phishguard.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanResult)

    @Delete
    suspend fun deleteScan(scan: ScanResult)

    @Query("DELETE FROM scan_history")
    suspend fun clearHistory()
}
