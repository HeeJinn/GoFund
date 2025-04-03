package com.example.gofund.view


// --- NECESSARY IMPORTS for Permissions ---
import android.Manifest // Needed for permission name
import android.content.pm.PackageManager // Needed for permission check result
import android.os.Build // Needed for SDK version check
import androidx.activity.compose.rememberLauncherForActivityResult // Needed for permission launcher
import androidx.activity.result.contract.ActivityResultContracts // Needed for permission contract
import androidx.core.content.ContextCompat // Needed for permission check function
// --- End Permission Imports ---
import com.example.gofund.viewmodel.LoginViewModel
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import androidx.lifecycle.viewmodel.compose.viewModel // Keep this
import androidx.navigation.NavController
import com.example.gofund.R
import com.example.gofund.navigations.Screen
import com.example.gofund.ui.theme.GoFundTheme
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue // Assuming this theme color exists
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
fun EmailNameTextField(username : String, onUsernameValueChange : (String) -> Unit){ // Copied from your previous input
    var isFocused by remember { mutableStateOf(false) }
    var focusedLabelColor = if (username.isNotEmpty() || isFocused) Color.White else Color.LightGray
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier
            .padding(10.dp)
            .width(278.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        value = username,
        shape = MaterialTheme.shapes.medium,
        onValueChange = onUsernameValueChange,
        maxLines = 1,
        singleLine = true,
        label = { Text(
            text = "Email",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold
        ) },
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
            cursorColor = Color.Black // Added for visibility
        ),
        placeholder = {Text(text = "Enter valid email", color = Color.LightGray)},
        leadingIcon ={
            Icon(imageVector = Icons.Rounded.Email, contentDescription = "Email")

        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next // Changed to Next for better flow
        ),
        keyboardActions = KeyboardActions(
            onNext = {focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down)} // Move focus down
            // onDone = {focusManager.clearFocus()} // Can keep if Next isn't desired
        )

    )
}

