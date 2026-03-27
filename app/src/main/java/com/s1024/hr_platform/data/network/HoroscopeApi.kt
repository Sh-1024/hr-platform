package com.s1024.hr_platform.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HoroscopeApi {
    @GET("v1/horoscope")
    suspend fun getHoroscope(
        @Header("X-API-Key") apiKey: String,
        @Query("sign") sign: String
    ): HoroscopeResponse
}