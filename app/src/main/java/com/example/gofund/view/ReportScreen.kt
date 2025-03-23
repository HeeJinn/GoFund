package com.example.gofund.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
){
    var selectedIndex by remember { mutableStateOf(0) }
    val reportOptions = listOf("Weekly", "Monthly", "Annually")
    var weeklyExpense by remember { mutableStateOf(0) }
    var weeklyInvestment by remember { mutableStateOf(0) }
    var monthlyExpense by remember { mutableStateOf(10) }
    var monthlyInvestment by remember { mutableStateOf(110) }
    var annualExpense by remember { mutableStateOf(1110) }
    var annualInvestment by remember { mutableStateOf(1110) }

    var totalExpense = if (selectedIndex == 0) weeklyExpense else if (selectedIndex == 1) monthlyExpense else annualExpense
    var totalInvestment = if (selectedIndex == 0) weeklyInvestment else if (selectedIndex == 1) monthlyInvestment else annualInvestment






    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 10.dp)
        ) {
            reportOptions.forEachIndexed { index, option ->
                SegmentedButton(
                    onClick = {
                        selectedIndex = index
                    },
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                    selected = selectedIndex == index,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = Color.Black,

                        ),
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = reportOptions.size)
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomComponent(
                canvasSize = 175.dp,
                backgroundIndicatorStrokeWidth = 50f,
                smallText = "Total",
                bigTextSuffix = "%",
                maxIndicatorValue = 1000,
                indicatorValue = totalExpense,
                bigTextFontSize = 27.sp,
                foregroundIndicatorStrokeWidth = 50f
                )

            CustomComponent(
                canvasSize = 175.dp,
                backgroundIndicatorStrokeWidth = 50f,
                smallText = "Total",
                bigTextSuffix = "%",
                maxIndicatorValue = 1000,
                indicatorValue = totalInvestment,
                foregroundIndicatorStrokeWidth = 50f,
                bigTextFontSize = 27.sp,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center, // Add spacing and center
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = "Expense",
                fontFamily = IntroFamily,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Text(
                modifier = Modifier
                    .weight(1f),
                text = "Investment",
                fontFamily = IntroFamily,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 25.dp, vertical = 25.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor  = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = Modifier.weight(1f).padding(start = 20.dp),
                        text = "Total\nExpense",
                        fontFamily = PoppinsFamily,
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "₱ $totalExpense",
                        fontFamily = IntroFamily,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray),
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = Modifier.weight(1f).padding(start = 20.dp),
                        text = "Total\nInvestment",
                        fontFamily = PoppinsFamily,
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "₱ $totalInvestment",
                        fontFamily = IntroFamily,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowReportScreen(){
    val navController = rememberNavController()
    ReportScreen(navController = navController)
}
