import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s1024.hr_platform.data.Vacancy
import com.s1024.hr_platform.data.repository.VacancyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class VacancySortOption {
    DATE_DESC, DATE_ASC, TITLE_ASC, AUTHOR_ASC
}

data class VacancyDetailsUiState(
    val vacancyId: Int = 0,
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val isExistingVacancy: Boolean = false
) {
    val isSaveEnabled: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && author.isNotBlank()
}

class VacancyViewModel(
    private val repository: VacancyRepository
) : ViewModel() {


    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _authorFilter = MutableStateFlow("")
    val authorFilter = _authorFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(VacancySortOption.DATE_DESC)
    val sortOption = _sortOption.asStateFlow()


    val filteredVacancies: StateFlow<List<Vacancy>> = combine(
        repository.observeLocalVacancies(),
        _searchQuery,
        _authorFilter,
        _sortOption
    ) { vacancies, query, author, sort ->
        var list = vacancies

        if (query.isNotBlank()) {
            list = list.filter { it.title.contains(query, ignoreCase = true) }
        }
        if (author.isNotBlank()) {
            list = list.filter { it.author.contains(author, ignoreCase = true) }
        }

        when (sort) {
            VacancySortOption.DATE_DESC -> list.sortedByDescending { it.timestamp }
            VacancySortOption.DATE_ASC -> list.sortedBy { it.timestamp }
            VacancySortOption.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
            VacancySortOption.AUTHOR_ASC -> list.sortedBy { it.author.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _detailsUiState = MutableStateFlow(VacancyDetailsUiState())
    val detailsUiState: StateFlow<VacancyDetailsUiState> = _detailsUiState.asStateFlow()

    init {
        viewModelScope.launch { repository.fetchVacanciesFromRemote() }
    }


    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateAuthorFilter(author: String) { _authorFilter.value = author }
    fun updateSortOption(option: VacancySortOption) { _sortOption.value = option }


    fun loadVacancy(vacancyId: Int) {
        viewModelScope.launch {
            if (vacancyId == 0) {
                _detailsUiState.value = VacancyDetailsUiState()
                return@launch
            }
            val vacancy = repository.getVacancy(vacancyId)
            _detailsUiState.value = vacancy?.toUiState() ?: VacancyDetailsUiState()
        }
    }

    fun updateTitle(title: String) { _detailsUiState.update { it.copy(title = title) } }
    fun updateDescription(desc: String) { _detailsUiState.update { it.copy(description = desc) } }
    fun updateAuthor(author: String) { _detailsUiState.update { it.copy(author = author) } }

    fun saveCurrentVacancy() {
        val state = detailsUiState.value
        if (!state.isSaveEnabled) return

        viewModelScope.launch {
            repository.saveVacancy(
                Vacancy(
                    id = state.vacancyId,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    author = state.author.trim(),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteCurrentVacancy() {
        val state = detailsUiState.value
        if (!state.isExistingVacancy) return

        viewModelScope.launch {
            repository.deleteVacancy(
                Vacancy(
                    id = state.vacancyId,
                    title = state.title,
                    description = state.description,
                    author = state.author,
                    timestamp = 0L
                )
            )
            _detailsUiState.value = VacancyDetailsUiState()
        }
    }

    private fun Vacancy.toUiState() = VacancyDetailsUiState(
        vacancyId = id, title = title, description = description, author = author, isExistingVacancy = true
    )
}