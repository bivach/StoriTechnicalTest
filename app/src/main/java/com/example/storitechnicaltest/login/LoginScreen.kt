package com.example.storitechnicaltest.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.storitechnicaltest.R

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current

    when (uiState) {
        is LoginViewModel.LoginUIState.Start -> {
            LoginForm(formState = formState, viewModel = viewModel, navController = navController)
        }

        is LoginViewModel.LoginUIState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoginViewModel.LoginUIState.Success -> {
            LaunchedEffect(Unit) {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        is LoginViewModel.LoginUIState.Error -> {
            val message = (uiState as LoginViewModel.LoginUIState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler {
        (context as? Activity)?.finish()
    }
}

@Composable
fun LoginForm(
    formState: LoginViewModel.LoginFormState,
    viewModel: LoginViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = formState.email,
            onValueChange = {
                viewModel.updateFormState(
                    formState.copy(
                        email = it,
                        emailError = null
                    )
                )
            },
            label = { Text(stringResource(id = R.string.email)) },
            isError = formState.emailError != null
        )
        if (formState.emailError != null) {
            Text(formState.emailError, color = MaterialTheme.colorScheme.error)
        }
        TextField(
            value = formState.password,
            onValueChange = {
                viewModel.updateFormState(
                    formState.copy(
                        password = it,
                        passwordError = null
                    )
                )
            },
            label = { Text(stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            isError = formState.passwordError != null
        )
        if (formState.passwordError != null) {
            Text(formState.passwordError, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (viewModel.validateForm()) {
                viewModel.loginUser()
            }
        }) {
            Text(stringResource(R.string.login))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("register") }) {
            Text(stringResource(id = R.string.register))
        }
    }
}
