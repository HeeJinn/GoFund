package com.example.gofund.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gofund.view.CreateExpenseScreen
import com.example.gofund.view.ExpenseScreen
import com.example.gofund.view.HomeScreen
import com.example.gofund.view.InvestmentScreen
import com.example.gofund.view.ReportScreen

@Composable
fun setUpContentNavGraph(navController: NavHostController){
    NavHost(
        startDestination = BottomBarScreen.Home_Screen.route,
        navController = navController,
    ) {
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