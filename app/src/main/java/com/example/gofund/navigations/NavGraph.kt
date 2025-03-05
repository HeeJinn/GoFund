package com.example.gofund.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.gofund.view.ContentMainScreen
import com.example.gofund.view.HomeScreen
import com.example.gofund.view.LoginScreen
import com.example.gofund.view.RegisterScreen

@Composable
fun SetupNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route,
        route = ROOT_ROUTE
    ) {
        composable(route = Screen.LoginScreen.route){
            LoginScreen(navController = navController)
        }
        composable(route = Screen.RegisterScreen.route){
            RegisterScreen(navController = navController)

        }
        composable(route = Screen.ContentScreen.route){
            ContentMainScreen()
        }

//        bottomNavGraph(navController)

    }

}