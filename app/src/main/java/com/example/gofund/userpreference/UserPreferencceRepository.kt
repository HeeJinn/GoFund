package com.example.gofund.userpreference

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Define data class to hold preferences
data class UserPreferences(
    val email: String,
    val password: String, // WARNING: Storing plain text password is not secure!
    val rememberMe: Boolean
)

// Context extension property to create DataStore instance (singleton)
// Choose a unique name for your DataStore file
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_login_prefs")

class UserPreferencesRepository(private val context: Context) {

    // Define Preference Keys
    private object PreferencesKeys {
        val EMAIL = stringPreferencesKey("pref_email")
        val PASSWORD = stringPreferencesKey("pref_password") // Key for the insecure password
        val REMEMBER_ME = booleanPreferencesKey("pref_remember_me")
    }

    // Flow to read all preferences
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("UserPrefsRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val email = preferences[PreferencesKeys.EMAIL] ?: ""
            val password = preferences[PreferencesKeys.PASSWORD] ?: "" // Retrieve plain text password
            val rememberMe = preferences[PreferencesKeys.REMEMBER_ME] ?: false
            UserPreferences(email, password, rememberMe)
        }

    // Function to save/clear credentials based on rememberMe flag
    suspend fun updateLoginCredentials(email: String, password: String, rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMEMBER_ME] = rememberMe
            if (rememberMe) {
                preferences[PreferencesKeys.EMAIL] = email
                // *** SECURITY WARNING ***
                // Storing passwords in plain text is highly insecure.
                preferences[PreferencesKeys.PASSWORD] = password
                Log.d("UserPrefsRepo", "Saved credentials (INSECURELY) for email: $email")
            } else {
                // If rememberMe is false, clear the stored credentials
                preferences.remove(PreferencesKeys.EMAIL)
                preferences.remove(PreferencesKeys.PASSWORD)
                Log.d("UserPrefsRepo", "Cleared saved credentials.")
            }
        }
    }
}