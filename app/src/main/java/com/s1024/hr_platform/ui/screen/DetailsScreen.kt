package com.s1024.hr_platform.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.s1024.hr_platform.R
import com.s1024.hr_platform.data.Vacancy
import com.s1024.hr_platform.viewmodel.VacancyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    vacancyId: Int,
    viewModel: VacancyViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var currentVacancy by remember { mutableStateOf<Vacancy?>(null) }

    LaunchedEffect(vacancyId) {
        if (vacancyId != 0) {
            viewModel.getVacancy(vacancyId)?.let { v ->
                currentVacancy = v
                title = v.title
                description = v.description
                date = v.date
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.vacancy_details)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(id = R.string.title_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.desc_hint)) },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text(stringResource(id = R.string.date_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.saveVacancy(vacancyId, title, description, date)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.save))
            }

            if (vacancyId != 0) {
                OutlinedButton(
                    onClick = {
                        currentVacancy?.let { viewModel.deleteVacancy(it) }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            }
        }
    }
}