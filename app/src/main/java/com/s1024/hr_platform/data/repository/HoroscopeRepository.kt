package com.s1024.hr_platform.data.repository


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.s1024.hr_platform.data.dao.HoroscopeDao
import com.s1024.hr_platform.data.entity.HoroscopeEntity
import com.s1024.hr_platform.data.network.HoroscopeApi
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HoroscopeRepository(
    private val api: HoroscopeApi,
    private val dao: HoroscopeDao
) {
    fun getCachedHoroscope(sign: String): Flow<HoroscopeEntity?> = dao.getHoroscopeBySign(sign)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshHoroscope(sign: String, apiKey: String) {
        try {
            val response = api.getHoroscope(apiKey, sign)

            if (response.status == "ok" && response.data != null) {
                val data = response.data

                val entity = HoroscopeEntity(
                    sign = data.sign.lowercase(),
                    date = LocalDate.now().toString(),
                    text = data.horoscope
                )
                dao.insertHoroscope(entity)
                Log.d("HoroscopeRepo", "Horoscope saved to db")
            } else {
                Log.e("HoroscopeRepo", "API Error")
            }
        } catch (e: Exception) {
            Log.e("HoroscopeRepo", "Network or db error: ${e.message}")
        }
    }
}