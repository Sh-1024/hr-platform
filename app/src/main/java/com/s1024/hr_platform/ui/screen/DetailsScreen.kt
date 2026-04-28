package com.s1024.hr_platform.ui.screen

import VacancyDetailsUiState
import VacancyViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.s1024.hr_platform.R

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    vacancyId: Int,
    viewModel: VacancyViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.detailsUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(vacancyId) {
        viewModel.loadVacancy(vacancyId)
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.updateLocation(
                        context.getString(
                            R.string.gps_location_format,
                            location.latitude,
                            location.longitude
                        )
                    )
                }
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(id = R.string.vacancy_details)) }) }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = uiState.title, onValueChange = viewModel::updateTitle, label = { Text(stringResource(id = R.string.title_hint)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.author, onValueChange = viewModel::updateAuthor, label = { Text(stringResource(id = R.string.author_hint)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.description, onValueChange = viewModel::updateDescription, label = { Text(stringResource(id = R.string.desc_hint)) }, modifier = Modifier.fillMaxWidth().height(100.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.location,
                    onValueChange = viewModel::updateLocation,
                    label = { Text(stringResource(id = R.string.location_hint)) },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(stringResource(id = R.string.gps_button))
                }
            }

            Button(
                onClick = { viewModel.saveCurrentVacancy(); onNavigateBack() },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(id = R.string.save)) }

            
            if (uiState.isExistingVacancy) {
                Button(
                    onClick = { shareToTelegram(context, uiState) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(stringResource(id = R.string.share_to_telegram))
                }

                OutlinedButton(onClick = { viewModel.deleteCurrentVacancy(); onNavigateBack() }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(id = R.string.delete))
                }
            }
        }
    }
}


fun shareToTelegram(context: Context, uiState: VacancyDetailsUiState) {
    val locationText = uiState.location.ifBlank {
        context.getString(R.string.location_not_specified)
    }
    val message = context.getString(
        R.string.telegram_share_message,
        uiState.title,
        uiState.author,
        locationText,
        uiState.description
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
        setPackage("org.telegram.messenger") 
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        
        intent.setPackage(null)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_vacancy_chooser_title)))
    }
}