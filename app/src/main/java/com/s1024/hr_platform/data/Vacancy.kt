package com.s1024.hr_platform.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Entity(tableName = "vacancies")
data class Vacancy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val author: String,
    val timestamp: Long
)



interface VacancyApiService {
    @GET("api/vacancies")
    suspend fun getVacancies(): List<Vacancy>

    @POST("api/vacancies")
    suspend fun createVacancy(@Body vacancy: Vacancy): Vacancy

    @PUT("api/vacancies/{id}")
    suspend fun updateVacancy(@Path("id") id: Int, @Body vacancy: Vacancy): Vacancy

    @DELETE("api/vacancies/{id}")
    suspend fun deleteVacancy(@Path("id") id: Int)
}