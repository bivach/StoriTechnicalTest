package com.example.storitechnicaltest.transactions.details

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.storitechnicaltest.core.model.Transaction

@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.getTransactionDetail(transactionId)
    }

    when (uiState) {
        is TransactionDetailViewModel.TransactionDetailUIState.Start -> {
            // Initial state, you can show a placeholder or nothing
        }
        is TransactionDetailViewModel.TransactionDetailUIState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is TransactionDetailViewModel.TransactionDetailUIState.Success -> {
            val transaction = (uiState as TransactionDetailViewModel.TransactionDetailUIState.Success).transaction
            TransactionDetailContent(transaction)
        }
        is TransactionDetailViewModel.TransactionDetailUIState.Error -> {
            val message = (uiState as TransactionDetailViewModel.TransactionDetailUIState.Error).message
            Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun TransactionDetailContent(transaction: Transaction) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Transaction ID: ${transaction.id}")
        Text("Amount: ${transaction.amount}")
        Text("Date: ${transaction.date}")
    }
}
