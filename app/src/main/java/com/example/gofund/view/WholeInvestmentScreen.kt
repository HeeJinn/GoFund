package com.example.gofund.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.model.ExpenseTypeItem
import com.example.gofund.ui.theme.IntroFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WholeInvestmentScreen(navController: NavController){
    var isButtonEnabled by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Investments", fontFamily = IntroFamily, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        enabled = isButtonEnabled,
                        onClick = {
                            navController.popBackStack()
                            isButtonEnabled = !isButtonEnabled
                            Log.d("CONTENT_BUTTON", "Back Button Clicked $isButtonEnabled")
                        }
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "arrow_button", modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ){
        val fakeInvestRepo = listOf(
            ExpenseTypeItem(key = "asd", expenseType = "investment", amount = 100, title = "Titw", note = "asdasd", timeStamp = "10-10-25")
//            ExpenseTypeItem("investment", 100, "Gaming Laptop", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "Iphone 22", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "OnaHole Flesh", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "Gaming Laptop", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "Iphone 22", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "OnaHole Flesh", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "Gaming Laptop", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "Iphone 22", "damn ive been dreaming for this item", "10-10-2024"),
//            ExpenseTypeItem("investment", 100, "OnaHole Flesh", "damn ive been dreaming for this item", "10-10-2024"),
        )

        LazyColumn(
            modifier = Modifier
                .padding(it)
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(fakeInvestRepo){
                ExpenseItemHolder(expenseItem = it, navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowWholeInvestmentScreen(){
    var navController = rememberNavController()
    WholeInvestmentScreen(navController)
}