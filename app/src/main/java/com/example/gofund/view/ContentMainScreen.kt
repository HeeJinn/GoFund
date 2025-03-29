package com.example.gofund.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.AlertDialog // Keep this
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // Keep this
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview // Add if needed for previewing this screen specifically
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.navigations.BottomBarScreen
import com.example.gofund.navigations.BottomNavGraph
import com.example.gofund.navigations.Screen
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.LightModeYellow
import com.example.gofund.viewmodel.ContentMainViewmodel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentMainScreen(navController: NavHostController, contentMainViewModel : ContentMainViewmodel = viewModel()){
    val bottomNavController = rememberNavController()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var showLogOutDialog by remember { mutableStateOf(false) }
    var showFundLimitDialog by remember { mutableStateOf(false) }
    val fundLimitValue by contentMainViewModel.fundLimit.collectAsStateWithLifecycle()

    BackHandler(enabled = true) {
        showLogOutDialog = true
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Go Fund", fontFamily = IntroFamily) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(LightModeLightBlue.value),
                    titleContentColor = Color(LightModeWhite.value),
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(
                        onClick = {
                            showFundLimitDialog = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.fund_limit_vector),
                            contentDescription = "fund_limit_vector",
                            modifier = Modifier.size(30.dp))
                    }
                    IconButton(
                        onClick = {
                            Log.d("LOGOUT_CLICK", "Logout icon clicked, showing dialog.")
                            showLogOutDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
        },
        bottomBar = { BottomBar(navController = bottomNavController) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            BottomNavGraph(navController = bottomNavController, mainNavController = navController)
        }
    }

    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogOutDialog = false
                Log.d("LOGOUT_DIALOG", "Dialog dismissed via outside click/back press.")
            },
            title = {
                Text(text = "Confirm Logout")
            },
            text = {
                Text("Are you sure you want to log out?") // More descriptive text
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogOutDialog = false
                        Log.d("LOGOUT_CONFIRM", "Logout confirmed. Signing out and navigating.")
                        Log.d("CURRENT_USER_ID_BEFORE", auth.currentUser?.uid.toString())

                        auth.signOut()

                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.ContentScreen.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        Log.d("CURRENT_USER_ID_AFTER", auth.currentUser?.uid.toString())
                    }
                ) {
                    Text(text = "Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogOutDialog = false
                        Log.d("LOGOUT_DISMISS", "Logout dismissed via button click.")
                    }
                ) {
                    Text(text = "Cancel", color = Color.Black)
                }
            }
        )
    }
    if (showFundLimitDialog){

        CustomFundLimitDialog(
            headerTitle = "Fund limit",
            contentText = "Amount",
            fundLimitAmount = "₱${fundLimitValue ?: "Not Set"}",
            onDismissText = "Back",
            onConfirmText = "Remove",
            contentTextColor = Color.Black,
            onConfirmContainerColor = MaterialTheme.colorScheme.error,
            onDismiss = {
                showFundLimitDialog = false
            },
            onConfirm = {
                contentMainViewModel.resetFund()
                showFundLimitDialog = false
            }
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController){
    val screens = listOf(
        BottomBarScreen.Home_Screen,
        BottomBarScreen.Expense_Screen,
        BottomBarScreen.Create_Screen,
        BottomBarScreen.Investment_Screen,
        BottomBarScreen.Report_Screen,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState() // Use 'by' delegate
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color(LightModeLightBlue.value),
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
){
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(imageVector = screen.icon, contentDescription = screen.title)
        },
        selected = currentDestination?.hierarchy?.any{
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route){
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(LightModeYellow.value),
            selectedTextColor = Color(LightModeYellow.value),
            indicatorColor = Color.DarkGray, // Consider adjusting indicator color for visibility
            unselectedIconColor = Color.White,
            unselectedTextColor = Color.White
        )
    )
}

// Optional: Preview function for ContentMainScreen if needed
@Preview(showBackground = true)
@Composable
fun PreviewContentMainScreen() {
    // You'll need a dummy NavHostController for the preview
    val dummyNavController = rememberNavController()
    ContentMainScreen(navController = dummyNavController)
    // Note: The preview won't show the dialog by default unless you manipulate
    // the 'showLogOutDialog' state specifically for the preview.
}