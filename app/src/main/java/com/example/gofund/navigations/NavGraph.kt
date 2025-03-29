package com.example.gofund.navigations

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.gofund.view.AccountScreen
import com.example.gofund.view.AddFundScreen
import com.example.gofund.view.ContentMainScreen
import com.example.gofund.view.EditFundScreen
import com.example.gofund.view.ExpenseDetailScreen
import com.example.gofund.view.ForgotPasswordScreen
import com.example.gofund.view.HomeScreen
import com.example.gofund.view.LoginScreen
import com.example.gofund.view.RegisterScreen
import com.example.gofund.view.WholeExpenseScreen
import com.example.gofund.view.WholeInvestmentScreen

@Composable
fun SetupNavGraph(navController: NavHostController){ //Main Nav Graph
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
        composable(route = Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navController)
        }
        composable(route = Screen.ContentScreen.route){
            ContentMainScreen(navController)
        }
        composable(route = Screen.AddFundScreen.route){
            AddFundScreen(navController)
        }
        composable(route = Screen.EditFundScreen.route ){
            EditFundScreen(navController)
        }
        composable(route = Screen.AccountScreen.route) {
            AccountScreen(navController)
        }
        composable(route = Screen.WholeExpenseScreen.route) {
            WholeExpenseScreen(navController)
        }
        composable(route = Screen.WholeInvestmentScreen.route) {
            WholeInvestmentScreen(navController)
        }
        composable(
            route = Screen.DetailScreen.route,
            arguments = listOf(
                navArgument(ARGUMENT_EXPENSE_TYPE) {
                    type = NavType.StringType
                },
                navArgument(ARGUMENT_AMOUNT_TYPE) {
                    type = NavType.IntType
                },
                navArgument(ARGUMENT_TITLE_TYPE) {
                    type = NavType.StringType
                },
                navArgument(ARGUMENT_NOTE) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(ARGUMENT_TIMESTAMP) {
                    type = NavType.StringType
                }
            )
        ) {
            val expenseType = it.arguments?.getString(ARGUMENT_EXPENSE_TYPE) ?: ""
            val amount = it.arguments?.getInt(ARGUMENT_AMOUNT_TYPE) ?: 0
            val title = it.arguments?.getString(ARGUMENT_TITLE_TYPE) ?: ""
            val note = it.arguments?.getString("note") ?: ""
            val timestamp = it.arguments?.getString(ARGUMENT_TIMESTAMP) ?: ""
            ExpenseDetailScreen(navController, expenseType, amount, title, note, timestamp)
            Log.d("PASSED_ARGUMENTS", "$expenseType, $amount, $title, $note, $timestamp")


        }


    }

}