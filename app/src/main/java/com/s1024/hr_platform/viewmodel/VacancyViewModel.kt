package com.s1024.hr_platform.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s1024.hr_platform.data.Vacancy
import com.s1024.hr_platform.data.VacancyDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VacancyViewModel(private val dao: VacancyDao) : ViewModel() {

    val allVacancies: StateFlow<List<Vacancy>> = dao.getAllVacancies()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveVacancy(id: Int, title: String, desc: String, date: String) {
        viewModelScope.launch {
            dao.insertVacancy(Vacancy(id = id, title = title, description = desc, date = date))
        }
    }

    fun deleteVacancy(vacancy: Vacancy) {
        viewModelScope.launch {
            dao.deleteVacancy(vacancy)
        }
    }

    suspend fun getVacancy(id: Int): Vacancy? {
        return dao.getVacancyById(id)
    }
}