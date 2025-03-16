package com.example.gofund.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.model.ExpenseTypeItem
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    navController: NavController,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        ImageAndTotal(9)
        ViewAllButton(
            onViewAllClick = {
                Log.d("CONTENT_BUTTON", "View All Clicked")
            }
        )
        LazyColumnCard()
    }

}

@Composable
fun ImageAndTotal(numberOfExpenses: Int){
    Row(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(

            modifier = Modifier
                .size(160.dp),
            painter = painterResource(id = R.drawable.expense),
            contentDescription = "expense_image",

        )
        Column(
            modifier = Modifier
                .padding(horizontal =20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Number of",
                fontFamily = IntroFamily,
                color = Color.Gray,
                fontSize = 24.sp
            )
            Text(
                text = "Expenses",
                fontFamily = IntroFamily,
                textAlign = TextAlign.Start,
                color = Color.Gray,
                fontSize = 18.sp
            )
            Column(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Text(
                    modifier = Modifier
                        .width(140.dp),
                    text = "$numberOfExpenses",
                    textAlign = TextAlign.Center,
                    fontSize = 90.sp,
                    fontFamily = IntroFamily,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun LazyColumnCard(){
    //fake repo remove later on hehe
    val fakeData = listOf(
        ExpenseTypeItem("expense", 100, "Burger", "yummy soo tastyy", "10-10-2024"),
        ExpenseTypeItem("expense", 100, "Fries", "yummy soo tastyy", "10-10-2024"),
        ExpenseTypeItem("expense", 100, "Chicken", "yummy soo tastyy", "10-10-2024"),

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
            items(fakeData) { data ->
                ExpenseItemHolder(data)
            }
        }
    }
}

@Composable
fun ExpenseItemHolder(expenseItem: ExpenseTypeItem){
    val expenseTypeImage = if (expenseItem.expenseType == "expense") painterResource(id = R.drawable.expense_type) else painterResource(id = R.drawable.expense)
    Card(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ){
        Row (
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                modifier = Modifier
                    .size(60.dp),
                painter = expenseTypeImage, contentDescription = "item type image"
            )
            Text(
                text = "${expenseItem.title}",
                fontFamily = PoppinsFamily,
                fontSize = 22.sp,
                color = Color.Black
            )
            Column {
                Text(
                    text = "₱${expenseItem.amount}",
                    fontFamily = IntroFamily,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "${expenseItem.timeStamp}",
                    fontFamily = PoppinsFamily,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun ViewAllButton(onViewAllClick: () -> Unit){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            modifier = Modifier
                .weight(1f),
            fontFamily = IntroFamily,
            fontSize = 16.sp,
            text = "Expense List",
            color = Color.Gray
        )
        OutlinedButton(
            modifier = Modifier
                ,
            onClick = {
                onViewAllClick()
            }
        ) {
            Text(
                modifier = Modifier
                    ,
                text = "View All",
                fontFamily = PoppinsFamily,
                fontSize = 13.sp,
                color = Color.Black
            )
        }
    }
}



@Preview
@Composable
fun PreviewExpenseScreen(){
    val navController = rememberNavController()
    ExpenseScreen(navController)
}

@Preview(showBackground = true)
@Composable
fun PreviewItem(){
    val fakeData = ExpenseTypeItem("Expense", 100, "Burger", "yummy soo tastyy", "10-10-2024")
    ExpenseItemHolder(fakeData)

}