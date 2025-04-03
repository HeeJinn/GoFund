package com.example.gofund.view

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.navigations.Screen
import com.example.gofund.ui.theme.GoFundTheme
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.LightModeYellow
import com.example.gofund.ui.theme.PoppinsFamily
import com.example.gofund.viewmodel.LoginViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegisterScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()){
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember{ mutableStateOf("")}
    var isLoading by remember { mutableStateOf(false) }
    var isAlreadyMemberEnabled by remember { mutableStateOf(true) }
    var isTermsAndPrivacyOpen by remember { mutableStateOf(false) }
    var isAgreedToTermsAndPrivacyOpen by remember { mutableStateOf(false) }


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
                        isEnabled = isAlreadyMemberEnabled,
                        onLoginClick = {
                            isAlreadyMemberEnabled = false
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
                            if (username.length <= 13 || it.length < username.length){
                                username = it
                            }
                        }
                    )
                    PasswordRegister(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        registerPass = password,
                        onPasswordValueChange = { newPassword ->
                                password = newPassword
                            }

                    )
                    if (errorMsg.isNotBlank()) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    SignupButton(
                        modifier = Modifier
                            .padding(top = 30.dp)
                        ,navController = navController
                    ) {
                        if (email.isEmpty() || username.isEmpty() || password.isEmpty()){
                            errorMsg = "Make sure to fill all the text field"
                        }
                        else if (!isAgreedToTermsAndPrivacyOpen) errorMsg = "Please accept the Terms and Conditions and Privacy Policy"
                        else if (password.length < 9){
                            errorMsg = "Password must be at least 9 characters"
                            return@SignupButton

                        } else{
                            errorMsg = ""
                            isLoading = true
                            loginViewModel.signUp(email.trim(), username.trim(), password.trim(),
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Verification sent to your email", Toast.LENGTH_SHORT).show()
                                    // TODO: take the values and throw to the db

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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "By creating an account, you agree to our",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = PoppinsFamily
                        )
                        TextButton(
                            modifier = Modifier.padding(0.dp),
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                isTermsAndPrivacyOpen = true
                            },

                        ) {
                            Text(
                                text = "Terms of Condition & Privacy Policy",
                                color = Color.White,
                                fontFamily = PoppinsFamily,
                                fontSize = 13.sp,
                                textDecoration = TextDecoration.Underline
                            )
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
        if (isTermsAndPrivacyOpen){
            TermsAndPrivacyDialogContent(
                onDismissClick = { isTermsAndPrivacyOpen = false },
                onAgreeClick = { isAgreedToTermsAndPrivacyOpen = true }
            )
        }
    }

}

