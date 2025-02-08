package com.example.gofund

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.PoppinsFamily
import com.example.gofund.ui.theme.SeaweedScriptFamily

@Composable
fun LogoText(){
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(270.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Go\nFund\nApp",
            fontFamily = IntroFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 80.sp,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = 60.sp
        )
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(160.dp),
            contentAlignment = Alignment.TopEnd
        ){
            Text(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                text = "Today",
                fontFamily = SeaweedScriptFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 75.sp,
                color = MaterialTheme.colorScheme.tertiary,
                lineHeight = 50.sp,


                )
        }

    }

}

@Composable
fun UserNameTextField(){
    var username by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var focusedLabelColor = if (username.isNotEmpty() || isFocused) Color.White else Color.LightGray
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier
            .padding(10.dp)
            .onFocusChanged{focusState ->
                isFocused = focusState.isFocused
            },
        value = username,
        onValueChange = { username = it },
        maxLines = 1,
        label = { Text(
            text = "Email",
            fontFamily = PoppinsFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ) },
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = MaterialTheme.colorScheme.background,
            fontSize = 16.sp
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = focusedLabelColor,
            focusedLeadingIconColor = MaterialTheme.colorScheme.background,
            unfocusedLabelColor = focusedLabelColor,
        ),
        placeholder = {Text(text = "Enter valid email")},
        leadingIcon ={
                Icon(imageVector = Icons.Rounded.Person, contentDescription = "Email")

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
fun PasswordTextField(modifier: Modifier = Modifier){
    var password by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var focusedLabelColor = if (password.isNotEmpty() || isFocused) Color.White else Color.LightGray
    var passVisibility by remember { mutableStateOf(false) }
    var icon =
        if (passVisibility) painterResource(id = R.drawable.vector_visibility)
        else painterResource(id = R.drawable.vector_not_visible)
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier
            .padding(10.dp)
            .onFocusChanged{focusState ->
                isFocused = focusState.isFocused
            },
        value = password,
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = MaterialTheme.colorScheme.background,
            fontSize = 16.sp
        ),
        onValueChange = {password = it},
        label = { Text(
            text = "Password",
            fontFamily = PoppinsFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ) },
        placeholder = {Text(text = "Enter password")},
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
fun ForgotPassword(modifier: Modifier = Modifier){
    Text(
        modifier = modifier
            .width(300.dp)
            .padding(end = 10.dp)
            .clickable{

            },
        text = "forgot password",
        textAlign = TextAlign.End,
        fontFamily = PoppinsFamily,
        color = Color.White,
        style = TextStyle(
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline
        )
    )
}


@Composable
fun LoginButton(modifier: Modifier = Modifier){
    Button(
        modifier = modifier
            .width(200.dp),
        onClick = {},
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(Color.White)
    ) {
        Text(
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = IntroFamily,
                fontWeight = FontWeight.Bold,
            ),
            color = Color.Black,
            text = "Login")
    }
}

@Composable
fun LoginScreen(){

}

@Preview(showBackground = true)
@Composable
fun PreviewCompose(){
    Surface(
        modifier = Modifier
            .width(437.dp)
            .height(971.dp),
        color = Color(LightModeLightBlue.value)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            LogoText()
            UserNameTextField()
            PasswordTextField(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 5.dp)
            )
            ForgotPassword(
                modifier = Modifier
                    .padding(bottom = 20.dp),
            )
            LoginButton()
        }
    }
}