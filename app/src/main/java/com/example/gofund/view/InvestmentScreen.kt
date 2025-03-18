package com.example.gofund.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.model.ExpenseTypeItem
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentScreen(
    navController: NavController,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val fakeInvestRepo = listOf(
            ExpenseTypeItem("investment", 100, "Gaming Laptop", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "Iphone 22", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "OnaHole Flesh", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "Gaming Laptop", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "Iphone 22", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "OnaHole Flesh", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "Gaming Laptop", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "Iphone 22", "damn ive been dreaming for this item", "10-10-2024"),
            ExpenseTypeItem("investment", 100, "OnaHole Flesh", "damn ive been dreaming for this item", "10-10-2024"),


        )
        ImageAndTotal(numberOfExpenses = fakeInvestRepo.size, typeOfExpense = "Investment")
        ViewAllButton(
            typeOfExpense = "Investment",
            onViewAllClick = {
                Log.d("CONTENT_BUTTON", "View All Clicked")
            }
        )
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth()
                .height(400.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(fakeInvestRepo) { data ->
                    ExpenseItemHolder(expenseItem = data, navController = navController)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowInvestmentScreen(){
    val navController = rememberNavController()
    InvestmentScreen(navController)
}