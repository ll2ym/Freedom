package com.freedom.ui.pin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.freedom.storage.SecurePrefs

@HiltViewModel
class PinViewModel @Inject constructor(
    private val securePrefs: SecurePrefs
) : ViewModel() {

    val pin = mutableStateOf("")
    val isPinCorrect = mutableStateOf(false)
    val keypadNumbers = mutableStateOf((0..9).shuffled())
    val hasPin = mutableStateOf(false)

    private var correctPin = ""

    init {
        correctPin = securePrefs.getString("pin", "") ?: ""
        hasPin.value = correctPin.isNotEmpty()
    }

    fun onKeyPress(key: Int) {
        if (pin.value.length < 6) {
            pin.value += key.toString()
            if (pin.value.length == 6) {
                if (hasPin.value) {
                    if (pin.value == correctPin) {
                        isPinCorrect.value = true
                    } else {
                        pin.value = ""
                    }
                } else {
                    securePrefs.putString("pin", pin.value)
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
}
