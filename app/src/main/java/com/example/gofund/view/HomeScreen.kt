package com.example.gofund.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.navigations.BottomBarScreen
import com.example.gofund.navigations.Screen
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily


@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var value by remember { mutableIntStateOf(50) }
    var userName = "Kenley"
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier= Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            text = "Hello $userName",
            fontFamily = IntroFamily,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp
        )
        Box(
            modifier = Modifier
                .background(Color.White)
                .wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            CustomComponent(
                indicatorValue = value,
                canvasSize = 280.dp,
                backgroundIndicatorStrokeWidth = 80f,
                foregroundIndicatorStrokeWidth = 80f,
            )
            TitleText(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            )
        }
        ContentButtons(
            navController = navController,
            onAddFundClick = {
                Log.d("CONTENT_BUTTON", "Add Fund Clicked")
                navController.navigate(Screen.AddFundScreen.route)
            },
            onEditFundClick = {
                Log.d("CONTENT_BUTTON", "Edit Fund Clicked")
                navController.navigate(Screen.EditFundScreen.route)
            },
            onAccountClick = {
                Log.d("CONTENT_BUTTON", "Account Clicked")
                navController.navigate(Screen.AccountScreen.route)
            }
        )

    }
}

@Composable
fun TitleText(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = "Go Fund",
            fontFamily = IntroFamily,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp
        )
        Text(
            text = "your financial navigator",
            fontFamily = PoppinsFamily,
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 17.sp,
        )
    }

}

@Composable
fun ContentButtons(navController: NavHostController, onAddFundClick: () -> Unit, onEditFundClick : () -> Unit, onAccountClick : () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
                .height(300.dp)
                .clickable{
                    onAddFundClick()
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_fund),
                    contentDescription = "Vector",
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .width(100.dp)
                        .height(100.dp)
                    )
                Text(
                    text = "Add Fund",
                    fontFamily = IntroFamily,
                    fontSize = 22.sp
                )
            }

        }
        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
                .height(300.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Card(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxSize()
                    .weight(1f)
                    .clickable{
                        onEditFundClick()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(id = R.drawable.edit_fund),
                        contentDescription = "Vector",
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .width(60.dp)
                            .height(60.dp)
                    )
                    Text(
                        text = "Edit Fund",
                        fontFamily = IntroFamily,
                        fontSize = 17.sp
                    )
                }

            }
            Card(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxSize()
                    .weight(1f)
                    .clickable{
                        onAccountClick()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(id = R.drawable.account),
                        contentDescription = "Vector",
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .width(60.dp)
                            .height(60.dp)
                    )
                    Text(
                        text = "Account",
                        fontFamily = IntroFamily,
                        fontSize = 17.sp
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}

