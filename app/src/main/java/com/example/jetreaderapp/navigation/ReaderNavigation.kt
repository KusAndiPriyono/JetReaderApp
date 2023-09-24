package com.example.jetreaderapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetreaderapp.screens.ReaderSplashScreen
import com.example.jetreaderapp.screens.home.Home
import com.example.jetreaderapp.screens.login.ReaderLoginScreen
import com.example.jetreaderapp.screens.signup.ReaderSignUpScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            ReaderSplashScreen(navController = navController)
        }

        composable(ReaderScreens.LoginScreen.name) {
            ReaderLoginScreen(navController = navController)
        }

        composable(ReaderScreens.CreateAccountScreen.name) {
            ReaderSignUpScreen(navController = navController)
        }

        composable(ReaderScreens.ReaderHomeScreen.name) {
            Home(navController = navController)
        }
    }
}