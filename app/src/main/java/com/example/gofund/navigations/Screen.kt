package com.example.gofund.navigations

sealed class Screen(val route: String) {
    object LoginScreen: Screen(route = "login_screen")
    object RegisterScreen: Screen(route = "register_screen")
    object HomeScreen: Screen(route = "home_screen")

}