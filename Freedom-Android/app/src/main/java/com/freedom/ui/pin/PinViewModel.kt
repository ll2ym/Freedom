package com.freedom.ui.pin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor() : ViewModel() {

    val pin = mutableStateOf("")
    val isPinCorrect = mutableStateOf(false)
    val keypadNumbers = mutableStateOf((0..9).shuffled())

    private val correctPin = "123456"

    fun onKeyPress(key: Int) {
        if (pin.value.length < 6) {
            pin.value += key.toString()
            if (pin.value.length == 6) {
                if (pin.value == correctPin) {
                    isPinCorrect.value = true
                } else {
                    pin.value = ""
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
