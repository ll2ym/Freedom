package com.freedom.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")