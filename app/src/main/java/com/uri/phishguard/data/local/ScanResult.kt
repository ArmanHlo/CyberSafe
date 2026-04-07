package com.uri.phishguard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String, // "TEXT", "URL", "IMAGE", "QR", "EMAIL", "WIFI"
    val content: String,
    val threatLevel: ThreatLevel,
    val resultSummary: String,
    val detailedReport: String,
    val timestamp: Long = System.currentTimeMillis()
)
