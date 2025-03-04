package com.example.gofund.navigations

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object  Expense_Screen: BottomBarScreen(
        route = "expense_screen",
        title = "Expense",
        icon = Icons.Rounded.ShoppingCart
    )object  Create_Screen: BottomBarScreen(
        route = "create_screen",
        title = "Create",
        icon = Icons.Rounded.Add
    )
    object  Investment_Screen: BottomBarScreen(
        route = "investment_screen",
        title = "Investment",
        icon = Icons.Rounded.Info
    )
    object  Report_Screen: BottomBarScreen(
        route = "report_screen",
        title = "Report",
        icon = Icons.Rounded.AccountBox
    )
    object  Home_Screen: BottomBarScreen(
        route = "home_screen",
        title = "Home",
        icon = Icons.Rounded.Home
    )


}