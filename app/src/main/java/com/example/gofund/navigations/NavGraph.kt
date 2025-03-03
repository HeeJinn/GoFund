package com.example.gofund.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gofund.HomeScreen
import com.example.gofund.LoginScreen
import com.example.gofund.RegisterScreen

@Composable
fun SetupNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = Screen.LoginScreen.route){
            LoginScreen(navController = navController)
        }
        composable(route = Screen.RegisterScreen.route){
            RegisterScreen(navController = navController)

        }
        composable(route= Screen.HomeScreen.route){
            HomeScreen(navController)
        }
    }

}