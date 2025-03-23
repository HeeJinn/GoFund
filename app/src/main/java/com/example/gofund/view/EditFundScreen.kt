package com.example.gofund.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFundScreen(navController: NavController){
    var isButtonEnabled by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Fund", fontFamily = IntroFamily) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                            isButtonEnabled = !isButtonEnabled


                        },
                        enabled = isButtonEnabled
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "back_vector", modifier = Modifier.size(50.dp), tint = Color.White)
                    }
                }
            )
        }
    ){
        var currentAmount by remember { mutableStateOf(5000) }
        var initialAmount by remember { mutableStateOf(10000) }
        var amount by remember { mutableStateOf("") }
        val maxAmountLength = 6

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(100.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Edit Expenses\nNow",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    color = Color.White,
                    fontFamily = IntroFamily,
                    fontSize = 25.sp
                )
            }
            CircularProgressBarAndTotal(currentAmount, initialAmount)
            CardWithTextFieldAndTitle(
                amount = amount,
                onAmountChange = {
                    if (it.all{ it.isDigit()} && it.length <= maxAmountLength){
                        amount = it
                    }
                }
            )
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .weight(1f),
                    onClick = {
                        amount = ""
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Clear",
                        fontFamily = PoppinsFamily,
                        fontSize = 17.sp
                    )
                }
                OutlinedButton(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f),
                    onClick = {

                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Decrease",
                        fontFamily = PoppinsFamily,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}
@Composable
fun CardWithTextFieldAndTitle(amount: String, onAmountChange: (String) -> Unit){
    var isFocus = LocalFocusManager.current
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 5.dp),
        text = "Decrease Initial Amount",
        fontFamily = PoppinsFamily,
        color = Color.Gray,
        fontSize = 18.sp
    )
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        TextField(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth(),
            value = amount,
            onValueChange = onAmountChange,
            textStyle = TextStyle(
                fontFamily = IntroFamily,
                fontSize = 20.sp
            ),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.philippine_peso),
                    contentDescription = "add_fund_icon",
                    modifier = Modifier.size(33.dp).alpha(0.7f)
                )
            },
            shape = MaterialTheme.shapes.medium,
            placeholder = {
                Text(
                    text = "Amount",
                    fontFamily = IntroFamily,
                    color = Color.Gray
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.tertiary,
                unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            supportingText = {
                Text(text = "*6-max characters", color = Color.White)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {isFocus.clearFocus()})
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewEditFundScreen(){
    val navController = rememberNavController()
    EditFundScreen(navController)
}