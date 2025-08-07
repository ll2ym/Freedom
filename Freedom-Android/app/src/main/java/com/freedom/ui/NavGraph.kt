package com.freedom.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.freedom.auth.LoginScreen
import com.freedom.auth.RegisterScreen
import com.freedom.ui.screens.SplashScreen
import com.freedom.ui.screens.OnboardingScreen
import com.freedom.ui.screens.HomeScreen
import com.freedom.ui.screens.SettingsScreen
import com.freedom.ui.chat.ChatScreen
import com.freedom.storage.SecurePrefs

import com.freedom.ui.pin.PinScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }
    var isPinAuthenticated by remember { mutableStateOf(false) }

    // Check if user is already logged in
    LaunchedEffect(Unit) {
        val token = SecurePrefs.getString("auth_token")
        isLoggedIn = !token.isNullOrEmpty()
    }

    if (!isPinAuthenticated) {
        PinScreen(onPinSuccess = { isPinAuthenticated = true })
    } else {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "splash"
        ) {
            composable("splash") {
                SplashScreen(navController)
            }

            composable("onboarding") {
                OnboardingScreen(navController)
            }

            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        isLoggedIn = true
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login")
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    navController = navController,
                    currentUser = getCurrentUser(),
                    recentChats = getRecentChats(),
                    onNewChatClick = {
                        navController.navigate("chat/new")
                    }
                )
            }

            composable("chat/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ChatScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    currentUser = getCurrentUser(),
                    onLogout = {
                        SecurePrefs.clear()
                        isLoggedIn = false
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onResetKeys = {
                        // Reset encryption keys
                    }
                )
            }
        }
    }
}

// Helper functions - these should be moved to proper repositories
private fun getCurrentUser(): com.freedom.data.User {
    val username = SecurePrefs.getString("username", "")
    return com.freedom.data.User(
        username = username,
        displayName = username,
        isTrusted = true
    )
}

private fun getRecentChats(): List<com.freedom.data.User> {
    // This should fetch from database
    return emptyList()
}