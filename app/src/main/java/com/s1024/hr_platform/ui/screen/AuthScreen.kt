package com.s1024.hr_platform.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import com.s1024.hr_platform.R
import com.s1024.hr_platform.viewmodel.AuthViewModel

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onNavigateToMain: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmptyFieldError by remember { mutableStateOf(false) }

    
    val isLoading by authViewModel.isLoading.collectAsState()
    val serverErrorMessage by authViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(id = R.string.auth_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                isEmptyFieldError = false
                authViewModel.clearError() 
            },
            label = { Text(stringResource(id = R.string.auth_username_label)) },
            modifier = Modifier.fillMaxWidth(),
            isError = isEmptyFieldError || serverErrorMessage != null,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isEmptyFieldError = false
                authViewModel.clearError()
            },
            label = { Text(stringResource(id = R.string.auth_password_label)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(), 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = isEmptyFieldError || serverErrorMessage != null,
            singleLine = true
        )

        
        if (isEmptyFieldError) {
            Text(
                text = stringResource(id = R.string.auth_error_empty_fields),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        
        if (serverErrorMessage != null) {
            Text(
                text = serverErrorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                
                Button(
                    onClick = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            authViewModel.login(username.trim(), password.trim(), onNavigateToMain)
                        } else {
                            isEmptyFieldError = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = R.string.auth_login_button))
                }

                OutlinedButton(
                    onClick = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            authViewModel.register(username.trim(), password.trim(), onNavigateToMain)
                        } else {
                            isEmptyFieldError = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = R.string.auth_register_button))
                }
            }
        }
    }
}