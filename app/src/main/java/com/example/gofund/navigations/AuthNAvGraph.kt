package com.example.gofund.navigations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.gofund.view.CreateExpenseScreen
import com.example.gofund.view.ExpenseScreen
import com.example.gofund.view.HomeScreen
import com.example.gofund.view.InvestmentScreen
import com.example.gofund.view.ReportScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
){
    navigation(
        startDestination = BottomBarScreen.Home_Screen.route,
        route = AUTHENTICATION_ROUTE
    ){
        composable(route = BottomBarScreen.Expense_Screen.route){
            ExpenseScreen(navController)
        }
        composable(route = BottomBarScreen.Create_Screen.route){
            CreateExpenseScreen(navController)
        }
        composable(route = BottomBarScreen.Investment_Screen.route){
            InvestmentScreen(navController)
        }
        composable(route = BottomBarScreen.Report_Screen.route){
            ReportScreen(navController)
        }
        composable(route= BottomBarScreen.Home_Screen.route){
            HomeScreen(navController)
        }
    }
}