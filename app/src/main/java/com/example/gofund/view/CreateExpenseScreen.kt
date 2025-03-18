package com.example.gofund.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpenseScreen(
    navController: NavController,
){
    var selectedIndex by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SegmentedButtonAndImageForExpense(
            selectedIndex = selectedIndex,
            onIndexChange = {newIndex ->
                selectedIndex = newIndex
            }
        )
        ExpenseCard()



    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonAndImageForExpense(
    selectedIndex: Int, // Pass selectedIndex as a parameter
    onIndexChange: (Int) -> Unit // Lambda to update the selectedIndex
) {
    val options = listOf("Expense", "Investment")
    val alphaImageExpense = if (selectedIndex == 0) 1f else 0.5f
    val alphaImageInvestment = if (selectedIndex == 1) 1f else 0.5f

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                onClick = {
                    onIndexChange(index) // Update the selectedIndex
                    Log.d("SELECTED_INDEX", selectedIndex.toString())
                },
                selected = selectedIndex == index,
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(
                    text = option,
                    fontFamily = PoppinsFamily
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(130.dp),
            alpha = alphaImageExpense,
            painter = painterResource(id = R.drawable.expense),
            contentDescription = "expense_image"
        )
        Image(
            modifier = Modifier
                .size(130.dp),
            alpha = alphaImageInvestment,
            painter = painterResource(id = R.drawable.investment),
            contentDescription = "expense_image"
        )
    }
}

@Composable
fun ExpenseCard(){
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White),
    ) {
        Column {
            // TODO: populate this ui later hehe 
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowCreateExpenseScreen(){
    val navController = rememberNavController()
    CreateExpenseScreen(navController = navController)

}
