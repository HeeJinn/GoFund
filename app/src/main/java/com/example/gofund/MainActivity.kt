package com.example.gofund

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.navigations.SetupNavGraph
import com.example.gofund.ui.theme.GoFundTheme


class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoFundTheme {
                navController = rememberNavController()

                SetupNavGraph(navController)

            }
        }
    }
}


