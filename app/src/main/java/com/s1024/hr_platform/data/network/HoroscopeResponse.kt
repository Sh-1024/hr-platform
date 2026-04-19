package com.s1024.hr_platform.data.network

import com.google.gson.annotations.SerializedName
    data class HoroscopeResponse(
        val status: String,
        val data: HoroscopeData?
    )

    data class HoroscopeData(
        val sign: String,
        val horoscope: String
    )