package com.example.gofund.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpenseScreen(
    navController: NavController,
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create Expense Screen", fontFamily = PoppinsFamily)

//        TextField(
//            value = value.toString(),
//            onValueChange = {
//                value = if (it.isNotEmpty()){
//                    it.toInt()
//                }else{
//                    0
//                }
//            },
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number,
//                imeAction = ImeAction.Done
//            )
//        )

    }
}