@Composable
fun PasswordTextField(modifier: Modifier = Modifier, password: String, onPasswordValueChange:(String) -> Unit){ // Copied from your previous input
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
            .width(278.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        value = password,
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
            fontWeight = FontWeight.Bold
        ) },
        singleLine = true,
        placeholder = {Text(text = "Enter password", color = Color.LightGray)},
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,  // Background when focused
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,  // Background when not focused
            focusedLabelColor = focusedLabelColor,  // Label color when focused
            focusedLeadingIconColor = MaterialTheme.colorScheme.background,
            unfocusedLabelColor = focusedLabelColor,  // Label color when not focused
            focusedTrailingIconColor = MaterialTheme.colorScheme.background,
            cursorColor = Color.Black // Added for visibility
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Lock, contentDescription = "Password")

        },
        trailingIcon = {
            IconButton(
                onClick = {
                    passVisibility = !passVisibility
                }
            ) {
                Icon(painter = icon, contentDescription = "Toggle password visibility")
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
fun RowAreaForRememberMeAndForgotPass(modifier: Modifier = Modifier, checkedState : Boolean, onCheckChange: (Boolean) -> Unit, onForgotPassClick: () -> Unit){
    Row (
        modifier = modifier
            .wrapContentHeight()
            .background(color = Color.Transparent)
            .width(300.dp), // Your original width
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(
            modifier = Modifier
                .weight(1f), // Your original weight
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // Your original arrangement
        ) {
            Checkbox(
                modifier = Modifier, // Your original modifier
                checked = checkedState,
                onCheckedChange = onCheckChange, // Your original direct callback
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.White,
                    // Using your original color setup, though MaterialTheme is generally preferred
                    checkmarkColor = MaterialTheme.colorScheme.primary
                )
            )
            // No extra Spacer here as per your original code
            Text(
                text = "Remember me",
                textAlign = TextAlign.Start,
                fontFamily = PoppinsFamily,
                color = Color.White,
                fontSize = 12.sp,
            )
        }
        TextButton(
            modifier = Modifier
                .weight(1f), // Your original weight
            onClick = onForgotPassClick
        ) {
            Text(
                text = "forgot password", // Your original text
                fontFamily = PoppinsFamily,
                textDecoration = TextDecoration.Underline,
                color = Color.White,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun LoginButton(modifier: Modifier = Modifier, onClick: () -> Unit){ // Removed NavController param, not needed
    Button(
        modifier = modifier
            .width(200.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(Color.White)
    ) {
        Text(
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = IntroFamily,
            ),
            color = Color.Black,
            text = "Login")
    }
}

@Composable
fun SignUpButton(modifier: Modifier = Modifier, onClick:() -> Unit){ // Copied from your previous input
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ),
        border = BorderStroke(width = 1.dp, color = Color.White),
        onClick = onClick
    ) {
        Text(text= "Don't have an account? Sign up", color = Color.White, fontSize = 12.sp) // Slightly smaller text
    }
}

@Composable
fun SpacerWhiteLine(modifier: Modifier = Modifier){ // Copied from your previous input
    Row(
        modifier = modifier
            .width(300.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            modifier = Modifier // Removed modifier = modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .height(1.dp)
                .background(Color.White)
        )
        Text(modifier = Modifier.padding(horizontal = 10.dp),text = "OR", color = Color.White, fontSize = 17.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold)
        Spacer(
            modifier = Modifier // Removed modifier = modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 20.dp)
                .height(1.dp)
                .background(Color.White)
        )
    }
}
// --- End of Unchanged UI Composables ---


@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) { // Use correct ViewModel instance
    val context = LocalContext.current

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var rememberMeChecked by remember { mutableStateOf(false) }


    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // Use nullable String for error message
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = context as? Activity

    val userPreferences by loginViewModel.userPreferences.collectAsState()

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Log.d("Permissions", "Notification Permission Result: Granted = $isGranted")
            hasNotificationPermission = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(key1 = true) { // Run once when LoginScreen enters composition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    LaunchedEffect(userPreferences) {
        // Check if the DataStore value is not the default empty/false AND local state is still empty/false
        if (userPreferences.rememberMe && userPreferences.email.isNotEmpty() && emailInput.isEmpty()) {
            Log.d("LoginScreen", "Loading credentials from DataStore: Email=${userPreferences.email}")
            emailInput = userPreferences.email
            passwordInput = userPreferences.password // Load password (INSECURE)
            rememberMeChecked = true // Set checkbox based on loaded preference
        } else if (!userPreferences.rememberMe && emailInput.isEmpty() && passwordInput.isEmpty()) {
            // If remember me was explicitly false in prefs, ensure checkbox reflects that initially
            rememberMeChecked = false
        }
    }

    // --- Back Handler ---
    BackHandler {
        showExitDialog = true
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        activity?.finishAffinity() // Closes the app task
                    }
                ) {
                    Text(
                        text = "Confirm",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Black // Consider using MaterialTheme colors
                    )
                }
            },
            title = { Text(text = "Exit App") },
            text = { Text(text = "Are you sure you want to exit?") },
        )
    }

    GoFundTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Main Content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(LightModeLightBlue.value)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp), // Consistent padding
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LogoText()

                    EmailNameTextField(
                        username = emailInput,
                        onUsernameValueChange = { emailInput = it }
                    )
                    PasswordTextField(
                        password = passwordInput,
                        modifier = Modifier.padding(top = 10.dp),
                        onPasswordValueChange = { newValue ->
                                passwordInput = newValue

                        }
                    )
                    RowAreaForRememberMeAndForgotPass(
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                        checkedState = rememberMeChecked,
                        onCheckChange = { isChecked ->

                            rememberMeChecked = isChecked
                            Log.d("LoginScreen", "Checkbox toggled: $isChecked")
                        },
                        onForgotPassClick = {
                            navController.navigate(Screen.ForgotPasswordScreen.route)
                        }
                    )


                    errorMessage?.let {
                        if (it.isNotEmpty()) {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    LoginButton(
                        modifier = Modifier.padding(bottom = 10.dp),
                        onClick = {
                            if (emailInput.isBlank() || passwordInput.isBlank()) {
                                errorMessage = "Email and password cannot be empty."

                                return@LoginButton
                            }else if(passwordInput.length < 9){
                                errorMessage = "Password must be at least 9 characters long."
                                return@LoginButton
                            } else {
                                errorMessage = null
                                isLoading = true
                                loginViewModel.login(
                                    email = emailInput.trim(),
                                    password = passwordInput.trim(),
                                    rememberMe = rememberMeChecked, // Pass the current UI checkbox state
                                    onSuccess = {
                                        isLoading = false
                                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                        // Navigate after successful login
                                        navController.navigate(Screen.ContentScreen.route) {
                                            popUpTo(Screen.LoginScreen.route) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true // Prevent multiple instances of ContentScreen
                                        }
                                    },
                                    onFailure = { error ->
                                        isLoading = false
                                        errorMessage = error // Set error message to display in Text
                                    }
                                )
                            }
                        }
                    )

                    SpacerWhiteLine(modifier = Modifier.padding(vertical = 0.dp)) // Reduced padding

                    SignUpButton(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = { navController.navigate(Screen.RegisterScreen.route) }
                    )

                    Spacer(modifier = Modifier.height(20.dp)) // Space at bottom if needed
                }
            }

            // --- Loading Indicator ---
            if (isLoading) {
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
}

// --- Previews (Optional, might need adjustments for ViewModel) ---
/*
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Previews often need a dummy NavController and might have issues with ViewModels
    // depending on how they are instantiated (e.g., Hilt).
    val navController = rememberNavController()
    // You might need a fake ViewModel or adjust the default viewModel() call for previews
    LoginScreen(navController = navController)
}

@Preview
@Composable
fun WhiteSpacePreview(){ // Keep this if useful
    SpacerWhiteLine()
}
*/