package com.uri.phishguard.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromThreatLevel(value: ThreatLevel): String {
        return value.name
    }

    @TypeConverter
    fun toThreatLevel(value: String): ThreatLevel {
        return try {
            ThreatLevel.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ThreatLevel.UNKNOWN
        }
    }
}
