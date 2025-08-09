package com.freedom.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.freedom.auth.LoginScreen
import com.freedom.auth.RegisterScreen
import com.freedom.storage.SecurePrefs
import com.freedom.ui.chat.ChatScreen
import com.freedom.ui.pin.PinScreen
import com.freedom.ui.pin.SetPinScreen
import com.freedom.ui.screens.HomeScreen
import com.freedom.ui.screens.OnboardingScreen
import com.freedom.ui.screens.SplashScreen
import com.freedom.ui.settings.SettingsScreen

import androidx.hilt.navigation.compose.hiltViewModel
import com.freedom.auth.AuthViewModel

@Composable
fun NavGraph(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController)
        }

        composable("set_pin") {
            SetPinScreen(onPinSet = {
                navController.navigate("home") {
                    popUpTo("set_pin") { inclusive = true }
                }
            })
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("pin") {
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
                    navController.navigate("set_pin") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        composable("pin") {
            PinScreen(onPinSuccess = {
                navController.navigate("home") {
                    popUpTo("pin") { inclusive = true }
                }
            })
        }

            composable("home") {
                HomeScreen(
                    navController = navController,
                    onNewChatClick = {
                        navController.navigate("chat/new")
                    }
                )
            }

            composable("chat/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ChatScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen()
            }
        }
    }
}