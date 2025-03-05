package com.example.gofund.navigations

const val ROOT_ROUTE = "root"
const val CONTENT_ROUTE = "content"

sealed class Screen(val route: String) {
    object LoginScreen: Screen(route = "login_screen")
    object RegisterScreen: Screen(route = "register_screen")
    object ContentScreen: Screen(route = "content_screen")

}