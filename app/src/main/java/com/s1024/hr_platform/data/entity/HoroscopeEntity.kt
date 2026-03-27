package com.s1024.hr_platform.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horoscope_cache")
data class HoroscopeEntity(
    @PrimaryKey val sign: String,
    val date: String,
    val text: String
)