package com.s1024.hr_platform.ui.screen
import VacancyViewModel
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.s1024.hr_platform.R
import com.s1024.hr_platform.data.Vacancy
import com.s1024.hr_platform.viewmodel.HoroscopeViewModel
import androidx.compose.runtime.*

import java.text.DateFormat
import java.util.Date
import java.util.Locale

fun Long.formatToReadableDate(): String {
    val date = Date(this)
    val format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
    return format.format(date)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: VacancyViewModel,
    horoscopeViewModel: HoroscopeViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        horoscopeViewModel.fetchLatestHoroscope()
    }

    val vacancies by viewModel.filteredVacancies.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val authorFilter by viewModel.authorFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val horoscopeUiState by horoscopeViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.main_screen_title)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.cd_open_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToDetails(0) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.cd_add_vacancy))
            }
        }
    ) { paddingValues ->

        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            
            if (!horoscopeUiState.isOnline) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.network_unavailable),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.horoscope_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (horoscopeUiState.isAvailable) {
                            Text(
                                text = stringResource(
                                    R.string.horoscope_sign_format,
                                    horoscopeUiState.sign.toLocalizedZodiacName(),
                                    horoscopeUiState.text.orEmpty()
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.horoscope_unavailable),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        label = { Text(stringResource(id = R.string.search_by_title_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = authorFilter,
                            onValueChange = viewModel::updateAuthorFilter,
                            label = { Text(stringResource(id = R.string.filter_by_author_label)) },
                            modifier = Modifier.weight(1f)
                        )
                        var expanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            Button(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Text(stringResource(id = R.string.sort_button))
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text(stringResource(id = R.string.sort_newest_first)) }, onClick = { viewModel.updateSortOption(VacancySortOption.DATE_DESC); expanded = false })
                                DropdownMenuItem(text = { Text(stringResource(id = R.string.sort_oldest_first)) }, onClick = { viewModel.updateSortOption(VacancySortOption.DATE_ASC); expanded = false })
                                DropdownMenuItem(text = { Text(stringResource(id = R.string.sort_by_title)) }, onClick = { viewModel.updateSortOption(VacancySortOption.TITLE_ASC); expanded = false })
                                DropdownMenuItem(text = { Text(stringResource(id = R.string.sort_by_author)) }, onClick = { viewModel.updateSortOption(VacancySortOption.AUTHOR_ASC); expanded = false })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            
            if (vacancies.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.empty_list))
                    }
                }
            } else {
                
                items(vacancies) { vacancy ->
                    VacancyItem(vacancy, onClick = { onNavigateToDetails(vacancy.id) })
                }
            }
        }
    }
}

@Composable
fun VacancyItem(vacancy: Vacancy, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = vacancy.title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(id = R.string.author_format, vacancy.author),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = vacancy.timestamp.formatToReadableDate(), style = MaterialTheme.typography.bodySmall)
            if (vacancy.location.isNotBlank()) {
                Text(
                    text = stringResource(id = R.string.location_format, vacancy.location),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = vacancy.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun String?.toLocalizedZodiacName(): String {
    val labelRes = when (this?.lowercase()) {
        "aries" -> R.string.zodiac_aries
        "taurus" -> R.string.zodiac_taurus
        "gemini" -> R.string.zodiac_gemini
        "cancer" -> R.string.zodiac_cancer
        "leo" -> R.string.zodiac_leo
        "virgo" -> R.string.zodiac_virgo
        "libra" -> R.string.zodiac_libra
        "scorpio" -> R.string.zodiac_scorpio
        "sagittarius" -> R.string.zodiac_sagittarius
        "capricorn" -> R.string.zodiac_capricorn
        "aquarius" -> R.string.zodiac_aquarius
        "pisces" -> R.string.zodiac_pisces
        else -> R.string.zodiac_unknown
    }
    return stringResource(id = labelRes)
}
