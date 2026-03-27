package com.s1024.hr_platform.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.s1024.hr_platform.data.dao.HoroscopeDao
import com.s1024.hr_platform.data.entity.HoroscopeEntity

@Database(entities =[Vacancy::class, HoroscopeEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vacancyDao(): VacancyDao
    abstract fun horoscopeDao(): HoroscopeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hr_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}