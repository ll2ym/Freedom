package com.freedom.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.freedom.auth.LoginScreen
import com.freedom.auth.RegisterScreen
import com.freedom.chat.ChatScreen

@Composable
fun FreedomApp() {
    FreedomTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "login") {
            composable("login") {
                LoginScreen(onLoginSuccess = { navController.navigate("chat") })
            }
            composable("register") {
                RegisterScreen(onRegisterSuccess = { navController.navigate("chat") })
            }
            composable("chat") {
                ChatScreen(messages = listOf(), onSend = {})
            }
        }
    }
}
