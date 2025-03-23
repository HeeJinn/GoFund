package com.example.gofund.view

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gofund.navigations.BottomBarScreen
import com.example.gofund.navigations.BottomNavGraph
import com.example.gofund.navigations.Screen
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.LightModeLightBlue
import com.example.gofund.ui.theme.LightModeWhite
import com.example.gofund.ui.theme.LightModeYellow
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentMainScreen(navController: NavHostController){
    val bottomNavController = rememberNavController()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
                            Log.d("CURRENT_USER_ID", auth.currentUser?.uid.toString())
                            navController.navigate(Screen.LoginScreen.route){
                                popUpTo(Screen.ContentScreen.route){
                                    inclusive = true
                                }
                            }
                            auth.signOut()
                            Log.d("CURRENT_USER_ID", auth.currentUser?.uid.toString())
                        }
                    ) {Icon(imageVector = Icons.Rounded.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(30.dp)) }
                }
            )
        },
        bottomBar = {BottomBar(navController = bottomNavController)}
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Use a NavHost *only* for the bottom bar screens
            BottomNavGraph(navController = bottomNavController, mainNavController = navController)

        }
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
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
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
            indicatorColor = Color.DarkGray,
            unselectedIconColor = Color.White,
            unselectedTextColor = Color.White
        )

    )
}
