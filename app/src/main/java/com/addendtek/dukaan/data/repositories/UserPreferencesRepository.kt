package com.addendtek.dukaan.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val APP_NAME = stringPreferencesKey("app_name")
        val APP_HELP_ENABLED = booleanPreferencesKey("app_help")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveAppNamePreference(appName: String) {
        dataStore.edit {preferences ->
            preferences[APP_NAME] = appName

        }
    }

    suspend fun saveAppHelpPreference(appHelpEnabled: Boolean) {
        dataStore.edit {preferences ->
            preferences[APP_HELP_ENABLED] = appHelpEnabled

        }
    }

    val appName: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[APP_NAME] ?: ""
        }
    val appHelpEnabled: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[APP_HELP_ENABLED] ?: true
        }
}