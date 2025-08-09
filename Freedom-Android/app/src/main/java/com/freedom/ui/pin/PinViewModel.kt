package com.freedom.ui.pin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freedom.network.WebSocketManager
import com.freedom.storage.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val securePrefs: SecurePrefs,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    val pin = mutableStateOf("")
    val isPinCorrect = mutableStateOf(false)
    val keypadNumbers = mutableStateOf((0..9).shuffled())
    val hasPin = mutableStateOf(false)

    private var correctPinHash = ""
    private var duressPinHash = ""

    init {
        correctPinHash = securePrefs.getString("pin_hash", "") ?: ""
        duressPinHash = securePrefs.getString("duress_pin_hash", "") ?: ""
        hasPin.value = correctPinHash.isNotEmpty()
    }

    fun onKeyPress(key: Int) {
        if (pin.value.length < 6) {
            pin.value += key.toString()
            if (pin.value.length == 6) {
                if (hasPin.value) {
                    if (pin.value.hashCode().toString() == correctPinHash) {
                        isPinCorrect.value = true
                    } else if (duressPinHash.isNotEmpty() && pin.value.hashCode().toString() == duressPinHash) {
                        handleDuress()
                    } else {
                        pin.value = ""
                    }
                } else {
                    securePrefs.putString("pin_hash", pin.value.hashCode().toString())
                    isPinCorrect.value = true
                }
            }
        }
        keypadNumbers.value = (0..9).shuffled()
    }

    fun onBackspace() {
        if (pin.value.isNotEmpty()) {
            pin.value = pin.value.dropLast(1)
        }
        keypadNumbers.value = (0..9).shuffled()
    }

    private fun handleDuress() {
        viewModelScope.launch {
            // Send distress message to all contacts
            val duressMessage = securePrefs.getString("duress_message", "") ?: ""
            if (duressMessage.isNotEmpty()) {
                // This is a placeholder. A real implementation would need to get a list of all contacts.
                webSocketManager.sendMessage(duressMessage)
            }

            // Wipe data
            securePrefs.clear()

            // For now, just mark the PIN as correct to exit the PIN screen.
            // A real implementation would likely exit the app or navigate to a "safe" screen.
            isPinCorrect.value = true
        }
    }
}
