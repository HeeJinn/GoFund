package com.example.gofund.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily
import kotlinx.coroutines.internal.synchronizedImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController){
    var isButtonEnabled by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("knribnitez@gmail.com") }
    var userName by remember { mutableStateOf("Kenley22") }
    var isEditable by remember { mutableStateOf(false) }
    var applyButtonContainerColor = if (isEditable) MaterialTheme.colorScheme.primary else Color.Transparent
    var editButtonContainerColor = if (isEditable) Color.Transparent else MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Account", fontFamily = IntroFamily) },
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
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (isEditable) {
                            // TODO: mediaPicker
                            Log.d("CONTENT_BUTTON", "Profile is clicked")
                        }
                    },
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "profile_image",
            )
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    CustomText(
                        headerText = "Username",
                        value = userName,
                        isEnabled = isEditable,
                        supportingText = "*13-max characters",
                        onValueChange = {
                            if (it.length <= 13){
                                userName = it
                            }
                        }
                    )
                    Spacer(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White)
                            .clip(MaterialTheme.shapes.medium)

                    )
                    CustomText(
                        headerText = "Email",
                        value = email,
                        isEnabled = isEditable,
                        onValueChange = {
                            email = it
                        }

                    )
                }
            }
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
                        isEditable = !isEditable
                    },
                    enabled = if (!isEditable) true else false,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        containerColor = editButtonContainerColor
                    ),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Edit",
                        fontFamily = PoppinsFamily,
                        fontSize = 17.sp,
                        color = if (isEditable) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
                OutlinedButton(
                    enabled = isEditable,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f),
                    onClick = {
                        userName = ""
                        email = ""
                        isEditable = !isEditable
                        Log.d("CONTENT_BUTTON", "Apply is clicked")
                    },

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = applyButtonContainerColor
                    ),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Apply",
                        fontFamily = PoppinsFamily,
                        fontSize = 17.sp,
                        color = if (isEditable) Color.White  else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CustomText(headerText: String, value : String,onValueChange: (String) -> Unit, isEnabled: Boolean, supportingText: String = ""){
    var colorText = if (isEnabled) Color.White.copy(alpha = 0.5f) else Color.White
    Text(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(),
        color = colorText,
        text = headerText,
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    )
    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontFamily = PoppinsFamily
        ),
        value = value,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Gray,
            disabledTextColor = Color.Gray,
            disabledContainerColor = Color.White,
            errorContainerColor = Color.White,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        enabled = isEnabled,
        supportingText = {
            Text(
                modifier = Modifier
                    .padding(bottom = 10.dp),
                text = supportingText,
                color = Color.White
            )
        }

    )
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview(){
    var navController = rememberNavController()
    AccountScreen(navController)

}