package com.s1024.hr_platform.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

class SessionManager(private val context: Context) {
    private val USERNAME_KEY = stringPreferencesKey("username")

    val currentUser: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[USERNAME_KEY]
    }

    suspend fun registerUser(username: String) {
        context.authDataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
        }
    }

    suspend fun logout() {
        context.authDataStore.edit { prefs ->
            prefs.remove(USERNAME_KEY)
        }
    }
}