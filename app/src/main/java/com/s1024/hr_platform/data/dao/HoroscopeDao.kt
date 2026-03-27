package com.s1024.hr_platform.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.s1024.hr_platform.data.entity.HoroscopeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoroscopeDao {
    @Query("SELECT * FROM horoscope_cache WHERE sign = :sign LIMIT 1")
    fun getHoroscopeBySign(sign: String): Flow<HoroscopeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoroscope(horoscope: HoroscopeEntity)
}