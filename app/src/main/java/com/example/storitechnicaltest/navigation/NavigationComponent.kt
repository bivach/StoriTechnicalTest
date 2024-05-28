package com.example.storitechnicaltest.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.storitechnicaltest.login.LoginScreen
import com.example.storitechnicaltest.login.LoginViewModel
import com.example.storitechnicaltest.register.RegisterScreen
import com.example.storitechnicaltest.transactions.TransactionsScreen
import com.example.storitechnicaltest.transactions.details.TransactionDetailScreen

@Composable
fun NavigationComponent() {
    val navController = rememberNavController()
    val viewModel: NavigationViewModel = hiltViewModel()
    val startDestination = viewModel.getStartDestination()
    NavHost(navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { TransactionsScreen(navController) }
        composable("transactionDetail/{transactionId}") { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            if (transactionId != null) {
                TransactionDetailScreen(navController, transactionId)
            }
        }
    }
}
