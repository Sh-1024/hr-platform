package com.s1024.hr_platform.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VacancyDao {
    @Query("SELECT * FROM vacancies")
    fun getAllVacancies(): Flow<List<Vacancy>>

    @Query("SELECT * FROM vacancies WHERE id = :id")
    suspend fun getVacancyById(id: Int): Vacancy?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVacancy(vacancy: Vacancy)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vacancies: List<Vacancy>)

    @Delete
    suspend fun deleteVacancy(vacancy: Vacancy)

    @Query("DELETE FROM vacancies")
    suspend fun deleteAll()
}