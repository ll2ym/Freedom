package com.freedom.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freedom.network.ApiService
import com.freedom.network.LoginRequest
import com.freedom.network.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _loginState = MutableStateFlow<AuthState?>(null)
    val loginState: StateFlow<AuthState?> = _loginState

    private val _registerState = MutableStateFlow<AuthState?>(null)
    val registerState: StateFlow<AuthState?> = _registerState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    _loginState.value = AuthState(success = true, token = response.body()?.accessToken)
                } else {
                    _loginState.value = AuthState(error = response.message())
                }
            } catch (e: Exception) {
                _loginState.value = AuthState(error = e.message)
            }
        }
    }

    fun register(username: String, password: String, inviteCode: String) {
        viewModelScope.launch {
            try {
                val response = apiService.register(RegisterRequest(username, password, inviteCode))
                if (response.isSuccessful) {
                    _registerState.value = AuthState(success = true, token = response.body()?.accessToken)
                } else {
                    _registerState.value = AuthState(error = response.message())
                }
            } catch (e: Exception) {
                _registerState.value = AuthState(error = e.message)
            }
        }
    }
}

data class AuthState(
    val success: Boolean = false,
    val token: String? = null,
    val error: String? = null
)
