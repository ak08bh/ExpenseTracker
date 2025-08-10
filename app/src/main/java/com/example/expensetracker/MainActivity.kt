package com.example.expensetracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseTrackerApp()
                }
            }
        }
    }
}

sealed class DestinationScreen(val route: String) {
    object ExpenseEntry : DestinationScreen("ExpenseEntry")
    object ExpenseList : DestinationScreen("ExpenseList")
    object ExpenseReport : DestinationScreen("ExpenseReport")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseTrackerApp() {
    val viewModel = hiltViewModel<TrackerViewModel>()
    val navController = rememberNavController()

    NotificationMessage(viewModel)

    Scaffold(
        bottomBar = { BottomBar(navController) } // single persistent bar
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DestinationScreen.ExpenseEntry.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DestinationScreen.ExpenseEntry.route) {
                ExpenseEntryScreen(viewModel, navController)
            }
            composable(DestinationScreen.ExpenseList.route) {
                ExpenseListScreen(viewModel, navController)
            }
            composable(DestinationScreen.ExpenseReport.route) {
                ExpenseReportScreen(viewModel, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerTheme {
        ExpenseTrackerApp()
    }
}
