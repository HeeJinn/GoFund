package com.example.gofund.navigations

const val ROOT_ROUTE = "root"
const val AUTHENTICATION_ROUTE = "authentication"

sealed class Screen(val route: String) {
    object LoginScreen: Screen(route = "login_screen")
    object RegisterScreen: Screen(route = "register_screen")

}