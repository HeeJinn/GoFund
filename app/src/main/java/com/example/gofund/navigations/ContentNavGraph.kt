package com.example.gofund.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gofund.view.CreateExpenseScreen
import com.example.gofund.view.ExpenseDetailScreen
import com.example.gofund.view.ExpenseScreen
import com.example.gofund.view.HomeScreen
import com.example.gofund.view.InvestmentScreen
import com.example.gofund.view.ReportScreen

@Composable
fun BottomNavGraph(navController: NavHostController, mainNavController: NavHostController) { //Child NavGraph
    NavHost(
        navController = navController,
        route = CONTENT_ROUTE,
        startDestination = BottomBarScreen.Home_Screen.route
    ) {
        composable(route = BottomBarScreen.Home_Screen.route) {
            HomeScreen(mainNavController) // Pass the mainNavController
        }
        composable(route = BottomBarScreen.Expense_Screen.route) {
            ExpenseScreen(mainNavController)  // Pass mainNavController
        }
        composable(route = BottomBarScreen.Create_Screen.route) {
            CreateExpenseScreen(mainNavController) // Pass mainNavController and other required params
        }
        composable(route = BottomBarScreen.Investment_Screen.route) {
            InvestmentScreen(mainNavController) // Pass mainNavController
        }
        composable(route = BottomBarScreen.Report_Screen.route) {
            ReportScreen(mainNavController)  //Pass main navController
        }
    }
}