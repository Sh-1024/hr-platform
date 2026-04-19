package com.s1024.hr_platform.ui.screen

import VacancyViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.s1024.hr_platform.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    vacancyId: Int,
    viewModel: VacancyViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.detailsUiState.collectAsStateWithLifecycle()

    LaunchedEffect(vacancyId) {
        viewModel.loadVacancy(vacancyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            id = if (uiState.isExistingVacancy) {
                                R.string.edit_vacancy_title
                            } else {
                                R.string.create_vacancy_title
                            }
                        )
                    )
                }
            )
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
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text(stringResource(id = R.string.title_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.author,
                onValueChange = viewModel::updateAuthor,
                label = { Text("Автор вакансии") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text(stringResource(id = R.string.desc_hint)) },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Button(
                onClick = {
                    viewModel.saveCurrentVacancy()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isSaveEnabled
            ) {
                Text(stringResource(id = R.string.save))
            }

            if (uiState.isExistingVacancy) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteCurrentVacancy()
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