package com.example.gofund.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.navigations.Screen
import com.example.gofund.ui.theme.GoFundTheme
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeYellow
import com.example.gofund.ui.theme.PoppinsFamily

@Composable
fun RegisterScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()){
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember{ mutableStateOf("")}
    var isLoading by remember { mutableStateOf(false) }


    GoFundTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ){
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color(LightModeLightBlue.value)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 7.dp),
                        text = "SIGN UP",
                        fontWeight = FontWeight.Bold,
                        fontFamily = IntroFamily,
                        fontSize = 60.sp,
                        color = Color.White
                    )
                    BackToLogin(
                        onLoginClick = {
                            navController.popBackStack()
                        }
                    )
                    EmailRegister(
                        email = email,
                        onTextChange = {
                            email = it
                        }
                    )
                    UsernameRegister(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        username = username,
                        onValueChange = {
                            username = it
                        }
                    )
                    PasswordRegister(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        registerPass = password,
                        onPasswordValueChange = {
                            password = it
                        }
                    )
                    SignupButton(
                        modifier = Modifier
                            .padding(top = 30.dp)
                        ,navController = navController
                    ) {
                        if (email.isEmpty() || username.isEmpty() || password.isEmpty()){
                            Toast.makeText(context, "Make sure to fill all the text field", Toast.LENGTH_SHORT).show()
                        }else{
                            isLoading = true
                            loginViewModel.signUp(email, username, password,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Verification sent to your email", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.LoginScreen.route){
                                        popUpTo(Screen.LoginScreen.route){
                                            inclusive = true
                                        }
                                    }
                                },
                                onFailure = { exception ->
                                    isLoading = false
                                    errorMsg = exception
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                })
                        }

                    }

                }

            }
        }
        // ✅ Floating Circular Progress
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Dim background
                    .clickable(enabled = false) {}, // Prevent clicks when loading
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .size(50.dp)

                )
            }
        }
    }

}

@Composable
fun BackToLogin(modifier : Modifier = Modifier, onLoginClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        pushStyle(
            SpanStyle(
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontSize = 13.sp
            )
        )
        append("Already a member? ")

        pushStyle(
            SpanStyle(
                color = Color(LightModeYellow.value),
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                fontFamily = IntroFamily
            )
        )
        append("Log In")
        pop()
    }

    Text(
        text = annotatedText,
        modifier = Modifier.clickable { onLoginClick() }
    )
}

@Composable
fun EmailRegister(modifier: Modifier= Modifier, email: String, onTextChange: (String) -> Unit){
     var isFocused by remember { mutableStateOf(false) }
     var focusedLabelColor = if (email.isNotEmpty() || isFocused) Color.White else Color.LightGray
     val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .padding(10.dp)
            .width(278.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        value = email,
        onValueChange = onTextChange,
        maxLines = 1,
        shape = MaterialTheme.shapes.medium,
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = Color.Black,
            fontSize = 16.sp
        ),
        colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                    focusedLabelColor = focusedLabelColor,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                    unfocusedLabelColor = focusedLabelColor,
        ),
        placeholder = {Text(text = "Enter valid email", color = Color.LightGray)},
        label = {Text(text = "Email", fontFamily = PoppinsFamily)},
        leadingIcon = { Icon(imageVector = Icons.Rounded.Email, contentDescription = "email_register") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {focusManager.clearFocus()}
        )
    )
}

@Composable
fun UsernameRegister(modifier: Modifier, username: String, onValueChange: (String) -> Unit){
    var isFocused by remember { mutableStateOf(false) }
    var focusedLabelColor = if (username.isNotEmpty() || isFocused) Color.White else Color.LightGray
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .padding(10.dp)
            .width(278.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        value = username,
        onValueChange = onValueChange,
        label = {
            Text(text = "Username", fontFamily = PoppinsFamily)
        },
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = Color.Black,
            fontSize = 16.sp
        ),
        maxLines = 1,
        placeholder = {Text(text = "Enter username", color = Color.LightGray)},
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = focusedLabelColor,
            focusedLeadingIconColor = MaterialTheme.colorScheme.background,
            unfocusedLabelColor = focusedLabelColor,
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Person, contentDescription = "username_icon")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {focusManager.clearFocus()}
        )

    )
}

@Composable
fun PasswordRegister(modifier: Modifier = Modifier, registerPass: String, onPasswordValueChange:(String) -> Unit){
    var isFocused by remember { mutableStateOf(false) }
    var focusedLabelColor = if (registerPass.isNotEmpty() || isFocused) Color.White else Color.LightGray
    var passVisibility by remember { mutableStateOf(false) }
    var icon =
        if (passVisibility) painterResource(id = R.drawable.vector_visibility)
        else painterResource(id = R.drawable.vector_not_visible)
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .padding(10.dp)
            .width(278.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        value = registerPass,
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = Color.Black,
            fontSize = 16.sp
        ),
        maxLines = 1,
        shape = MaterialTheme.shapes.medium,
        onValueChange = onPasswordValueChange,
        label = { Text(
            text = "Password",
            fontFamily = PoppinsFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ) },
        placeholder = {Text(text = "Enter password", color = Color.LightGray)},
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,  // Background when focused
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,  // Background when not focused
            focusedLabelColor = focusedLabelColor,  // Label color when focused
            focusedLeadingIconColor = MaterialTheme.colorScheme.background,
            unfocusedLabelColor = focusedLabelColor,  // Label color when not focused
            focusedTrailingIconColor = MaterialTheme.colorScheme.background,
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Lock, contentDescription = "Email")

        },
        trailingIcon = {
            IconButton(onClick = {passVisibility = !passVisibility}) {
                Icon(painter = icon, contentDescription = "visibility")
            }
        },
        visualTransformation = if (passVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {focusManager.clearFocus()}
        )
    )
}

@Composable
fun SignupButton(modifier: Modifier = Modifier,navController: NavController, onClick: () -> Unit){
    Row(
        modifier = modifier
            .width(300.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .height(1.dp)
                .background(Color.White)
        )
        Button(
            modifier = modifier,
            onClick = onClick,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(Color.White)

        ) {
            Text(
                text = "Sign up", style = TextStyle(
                fontSize = 22.sp,
                fontFamily = IntroFamily,
                fontWeight = FontWeight.Bold,
            ), color = Color.Black)
        }
        Spacer(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .height(1.dp)
                .background(Color.White)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewRegister(){
    val navController = rememberNavController()
    RegisterScreen(navController)
}