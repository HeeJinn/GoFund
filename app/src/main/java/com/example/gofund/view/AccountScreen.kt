package com.example.gofund.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Using wildcard
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.* // Using wildcard
import androidx.compose.runtime.* // Using wildcard
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily
import com.example.gofund.viewmodel.AccountViewModel // Import correct VM
import com.example.gofund.viewmodel.ProfileLoadResult // Import states
import com.example.gofund.viewmodel.UpdateResult // Import states
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    modifier: Modifier = Modifier, // Added modifier back
    accountViewModel: AccountViewModel = viewModel() // Corrected VM name and using Hilt/Compose VM getter
) {
    // --- UI Edit Mode State ---
    var isEditable by remember { mutableStateOf(false) }

    // --- Local State for TextFields (initialized empty) ---
    var emailInput by remember { mutableStateOf("") }
    var userNameInput by remember { mutableStateOf("") }
    // --- State to store original loaded values ---
    var originalEmail by remember { mutableStateOf<String?>(null) }
    var originalUserName by remember { mutableStateOf<String?>(null) }

    // --- Observe ViewModel states ---
    val profileState by accountViewModel.profileState.collectAsStateWithLifecycle()
    val isLoading by accountViewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // --- Effect to load initial data into local input states ---
    LaunchedEffect(profileState) {
        // Only populate inputs if data is loaded successfully AND we haven't stored originals yet
        if (profileState is ProfileLoadResult.Success && originalEmail == null) {
            val data = profileState as ProfileLoadResult.Success
            emailInput = data.email
            userNameInput = data.userName
            originalEmail = data.email // Store original for comparison and cancel
            originalUserName = data.userName // Store original for comparison and cancel
            Log.d("AccountScreen", "Initial profile data loaded into UI state.")
        } else if (profileState is ProfileLoadResult.Error) {
            Toast.makeText(context, (profileState as ProfileLoadResult.Error).message, Toast.LENGTH_LONG).show()
            // Optionally navigate back if profile loading fails critically
        }
    }

    // --- Effect to handle update feedback events ---
    LaunchedEffect(Unit) { // Key = Unit runs once, collecting happens inside
        launch {
            accountViewModel.updateEmailEvent.collect { result ->
                val message = when (result) {
                    is UpdateResult.Success -> "Email updated successfully!"
                    is UpdateResult.Error -> "Email Update Failed: ${result.message}"
                    is UpdateResult.RequiresReAuthentication -> "Please log in again to update email."
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                if (result is UpdateResult.Success) {
                    originalEmail = emailInput // Update original value tracking on success
                }
                if (result is UpdateResult.RequiresReAuthentication) {
                    // TODO: Handle re-authentication request (e.g., navigate to login)
                }
            }
        }
        launch {
            accountViewModel.updateUsernameEvent.collect { result ->
                val message = when (result) {
                    is UpdateResult.Success -> "Username updated successfully!"
                    is UpdateResult.Error -> "Username Update Failed: ${result.message}"
                    else -> "Username update status unknown"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                if (result is UpdateResult.Success) {
                    originalUserName = userNameInput // Update original value tracking on success
                }
            }
        }
    }

    // --- Calculated button colors (kept from your code) ---
    val applyButtonContainerColor = if (isEditable) MaterialTheme.colorScheme.primary else Color.Transparent
    val editButtonContainerColor = if (isEditable) Color.Transparent else MaterialTheme.colorScheme.primary
    val applyButtonTextColor = if (isEditable) Color.White else MaterialTheme.colorScheme.primary
    val editButtonTextColor = if (isEditable) MaterialTheme.colorScheme.primary else Color.White

    // --- UI Structure ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Account", fontFamily = IntroFamily) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // --- Back navigation logic ---
                            if (isEditable) {
                                // Cancel edit, revert changes
                                emailInput = originalEmail ?: ""
                                userNameInput = originalUserName ?: ""
                                isEditable = false
                            } else {
                                navController.popBackStack()
                            }
                            // -------------------------
                        },
                        enabled = !isLoading // Disable back if currently saving? Maybe not needed.
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Back", modifier = Modifier.size(30.dp))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier // Use passed-in modifier
                .padding(paddingValues) // Apply Scaffold padding
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Top, // Changed arrangement
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show loading indicator for initial profile load
            if (profileState is ProfileLoadResult.Loading) {
                Spacer(modifier = Modifier.height(50.dp))
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading Profile...")
            } else {
                // --- Show content only when not loading initial profile ---
                Image(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 10.dp)
                        .size(150.dp)
                        .clip(CircleShape)
                        .clickable(enabled = isEditable && !isLoading) { // Enable click only when editable & not saving
                            // TODO: Implement image picker logic
                            Log.d("AccountScreen", "Profile image clicked (Edit enabled: $isEditable)")
                        },
                    painter = painterResource(id = R.drawable.profile), // Use actual profile pic if available
                    contentDescription = "Profile Image",
                )
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(), // Use wrap content
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp)
                    ) {
                        // Use local state bound TextFields
                        CustomText( // Your definition below
                            headerText = "Username",
                            value = userNameInput,
                            isEnabled = isEditable && !isLoading, // Disable based on edit mode AND loading state
                            supportingText = "*Max 13 characters",
                            onValueChange = { newValue ->
                                if (newValue.length <= 13) { // Keep local validation
                                    userNameInput = newValue
                                }
                            }
                        )
                        Spacer( // Divider
                            modifier = Modifier.padding(vertical = 15.dp).fillMaxWidth().height(1.dp)
                                .background(Color.White.copy(alpha = 0.5f))
                        )
                        CustomText( // Your definition below
                            headerText = "Email",
                            value = emailInput,
                            isEnabled = isEditable && !isLoading, // Disable based on edit mode AND loading state
                            onValueChange = { emailInput = it }
                        )
                    }
                } // End Card

                // Edit / Apply Buttons Row
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), // Use spacedBy
                ) {
                    // EDIT / CANCEL Button
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isEditable) { // Treat as CANCEL
                                emailInput = originalEmail ?: ""
                                userNameInput = originalUserName ?: ""
                                Log.d("AccountScreen", "Edit cancelled, reverting changes.")
                            } else {
                                Log.d("AccountScreen", "Edit mode enabled.")
                            }
                            isEditable = !isEditable // Toggle edit mode
                        },
                        enabled = !isLoading, // Disable if any operation is loading
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = editButtonContainerColor),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = if (isEditable) "Cancel" else "Edit", // Dynamic text
                            fontFamily = PoppinsFamily, fontSize = 17.sp,
                            color = editButtonTextColor
                        )
                    }

                    // APPLY Button
                    OutlinedButton(
                        enabled = isEditable && !isLoading, // Enable only when editable and not loading
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // --- Modified Apply Logic ---
                            var emailChanged = false
                            var usernameChanged = false

                            // Check if email changed and trigger update
                            if (emailInput != originalEmail) {
                                accountViewModel.updateUserEmail(emailInput)
                                emailChanged = true
                                Log.d("AccountScreen", "Apply: Email change detected, calling VM.")
                            }
                            // Check if username changed and trigger update
                            if (userNameInput != originalUserName) {
                                accountViewModel.updateUserName(userNameInput)
                                usernameChanged = true
                                Log.d("AccountScreen", "Apply: Username change detected, calling VM.")
                            }

                            if (!emailChanged && !usernameChanged) {
                                Toast.makeText(context, "No changes to apply.", Toast.LENGTH_SHORT).show()
                            }
                            // Exit edit mode after applying changes (or if no changes)
                            isEditable = false
                            // ---------------------------
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = applyButtonTextColor, // Text color depends on isEditable
                            containerColor = applyButtonContainerColor,
                            disabledContainerColor = Color.Transparent // Keep transparent when disabled
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        // Show ProgressIndicator inside button when loading AND in edit mode
                        if (isLoading && isEditable) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = applyButtonTextColor, strokeWidth = 2.dp)
                        } else {
                            Text(text = "Apply", fontFamily = PoppinsFamily, fontSize = 17.sp, color = applyButtonTextColor)
                        }
                    }
                } // End Button Row
            } // End Else block (content loaded)
        } // End Main Column
    } // End Scaffold
} // End AccountScreen

// --- Your CustomText Composable Definition (UNCHANGED) ---
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
            disabledContainerColor = Color.White.copy(alpha = 0.8f), // Indicate disabled state
            errorContainerColor = Color.White,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        enabled = isEnabled, // Use the passed-in isEnabled state
        supportingText = {
            // Only show supporting text if provided
            if (supportingText.isNotBlank()){
                Text(
                    text = supportingText,
                    color = Color.White.copy(alpha = 0.7f), // Subtle supporting text
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun AccountScreenPreview(){
    // Preview uses default values, won't reflect ViewModel state
    var navController = rememberNavController()
    AccountScreen(navController)
}