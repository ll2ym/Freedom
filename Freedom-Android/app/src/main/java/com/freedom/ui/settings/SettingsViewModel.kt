package com.freedom.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freedom.network.ApiService
import com.freedom.network.ProfileUpdateRequest
import com.freedom.storage.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val displayName: String = "",
    val status: String = "",
    val showNotificationsWhenLocked: Boolean = true,
    val wipeAfterLogout: String = "00:00:00",
    val wipeAfterNoConnection: String = "00:00:00",
    val duressPin: String = "",
    val duressMessage: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securePrefs: SecurePrefs,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(
                displayName = securePrefs.getString("display_name", "") ?: "",
                status = securePrefs.getString("status", "") ?: "",
                showNotificationsWhenLocked = securePrefs.getBoolean("show_notifications_locked", true),
                wipeAfterLogout = securePrefs.getString("wipe_after_logout", "00:00:00") ?: "00:00:00",
                wipeAfterNoConnection = securePrefs.getString("wipe_after_no_connection", "00:00:00") ?: "00:00:00",
                duressPin = securePrefs.getString("duress_pin", "") ?: "",
                duressMessage = securePrefs.getString("duress_message", "") ?: ""
            )
        }
    }

    fun onDisplayNameChange(value: String) {
        _uiState.value = _uiState.value.copy(displayName = value)
    }

    fun onStatusChange(value: String) {
        _uiState.value = _uiState.value.copy(status = value)
    }

    fun onShowNotificationsChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(showNotificationsWhenLocked = value)
    }

    fun onWipeAfterLogoutChange(value: String) {
        _uiState.value = _uiState.value.copy(wipeAfterLogout = value)
    }

    fun onWipeAfterNoConnectionChange(value: String) {
        _uiState.value = _uiState.value.copy(wipeAfterNoConnection = value)
    }

    fun onDuressPinChange(value: String) {
        _uiState.value = _uiState.value.copy(duressPin = value)
    }

    fun onDuressMessageChange(value: String) {
        _uiState.value = _uiState.value.copy(duressMessage = value)
    }

    fun saveSettings() {
        viewModelScope.launch {
            securePrefs.putString("display_name", _uiState.value.displayName)
            securePrefs.putString("status", _uiState.value.status)
            securePrefs.putBoolean("show_notifications_locked", _uiState.value.showNotificationsWhenLocked)
            securePrefs.putString("wipe_after_logout", _uiState.value.wipeAfterLogout)
            securePrefs.putString("wipe_after_no_connection", _uiState.value.wipeAfterNoConnection)
            securePrefs.putString("duress_pin", _uiState.value.duressPin)
            securePrefs.putString("duress_message", _uiState.value.duressMessage)

            // Also update the user profile on the server
            val token = securePrefs.getString("auth_token")
            if (token != null) {
                apiService.updateUser(
                    "Bearer $token",
                    ProfileUpdateRequest(
                        displayName = _uiState.value.displayName,
                        status = _uiState.value.status
                    )
                )
            }
        }
    }
}
