package com.s1024.hr_platform.ui.screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.s1024.hr_platform.R
import com.s1024.hr_platform.data.Vacancy
import com.s1024.hr_platform.viewmodel.VacancyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: VacancyViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    val vacancies by viewModel.allVacancies.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.main_screen_title)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToDetails(0) }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        if (vacancies.isEmpty()) {
            Text(
                text = stringResource(id = R.string.empty_list),
                modifier = Modifier.padding(paddingValues).padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
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
            Text(text = vacancy.date, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = vacancy.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
