package com.example.storitechnicaltest.transactions

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.storitechnicaltest.R

@Composable
fun TransactionsScreen(navController: NavController) {
    val viewModel: TransactionsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        when (uiState) {
            is TransactionsViewModel.TransactionsUIState.Start,
            TransactionsViewModel.TransactionsUIState.Loading, -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TransactionsViewModel.TransactionsUIState.Success -> {
                val state = uiState as TransactionsViewModel.TransactionsUIState.Success
                UserScreen(state, navController) {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
            is TransactionsViewModel.TransactionsUIState.Error -> {
                val message = (uiState as TransactionsViewModel.TransactionsUIState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Button(onClick = {
                            viewModel.getUserTransactions()
                        }) {
                            Text(stringResource(R.string.retry))
                        }
                        Button(onClick = {
                            viewModel.logout()
                        }) {
                            Text(stringResource(id = R.string.log_out))
                        }
                    }
                }
            }
        }
    }

    BackHandler {
        (context as? Activity)?.finish()
    }
}

@Composable
fun CircularImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                transformations(CircleCropTransformation())
            }
        )
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier.size(size)
        )
    }
}

@Composable
fun UserScreen(
    state: TransactionsViewModel.TransactionsUIState.Success,
    navController: NavController,
    logout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp) // Optional padding
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.welcome, state.user.firstName, state.user.lastName),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        CircularImage(
            imageUrl = state.user.photoUrl,
            contentDescription = stringResource(R.string.id_photo)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                logout.invoke()
            }) {
                Text(stringResource(R.string.log_out))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.balance, state.user.formatBalance()), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.account_movements), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column {
            state.transactions.forEach { transaction ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("transactionDetail/${transaction.id}") }
                ) {
                    Column {
                        Text(stringResource(R.string.transaction_id, transaction.id))
                        Text(stringResource(R.string.amount, transaction.formatAmount()))
                        Text(stringResource(R.string.date, transaction.date))
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                    }
                }
            }
        }
    }
}