@Composable
fun BackToLogin(isEnabled: Boolean,modifier : Modifier = Modifier, onLoginClick: () -> Unit) {
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
        modifier = Modifier
            .clickable(enabled = isEnabled) {

                onLoginClick()
            }
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
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = Color.Black,
            fontSize = 16.sp
        ),
        supportingText = {
            Text(
                text = "Must be a valid email format",
                color = Color.White
            )
        },
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
        onValueChange = {
            if (username.length <= 13 || it.length < username.length){
                onValueChange(it)
            }
        },
        label = {
            Text(text = "Username", fontFamily = PoppinsFamily)
        },
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = Color.Black,
            fontSize = 16.sp
        ),
        singleLine = true,
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
        supportingText = {
            Text(
                text = "Must be 13 characters or less",
                color = Color.White
            )
        },
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
fun PasswordRegister(
    modifier: Modifier = Modifier,
    registerPass: String,
    onPasswordValueChange: (String) -> Unit
) {
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
        singleLine = true,
        supportingText = {
            Text(
                text = "Must be 9 characters or more",
                color = Color.White
            )
        },
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            color = Color.Black,
            fontSize = 16.sp
        ),
        maxLines = 1,
        shape = MaterialTheme.shapes.medium,
        onValueChange = { newPassword ->
           onPasswordValueChange(newPassword)

        },
        label = {
            Text(
                text = "Password",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold
            )
        },
        placeholder = { Text(text = "Enter password", color = Color.LightGray) },
        colors = TextFieldDefaults.colors(
            errorIndicatorColor = Color.Red,
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = focusedLabelColor,
            focusedLeadingIconColor = MaterialTheme.colorScheme.background,
            unfocusedLabelColor = focusedLabelColor,
            focusedTrailingIconColor = MaterialTheme.colorScheme.background,
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Lock, contentDescription = "Email")
        },
        trailingIcon = {
            IconButton(onClick = { passVisibility = !passVisibility }) {
                Icon(painter = icon, contentDescription = "visibility")
            }
        },
        visualTransformation = if (passVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
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
fun TermsAndPrivacyDialogContent( // Renamed for clarity
    onDismissClick: () -> Unit,
    onAgreeClick: () -> Unit
) {
    val scrollState: ScrollState = rememberScrollState()
    // Consider using AlertDialog for standard dialog behavior
    Dialog(
        onDismissRequest = onDismissClick,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp) // Adjust height as needed
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // --- Header ---
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = LightModeLightBlue
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        text = "Terms & Privacy Policy", // Updated Title
                        textAlign = TextAlign.Center,
                        // fontFamily = IntroFamily,
                        fontSize = 25.sp,
                        color = LightModeWhite
                    )
                }

                // --- Scrollable Content ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    // --- Terms and Conditions Section ---
                    Text(
                        text = "Terms and Conditions",
                        fontWeight = FontWeight.ExtraBold, // Make T&C stand out
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        // Use actual last updated date for T&C
                        text = "Last Updated: 04/03/2025\n",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Welcome to Go Fund App!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SectionText("These Terms and Conditions (\"Terms\") govern your use of our mobile application (\"App\") and services. By accessing or using the App, you agree to comply with these Terms. If you do not agree, please refrain from using the App.")

                    SectionTitle("Acceptance of Terms")
                    SectionText("By creating an account or using Go Fund App, you acknowledge that you have read, understood, and agreed to these Terms, as well as our Privacy Policy. These Terms constitute a legally binding agreement between you and Go Fund App.")

                    SectionTitle("Account Registration")
                    SectionText(
                        "• To use the App, you must register using a valid email address.\n" +
                                "• You are responsible for maintaining the confidentiality of your account credentials.\n" +
                                "• You must provide accurate and up-to-date information.\n" +
                                "• Go Fund App reserves the right to suspend or terminate accounts that violate these Terms."
                    )

                    SectionTitle("Use of the App")
                    SectionText(
                        "• The App allows users to track dummy input funds, simulate expenses, and simulate investments.\n" +
                                "• All financial data entered is for simulation purposes only and does not represent real financial transactions.\n" +
                                "• The App does not provide real financial, investment, or tax advice."
                    )

                    SectionTitle("User Responsibilities")
                    SectionText(
                        "• You agree not to use the App for illegal or fraudulent activities.\n" +
                                "• You must not attempt to hack, reverse-engineer, or disrupt the App’s functionality.\n" +
                                "• You are solely responsible for any data you input into the App."
                    )

                    SectionTitle("Intellectual Property")
                    SectionText(
                        "• Go Fund App owns all rights, titles, and interests in the App, including its software and content.\n" +
                                "• No copyrighted material is used in this App.\n" +
                                "• You may not reproduce, modify, or distribute the App without explicit permission."
                    )

                    SectionTitle("Data Privacy")
                    SectionText(
                        "• Your personal data will be handled in accordance with the Philippine Data Privacy Act of 2012 (Republic Act No. 10173).\n" +
                                "• We collect your email for account creation and may use analytics to improve the App.\n" +
                                "• For more details, please review our Privacy Policy." // Reference to the policy below
                    )

                    SectionTitle("Limitation of Liability")
                    SectionText(
                        "• The App is provided \"as is\" without warranties of any kind.\n" +
                                "• Go Fund App shall not be liable for any damages arising from the use or inability to use the App.\n" +
                                "• The App does not guarantee accuracy in financial simulations."
                    )

                    SectionTitle("Governing Law")
                    SectionText("These Terms shall be governed by and construed in accordance with the laws of the Republic of the Philippines. Any disputes shall be resolved in the courts of the Philippines.")

                    SectionTitle("Changes to Terms")
                    SectionText("Go Fund App reserves the right to modify these Terms at any time. Continued use of the App after changes constitutes acceptance of the updated Terms.")

                    SectionTitle("Termination")
                    SectionText("Go Fund App may terminate or suspend your access to the App at any time for violations of these Terms.")

                    SectionTitle("Contact Information (Terms)") // Clarify which contact info
                    SectionText(
                        "For questions or concerns regarding these Terms, please contact us at:\n" +
                                "Email: knribnitez@gmail.com" // Use actual email
                    )

                    // --- Separator ---
                    Divider(modifier = Modifier.padding(vertical = 20.dp), thickness = 1.dp, color = Color.LightGray)

                    // --- Privacy Policy Section ---
                    Text(
                        text = "Privacy Policy",
                        fontWeight = FontWeight.ExtraBold, // Make Privacy Policy stand out
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    val currentDateTime = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
                    Text(
                        // Replace placeholder [Insert Date] with current date or specific last updated date
                        text = "Last Updated: $currentDateTime\n", // Using current date as placeholder was not provided
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Welcome to Go Fund App!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SectionText("This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application (\"App\"). By accessing or using the App, you agree to the terms of this Privacy Policy. If you do not agree, please do not use the App.")

                    SectionTitle("Information We Collect")
                    SectionText(
                        "Personal Information\n" +
                                "When you register, we collect:\n" +
                                " • Email Address (for account creation and communication)\n" +
                                " • Username (optional, if applicable)\n\n" +
                                "Non-Personal Information\n" +
                                "We may automatically collect:\n" +
                                " • Device Information (e.g., device model, operating system)\n" +
                                " • Usage Data (e.g., features accessed, session duration)\n" +
                                " • IP Address & Location Data (general, non-precise location for analytics)\n\n" +
                                "Financial Simulation Data\n" +
                                " • The App allows you to input dummy funds, expenses, and investments for simulation purposes.\n" +
                                " • This data is stored locally on your device unless you opt for cloud backup (if applicable).\n" +
                                " • No real financial transactions occur within the App."
                    )

                    SectionTitle("How We Use Your Information")
                    SectionText(
                        "We use collected data to:\n" +
                                " • Provide and improve the App’s functionality.\n" +
                                " • Personalize your experience.\n" +
                                " • Respond to user inquiries and support requests.\n" +
                                " • Analyze trends and usage for App optimization.\n" +
                                " • Comply with legal obligations under Philippine law."
                    )

                    SectionTitle("Data Sharing & Disclosure")
                    SectionText(
                        "We do not sell or rent your personal information. However, we may share data in the following cases:\n" +
                                " • Service Providers: Third-party vendors assisting in App operations (e.g., cloud storage, analytics).\n" +
                                " • Legal Compliance: If required by law (e.g., court orders, government requests).\n" +
                                " • Business Transfers: In case of mergers, acquisitions, or asset sales."
                    )

                    SectionTitle("Data Security")
                    SectionText(
                        " • We implement industry-standard security measures to protect your data.\n" +
                                " • However, no digital transmission is 100% secure—use the App at your own risk."
                    )

                    SectionTitle("Data Retention")
                    SectionText(
                        " • Your account data is retained as long as your account is active.\n" +
                                " • You may request account deletion by contacting us.\n" +
                                " • Financial simulation data may be stored locally on your device unless deleted by you."
                    )

                    SectionTitle("Your Rights Under Philippine Law")
                    SectionText(
                        "Under the Data Privacy Act of 2012 (Republic Act No. 10173), you have the right to:\n" +
                                " • Access, correct, or delete your personal data.\n" +
                                " • Withdraw consent for data processing.\n" +
                                " • File a complaint with the National Privacy Commission (NPC) if needed."
                    )

                    SectionTitle("Children’s Privacy")
                    SectionText(
                        " • The App is not intended for users under 13.\n" +
                                " • We do not knowingly collect data from minors."
                    )

                    SectionTitle("Third-Party Links")
                    SectionText(
                        " • The App may contain links to third-party websites/services.\n" +
                                " • We are not responsible for their privacy practices."
                    )

                    SectionTitle("Changes to This Policy")
                    SectionText("We may update this Privacy Policy periodically. Continued use of the App after changes implies acceptance.")

                    SectionTitle("Contact Us (Privacy)") // Clarify which contact info
                    SectionText(
                        "For questions, data requests, or concerns regarding this Privacy Policy, contact us at:\n" +
                                "Email: knribnitez@gmail.com" // Use actual email, same as T&C for consistency?
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Final Acknowledgment ---
                    Text(
                        text = "By clicking \"Agree\", you acknowledge that you have read, understood, and agreed to both the Terms and Conditions and the Privacy Policy.", // Updated acknowledgement
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // --- Buttons Row ---
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        onClick = onDismissClick, // Use passed lambda
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Don't Agree",
                            color = LightModeWhite,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        onClick = onAgreeClick, // Use passed lambda
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightModeLightBlue
                        )
                    ) {
                        Text(
                            text = "Agree",
                            color = LightModeWhite,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

    }
}

// Helper composables for consistent styling
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun SectionText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewRegister(){
    val navController = rememberNavController()
    RegisterScreen(navController)
}

@Preview
@Composable
fun PreviewTerms(){
    TermsAndPrivacyDialogContent(
        onDismissClick = {
            // Handle dismiss
        },
        onAgreeClick = {
            // Handle agree
        }
    )
}