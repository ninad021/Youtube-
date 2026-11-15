package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "youtubex_prefs")

data class UserPreferences(
    val isDarkTheme: Boolean = true,
    val autoPlayEnabled: Boolean = true,
    val preferredQuality: String = "Auto",
    val preferredSpeed: Float = 1.0f,
    val subtitlesEnabled: Boolean = false,
    val username: String = "Alex Rivera",
    val userHandle: String = "@alexrivera_dev"
)

class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val AUTO_PLAY_ENABLED = booleanPreferencesKey("auto_play_enabled")
        val PREFERRED_QUALITY = stringPreferencesKey("preferred_quality")
        val PREFERRED_SPEED = floatPreferencesKey("preferred_speed")
        val SUBTITLES_ENABLED = booleanPreferencesKey("subtitles_enabled")
        val USERNAME = stringPreferencesKey("username")
        val USER_HANDLE = stringPreferencesKey("user_handle")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            isDarkTheme = prefs[Keys.IS_DARK_THEME] ?: true,
            autoPlayEnabled = prefs[Keys.AUTO_PLAY_ENABLED] ?: true,
            preferredQuality = prefs[Keys.PREFERRED_QUALITY] ?: "Auto",
            preferredSpeed = prefs[Keys.PREFERRED_SPEED] ?: 1.0f,
            subtitlesEnabled = prefs[Keys.SUBTITLES_ENABLED] ?: false,
            username = prefs[Keys.USERNAME] ?: "Alex Rivera",
            userHandle = prefs[Keys.USER_HANDLE] ?: "@alexrivera_dev"
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_DARK_THEME] = enabled }
    }

    suspend fun setAutoPlayEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_PLAY_ENABLED] = enabled }
    }

    suspend fun setPreferredQuality(quality: String) {
        context.dataStore.edit { it[Keys.PREFERRED_QUALITY] = quality }
    }

    suspend fun setPreferredSpeed(speed: Float) {
        context.dataStore.edit { it[Keys.PREFERRED_SPEED] = speed }
    }

    suspend fun setSubtitlesEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SUBTITLES_ENABLED] = enabled }
    }

    suspend fun updateProfile(username: String, handle: String) {
        context.dataStore.edit {
            it[Keys.USERNAME] = username.trim().ifEmpty { "Alex Rivera" }
            val cleanHandle = handle.trim().let { h ->
                if (h.isEmpty()) "@alexrivera_dev" else if (h.startsWith("@")) h else "@$h"
            }
            it[Keys.USER_HANDLE] = cleanHandle
        }
    }
}
