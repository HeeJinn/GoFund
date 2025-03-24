package com.example.gofund.view


import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.model.UserData
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundScreen(navController: NavController){
    var isButtonEnabled by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Fund", fontFamily = IntroFamily) },
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
    ) {
        val firebase = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/") // Correct URL
        val dbRef = firebase.getReference("goFund")
        val userID = Firebase.auth.currentUser?.uid
        val context = LocalContext.current

        var totalAmount by remember { mutableIntStateOf(0) }
        var initialAmount by remember { mutableIntStateOf(0) }
        var amount by remember { mutableStateOf("") }
        val maxAmountLength = 6

        LaunchedEffect(key1 = userID) {
            if (userID != null) {
                dbRef.child(userID).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val userData = snapshot.getValue(UserData::class.java)
                            totalAmount = userData?.totalAmount ?: 0 // Fetch totalAmount
                            initialAmount = userData?.initialAmount ?: 0

                        } else {
                            Log.e("Firebase", "no data found ")
                        }
                    }
                    .addOnFailureListener {
                        Log.e("Firebase", "Error getting data", it)
                    }
            } else {
                Log.e("Firebase", "User not signed in")

            }
        }
        Log.d("AMOUNT", "$totalAmount $initialAmount")

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 30.dp)
            )
            CircularProgressBarAndTotal(totalAmount, initialAmount)
            CardAndInput(
                amount = amount,
                onAmountChange = {
                    if (it.all{ it.isDigit()} && it.length <= maxAmountLength){
                        amount = it
                    }
                }
            )
            AddFundButtons(
                onClearButtonClick = {
                    amount = ""
                },
                onAddButtonClick = {
                    if (amount.isEmpty()){
                        Toast.makeText(context, "Amount cannot be empty", Toast.LENGTH_SHORT).show()
                        return@AddFundButtons
                    }
                    totalAmount += amount.toInt()
                    var newTotalAmount = checkIfHigherThanMax(totalAmount)
                    totalAmount = newTotalAmount
                    initialAmount += amount.toInt()
                    var newInitialAmount = checkIfHigherThanMax(initialAmount)
                    initialAmount = newInitialAmount
                    Log.d("AMOUNT", "$totalAmount $initialAmount")
                    dbRef.child(userID.toString()).child("totalAmount").setValue(newTotalAmount)
                    dbRef.child(userID.toString()).child("initialAmount").setValue(newInitialAmount)
                    amount = ""
                }

            )
        }
    }
}
fun checkIfHigherThanMax(amount: Int): Int{
    return if (amount > 100000) 100000 else amount
}

@Composable
fun CircularProgressBarAndTotal(currentAmount: Int, initialAmount: Int, isForEditFund: Boolean = false){
    Row(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomComponent(
            canvasSize = 190.dp,
            indicatorValue = currentAmount,
            foregroundIndicatorStrokeWidth = 40f,
            backgroundIndicatorStrokeWidth = 40f,
        )
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start)
        {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = if (isForEditFund) checkIfLowerThanZero(currentAmount).toString() else checkIfHigherThanMax(currentAmount).toString(),
                fontFamily = IntroFamily,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.LightGray)
                    .clip(shape = MaterialTheme.shapes.medium)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = if (isForEditFund) checkIfLowerThanZero(initialAmount).toString() else checkIfHigherThanMax(initialAmount).toString(),
                fontFamily = IntroFamily,
                fontSize = 40.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CardAndInput(amount: String, onAmountChange: (String) -> Unit){
    var isFocus = LocalFocusManager.current
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
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                text = "Add Fund to Monitor",
                fontFamily = IntroFamily,
                color = Color.White,
                fontSize = 20.sp,
            )
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.White)
                    .clip(shape = MaterialTheme.shapes.medium)
            )
            TextField(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
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
                        modifier = Modifier
                            .size(33.dp)
                            .alpha(0.7f)
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
}

@Composable
fun AddFundButtons(onClearButtonClick: () -> Unit, onAddButtonClick: () -> Unit){
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
            onClick = onClearButtonClick,
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
            onClick = onAddButtonClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = Color.Transparent
            ),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Add",
                fontFamily = PoppinsFamily,
                fontSize = 17.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAddFundScreen(){
    val navController = rememberNavController()
    AddFundScreen(navController)
}