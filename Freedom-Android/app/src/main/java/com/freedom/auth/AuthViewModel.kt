package com.freedom.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freedom.storage.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.freedom.network.ApiService
import com.freedom.network.LoginRequest
import com.freedom.network.RegisterRequest

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val securePrefs: SecurePrefs,
    private val apiService: ApiService
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        viewModelScope.launch {
            val token = securePrefs.getString("auth_token")
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    securePrefs.putString("auth_token", response.body()!!.accessToken)
                    _isLoggedIn.value = true
                    _loginError.value = null
                } else {
                    _loginError.value = "Invalid credentials"
                }
            } catch (e: Exception) {
                _loginError.value = "An error occurred"
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            securePrefs.clear()
            _isLoggedIn.value = false
        }
    }

    fun register(username: String, password: String, inviteCode: String) {
        viewModelScope.launch {
            try {
                val response = apiService.register(RegisterRequest(username, password, inviteCode))
                if (response.isSuccessful && response.body() != null) {
                    securePrefs.putString("auth_token", response.body()!!.accessToken)
                    _isLoggedIn.value = true
                    _loginError.value = null
                } else {
                    _loginError.value = response.errorBody()?.string() ?: "An error occurred"
                }
            } catch (e: Exception) {
                _loginError.value = "An error occurred"
            }
        }
    }
}
