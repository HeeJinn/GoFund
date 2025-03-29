package com.example.gofund.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.R
import com.example.gofund.ui.theme.PoppinsFamily
import com.example.gofund.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(navController: NavController, forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()){
    var isRememberButtonEnabled by remember { mutableStateOf(true) }
    var focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading = forgotPasswordViewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = "forgot Your Password?",
                fontFamily = IntroFamily,
                color = Color.White,
                fontSize = 23.sp
            )
            Image(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(200.dp),
                painter = painterResource(id = R.drawable.forgetpassword_sticker), contentDescription = "forget_password_sticker"
            )
            Card (
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter your registered email below to receive password reset link",
                        fontFamily = PoppinsFamily,
                        color = Color.Black,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    TextField(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .fillMaxWidth(),
                        value = email,
                        textStyle = TextStyle(
                            fontFamily = PoppinsFamily,
                            color = Color.White
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Gray.copy(alpha = 0.7f),
                            unfocusedContainerColor = Color.Gray.copy(alpha = 0.7f),
                        ),
                        shape = ShapeDefaults.Medium,
                        onValueChange = {
                            email = it
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Rounded.MailOutline, contentDescription = "email_icon", tint = Color.White)
                        },
                        placeholder = {
                            Text(
                                text = "Email",
                                fontFamily = PoppinsFamily,
                                color = Color.White
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = ShapeDefaults.Medium,
                        onClick = {
                            forgotPasswordViewModel.sendPasswordResetEmail(email,
                                onSuccess = {
                                    Toast.makeText(context, "Reset link sent to your email.", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onFailure = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    ) {
                        Text(
                            text = "Send Reset Link",
                            fontFamily = PoppinsFamily,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            TextButton(
                modifier = Modifier
                    .padding(top = 30.dp),
                enabled = isRememberButtonEnabled,
                onClick = {
                    isRememberButtonEnabled = false
                    navController.popBackStack()
                }
            ) {
                Text(
                    text = "Remember Password? Login",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    textDecoration = TextDecoration.Underline,

                    )
            }

        }
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    // Prevent clicks passing through the loading overlay
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White, // Or MaterialTheme.colorScheme.primary
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPassword(){
    val navController = rememberNavController()
    ForgotPasswordScreen(navController)

}