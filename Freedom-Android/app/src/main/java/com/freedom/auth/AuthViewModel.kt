package com.freedom.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<AuthState?>(null)
    val loginState: StateFlow<AuthState?> = _loginState

    private val _registerState = MutableStateFlow<AuthState?>(null)
    val registerState: StateFlow<AuthState?> = _registerState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            if (username == "test" && password == "pass") {
                _loginState.value = AuthState(success = true)
            } else {
                _loginState.value = AuthState(error = "Invalid credentials")
            }
        }
    }

    fun register(username: String, password: String, inviteCode: String) {
        viewModelScope.launch {
            if (inviteCode == "invite123") {
                _registerState.value = AuthState(success = true)
            } else {
                _registerState.value = AuthState(error = "Invalid invite code")
            }
        }
    }
}

data class AuthState(
    val success: Boolean = false,
    val error: String? = null
)
