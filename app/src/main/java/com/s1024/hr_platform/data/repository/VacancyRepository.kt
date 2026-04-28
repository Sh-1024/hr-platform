package com.s1024.hr_platform.data.repository

import com.s1024.hr_platform.data.Vacancy
import com.s1024.hr_platform.data.VacancyApiService
import com.s1024.hr_platform.data.VacancyDao
import com.s1024.hr_platform.data.network.NotificationHelper
import kotlinx.coroutines.flow.Flow

class VacancyRepository(
    private val dao: VacancyDao,
    private val api: VacancyApiService,
    private val notificationHelper: NotificationHelper
) {
    fun observeLocalVacancies(): Flow<List<Vacancy>> = dao.getAllVacancies()

    suspend fun fetchVacanciesFromRemote() {
        try {
            val remoteData = api.getVacancies()
            dao.deleteAll()
            dao.insertAll(remoteData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getVacancy(id: Int): Vacancy? = dao.getVacancyById(id)

    suspend fun saveVacancy(vacancy: Vacancy) {
        try {
            if (vacancy.id == 0) {
                val created = api.createVacancy(vacancy)
                dao.insertVacancy(created)

                notificationHelper.showVacancyCreatedNotification(created.title, created.author)
            } else {
                val updated = api.updateVacancy(vacancy.id, vacancy)
                dao.insertVacancy(updated)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dao.insertVacancy(vacancy)
        }
    }

    suspend fun deleteVacancy(vacancy: Vacancy) {
        try {
            api.deleteVacancy(vacancy.id)
            dao.deleteVacancy(vacancy)
        } catch (e: Exception) {
            e.printStackTrace()
            dao.deleteVacancy(vacancy)
        }
    }
}