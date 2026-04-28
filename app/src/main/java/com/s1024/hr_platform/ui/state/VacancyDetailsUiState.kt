package com.s1024.hr_platform.ui.state

data class VacancyDetailsUiState(
    val vacancyId: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val isExistingVacancy: Boolean = false
) {
    val isSaveEnabled: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && date.isNotBlank()
}
