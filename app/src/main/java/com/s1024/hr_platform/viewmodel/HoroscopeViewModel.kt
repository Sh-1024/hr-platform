package com.s1024.hr_platform.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s1024.hr_platform.data.entity.HoroscopeEntity
import com.s1024.hr_platform.data.network.NetworkMonitor
import com.s1024.hr_platform.data.repository.HoroscopeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HoroscopeViewModel(
    private val repository: HoroscopeRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val API_KEY = "some trash instead of api key because I did not add .env file"

    @RequiresApi(Build.VERSION_CODES.O)
    val currentSign = getZodiacSign(LocalDate.now())

    val isOnline: StateFlow<Boolean> = networkMonitor.isConnected
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    @RequiresApi(Build.VERSION_CODES.O)
    val dailyHoroscope: StateFlow<HoroscopeEntity?> = repository.getCachedHoroscope(currentSign)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchLatestHoroscope() {
        viewModelScope.launch {
            if (isOnline.value) {
                repository.refreshHoroscope(currentSign, API_KEY)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getZodiacSign(date: LocalDate): String {
        val day = date.dayOfMonth
        return when (date.monthValue) {
            1 -> if (day <= 19) "capricorn" else "aquarius"
            2 -> if (day <= 18) "aquarius" else "pisces"
            3 -> if (day <= 20) "pisces" else "aries"
            4 -> if (day <= 19) "aries" else "taurus"
            5 -> if (day <= 20) "taurus" else "gemini"
            6 -> if (day <= 20) "gemini" else "cancer"
            7 -> if (day <= 22) "cancer" else "leo"
            8 -> if (day <= 22) "leo" else "virgo"
            9 -> if (day <= 22) "virgo" else "libra"
            10 -> if (day <= 22) "libra" else "scorpio"
            11 -> if (day <= 21) "scorpio" else "sagittarius"
            12 -> if (day <= 21) "sagittarius" else "capricorn"
            else -> "aries"
        }
    }
}