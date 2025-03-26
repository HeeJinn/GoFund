package com.example.gofund.view // Adjust package name if needed

// --- Keep your existing imports ---
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // Use wildcard or list needed ones
import androidx.compose.material3.*
import androidx.compose.runtime.* // Use wildcard or list needed ones
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// --- Add these imports ---
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gofund.viewmodel.ReportViewModel // Import your ViewModel

// --- Your Theme/Font imports ---
import com.example.gofund.ui.theme.IntroFamily
// import com.example.gofund.ui.theme.LightModeLightBlue // Not used in the provided code snippet
// import com.example.gofund.ui.theme.LightModeWhite // Not used directly, background is Color.White
import com.example.gofund.ui.theme.PoppinsFamily

// Assuming CustomComponent is in the same package or imported correctly
// If CustomComponent is in the same file/package, no import needed
// else import com.example.gofund.view.CustomComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    // Obtain the ViewModel instance using the compose integration library
    viewModel: ReportViewModel = viewModel() // Instantiate the ViewModel
) {
    // State for the Segmented Button selection remains
    var selectedIndex by remember { mutableIntStateOf(0) } // Use mutableIntStateOf
    val reportOptions = listOf("7 Days", "30 Days", "365 Days")

    // --- Collect states from ViewModel ---
    val weeklyData by viewModel.weeklyTotals.collectAsState()
    val monthlyData by viewModel.monthlyTotals.collectAsState()
    val annualData by viewModel.annualTotals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // --- Determine which data object to display based on the selected index ---
    val currentData = when (selectedIndex) {
        0 -> weeklyData
        1 -> monthlyData
        else -> annualData // Index 2 for Annually
    }

    // --- Extract values from the currentData object for easier use in UI ---
    // These replace the old state variables and calculation
    val totalExpenseToShow = currentData.expenseTotal
    val totalInvestmentToShow = currentData.investmentTotal
    val expensePercentageToShow = currentData.expensePercentage
    val investmentPercentageToShow = currentData.investmentPercentage

    // --- Main layout container ---
    // Box allows overlaying the loading indicator
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            // Changed to Top to allow fixed positioning of elements and error message
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp)) // Add some top padding

            // --- Segmented Button Row (UI unchanged) ---
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)
            ) {
                reportOptions.forEachIndexed { index, option ->
                    SegmentedButton(
                        onClick = {
                            selectedIndex = index // State update triggers recomposition
                        },
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                        selected = selectedIndex == index,
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = Color.White,
                            inactiveContainerColor = Color.Transparent,
                            inactiveContentColor = Color.Black,
                        ),
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = reportOptions.size)
                    ) {
                        Text(
                            text = option,
                            fontFamily = PoppinsFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Row for Percentage Indicators (CustomComponent) ---
            // Data source is now from ViewModel state
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomComponent(
                    canvasSize = 175.dp,
                    backgroundIndicatorStrokeWidth = 50f,
                    foregroundIndicatorStrokeWidth = 50f,
                    maxIndicatorValue = 100, // IMPORTANT: Max is 100 for percentage
                    indicatorValue = expensePercentageToShow.toInt(), // Use calculated percentage
                    smallText = "Expense %", // Updated label for clarity,
                    smallTextFontSize = 18.sp,
                    bigTextSuffix = "%",
                    // Assuming CustomComponent uses indicatorValue to display the number
                    bigTextFontSize = 27.sp,
                )

                CustomComponent(
                    canvasSize = 175.dp,
                    backgroundIndicatorStrokeWidth = 50f,
                    foregroundIndicatorStrokeWidth = 50f,
                    maxIndicatorValue = 100, // IMPORTANT: Max is 100 for percentage
                    indicatorValue = investmentPercentageToShow.toInt(), // Use calculated percentage
                    smallText = "Investment %", // Updated label for clarity
                    smallTextFontSize = 18.sp,
                    bigTextSuffix = "%",
                    // Assuming CustomComponent uses indicatorValue to display the number
                    bigTextFontSize = 27.sp,
                )
            }

            // --- Row for Text Labels below Indicators (UI unchanged) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp), // Added vertical padding
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Expense",
                    fontFamily = IntroFamily,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Investment",
                    fontFamily = IntroFamily,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Card for Monetary Totals ---
            // Data source is now from ViewModel state
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 25.dp, vertical = 10.dp), // Adjusted vertical padding
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor  = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = BorderStroke(width = 1.dp, color = Color.LightGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Total Expense Row ---
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        // Use SpaceBetween for better alignment in the card
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            // Label updated dynamically based on selection
                            text = "Total ${reportOptions[selectedIndex]}\nExpense",
                            fontFamily = PoppinsFamily,
                            fontSize = 18.sp, // Adjusted font size slightly
                            color = Color.Black,
                            textAlign = TextAlign.Start,
                            lineHeight = 20.sp // Adjust line height if needed
                        )
                        Text(
                            // Display value from ViewModel
                            text = "₱ $totalExpenseToShow",
                            fontFamily = IntroFamily,
                            color = MaterialTheme.colorScheme.tertiary, // Consider Expense color
                            fontSize = 20.sp,
                            textAlign = TextAlign.Start, // Align amount to the end
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .padding(vertical = 15.dp) // Adjusted padding
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.LightGray),
                    )

                    // --- Total Investment Row ---
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        // Use SpaceBetween for better alignment in the card
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            // Label updated dynamically based on selection
                            text = "Total ${reportOptions[selectedIndex]}\nInvestment",
                            fontFamily = PoppinsFamily,
                            fontSize = 18.sp, // Adjusted font size slightly
                            color = Color.Black,
                            textAlign = TextAlign.Start,
                            lineHeight = 20.sp // Adjust line height if needed
                        )
                        Text(
                            // Display value from ViewModel
                            text = "₱ $totalInvestmentToShow",
                            fontFamily = IntroFamily,
                            // Consider using secondary color or specific Investment color
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 20.sp,
                            textAlign = TextAlign.End, // Align amount to the end
                        )
                    }
                }
            }
            Log.d("INVESTMENT", totalInvestmentToShow.toString())

            Spacer(modifier = Modifier.height(16.dp)) // Add some bottom padding before error

            // --- Display Error Message (if any) ---
            if (errorMessage != null) {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red, // Make errors visible
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

        } // End Column

        // --- Loading Indicator Overlay ---
        // This will appear centered on top of the Column content when isLoading is true
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } // End Box
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun ShowReportScreen() {
    val navController = rememberNavController()
    // The preview will show the initial state of the ViewModel (usually zeros and not loading)
    // To show data in preview, you might need a fake ViewModel with sample data.
    ReportScreen(navController = navController)
}