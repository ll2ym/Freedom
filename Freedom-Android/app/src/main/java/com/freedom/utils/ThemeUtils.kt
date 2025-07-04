package com.freedom.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

enum class AppTheme(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun from(value: String?): AppTheme {
            return values().find { it.value == value } ?: SYSTEM
        }
    }
}

object ThemeUtils {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    fun applyTheme(theme: AppTheme) {
        when (theme) {
            AppTheme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            AppTheme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppTheme.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun saveTheme(context: Context, theme: AppTheme) {
        runBlocking {
            context.themeDataStore.edit { prefs ->
                prefs[THEME_KEY] = theme.value
            }
        }
    }

    fun loadTheme(context: Context): AppTheme {
        return runBlocking {
            val prefs = context.themeDataStore.data.first()
            AppTheme.from(prefs[THEME_KEY])
        }
    }
}
