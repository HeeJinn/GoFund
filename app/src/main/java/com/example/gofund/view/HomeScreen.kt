package com.example.gofund.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.navigations.Screen
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily
import com.example.gofund.viewmodel.BSheetViewModel
import com.example.gofund.viewmodel.HomeViewModel
import com.example.gofund.viewmodel.UserDataResult
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class) // Keep for ModalBottomSheet
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(),
    bSheetViewModel: BSheetViewModel = viewModel()
) {


    // --- Observe state from ViewModels ---
    val userDataResult by homeViewModel.userDataState.collectAsStateWithLifecycle()
    val showBottomSheet by bSheetViewModel.openBottomSheet.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Column(
        // Use the modifier parameter passed into the function
        modifier = modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- Handle different data states ---
        when (val result = userDataResult) {
            is UserDataResult.Loading -> {
                // Display loading indicators
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading...")
            }
            is UserDataResult.Success -> {
                // --- Display UI using data from ViewModel state ---
                val userData = result.userData
                Log.d("HomeScreen", "Displaying data: $userData")

                Text(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    text = "Hello ${userData.userName ?: "User"}", // Use data from VM
                    fontFamily = IntroFamily,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .wrapContentSize()
                        .clickable(
                            onClick = {
                                // Open sheet using BSheetViewModel
                                bSheetViewModel.openSheet()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CustomComponent(
                        indicatorValue = userData.totalAmount ?: 0, // Use data from VM
                        canvasSize = 280.dp,
                        backgroundIndicatorStrokeWidth = 80f,
                        foregroundIndicatorStrokeWidth = 80f,
                    )
                    TitleText( // Your UI - Unchanged
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                    )
                }
                ContentButtons( // Your UI - Unchanged
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
                // --- End of UI display on Success ---
            }
            is UserDataResult.Error -> {

                val errorMessage = result.message
                Text(
                    "Error: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.padding(20.dp)
                )

                LaunchedEffect(errorMessage) {
                    if (errorMessage.contains("User not logged in") || errorMessage.contains("not found")) {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }
        } // --- End When block ---

    } // End Main Column

    if (showBottomSheet) {
        val context = LocalContext.current
        var showCustomResetDialog by remember { mutableStateOf(false) }
        val currentTotalAmountForSheet = when (val result = userDataResult) {
            is UserDataResult.Success -> result.userData.totalAmount ?: 0
            else -> 0 // Show 0 if data is loading or in error state when sheet opens
        }
        ModalBottomSheet(
            onDismissRequest = {
                bSheetViewModel.closeSheet()
            },
            sheetState = bottomSheetState
        ) {
            // Your Bottom Sheet Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetItem(
                    totalAmount = currentTotalAmountForSheet,
                    onCancelClick = {
                        scope
                            .launch {bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible){
                                    bSheetViewModel.closeSheet()
                                }
                            }
                    },
                    onSetFundClick = {selectedLimit ->
                        bSheetViewModel.updateFundLimit(selectedLimit)
                        Toast.makeText(context, "Fund Limit has set to ₱$selectedLimit", Toast.LENGTH_SHORT).show()

                        scope
                            .launch {bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible){
                                    bSheetViewModel.closeSheet()
                                }
                            }
                    },
                    onResetFund = {
                        showCustomResetDialog = true
                    }
                )
            }

        }
        if (showCustomResetDialog) {
            CustomResetDialog(
                onDismiss = {
                    showCustomResetDialog = false },
                onConfirm = {
                    bSheetViewModel.resetFund()
                    showCustomResetDialog = false
                }
            )
        }
    }
}

@Composable
fun CustomResetDialog(onDismiss: () -> Unit, onConfirm: () -> Unit){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .width(700.dp)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        modifier = Modifier
                            .padding(30.dp),
                        text = "Reset Fund",
                        fontFamily = IntroFamily,
                        fontSize = 30.sp,
                        color = Color.White
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(20.dp),
                        text = "Are you sure you want to reset your fund",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.warning_vector), contentDescription = "Error ICon", modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.error
                    )
                    Row(
                        modifier = Modifier
                            .padding(start = 15.dp, top = 25.dp, end = 15.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(1f),
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            )
                        )  {
                            Text(
                                text = "No",
                                fontFamily = PoppinsFamily,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )
                        Button(
                            modifier = Modifier
                                .weight(1f),
                            onClick = onConfirm,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Yes",
                                fontFamily = PoppinsFamily,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                }

            }
        }
    }

}


@Composable
fun BottomSheetItem(totalAmount: Int, onCancelClick:() -> Unit, onSetFundClick:(limit: Int) -> Unit, onResetFund:()->Unit){
    var sliderValue by remember { mutableStateOf(100.0f) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Fund Limit", fontFamily = IntroFamily, fontSize = 25.sp, color = MaterialTheme.colorScheme.primary)
        Text(
            "₱ ${sliderValue.toInt()}".format(sliderValue),
            fontFamily = IntroFamily,
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
            },
            valueRange = 100f..10000f,
        )
        Text(
            "When you establish a fund limit, the application will notify you as a reminder of the maximum spending amount you defined.",
            fontFamily = PoppinsFamily,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            fontSize = 14.sp,
            color = Color.Black
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = onCancelClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)

            ) {
                Text(
                "Close",
                    fontSize = 17.sp
                )
            }
            Spacer(
                modifier = Modifier
                    .width(10.dp)
            )
            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {onSetFundClick(sliderValue.toInt())},
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)

            ) {
                Text(
                "Add",
                    fontSize = 17.sp
                )
            }
        }
        Spacer(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 20.dp)
                .height(2.dp)
                .background(Color.LightGray)
                .clip(RoundedCornerShape(percent = 50))
                .fillMaxWidth()
        )
        Text("Reset Fund", fontFamily = IntroFamily, fontSize = 25.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(10.dp))
        CustomComponent(
            indicatorValue = totalAmount, // Use data from VM
            canvasSize = 280.dp,
            backgroundIndicatorStrokeWidth = 80f,
            foregroundIndicatorStrokeWidth = 80f,
        )
        OutlinedButton(
            onClick = onResetFund,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White,
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(5.dp),
        ) {
            Text("Reset Fund", fontSize = 17.sp)
        }



    }
    Log.d("SLIDER", "${sliderValue.toInt()}")
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomSheetItem(){
    CustomResetDialog(
        onDismiss = {},
        onConfirm = {}
    )
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
                .clickable { onAddFundClick() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_fund),
                    contentDescription = "Add Fund", // Corrected from Vector
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .size(100.dp)
                )
                Text( text = "Add Fund", fontFamily = IntroFamily, fontSize = 22.sp )
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
                    .clickable { onEditFundClick() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(id = R.drawable.edit_fund),
                        contentDescription = "Edit Fund",
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .size(60.dp)
                    )
                    Text( text = "Edit Fund", fontFamily = IntroFamily, fontSize = 17.sp )
                }
            }
            Card(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxSize()
                    .weight(1f)
                    .clickable { onAccountClick() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(id = R.drawable.account),
                        contentDescription = "Account",
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .size(60.dp)
                    )
                    Text( text = "Account", fontFamily = IntroFamily, fontSize = 17.sp )
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