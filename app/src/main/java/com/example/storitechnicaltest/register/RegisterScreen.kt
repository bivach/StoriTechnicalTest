package com.example.storitechnicaltest.register

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.storitechnicaltest.R
import java.io.File

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val context = LocalContext.current

    var photoFile: File? = remember { null }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoFile?.let {
                val uri = FileProvider.getUriForFile(context, AUTHORITY, it)
                viewModel.updateFormState(formState.copy(photoUri = uri, photoUriError = null))
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ -> }
    )

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    when (uiState) {
        is RegisterViewModel.RegisterUIState.Start -> {
            RegisterForm(formState, viewModel) {
                photoFile = createImageFile(context)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(
                        MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                            context,
                            AUTHORITY,
                            photoFile!!
                        )
                    )
                }
                launcher.launch(intent)
            }
        }

        is RegisterViewModel.RegisterUIState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is RegisterViewModel.RegisterUIState.Success -> {
            LaunchedEffect(Unit) {
                Toast.makeText(
                    context,
                    context.getString(R.string.registration_successful), Toast.LENGTH_SHORT
                ).show()
                navController.navigate("login")
            }
        }

        is RegisterViewModel.RegisterUIState.Error -> {
            val message = (uiState as RegisterViewModel.RegisterUIState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun RegisterForm(
    formState: RegisterViewModel.RegisterFormState,
    viewModel: RegisterViewModel,
    onTakePhotoClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make the whole form scrollable
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
            label = { Text(stringResource(R.string.email)) },
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
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            isError = formState.passwordError != null
        )
        if (formState.passwordError != null) {
            Text(formState.passwordError, color = MaterialTheme.colorScheme.error)
        }
        TextField(
            value = formState.firstName,
            onValueChange = {
                viewModel.updateFormState(
                    formState.copy(
                        firstName = it,
                        firstNameError = null
                    )
                )
            },
            label = { Text(stringResource(R.string.first_name)) },
            isError = formState.firstNameError != null
        )
        if (formState.firstNameError != null) {
            Text(formState.firstNameError, color = MaterialTheme.colorScheme.error)
        }
        TextField(
            value = formState.lastName,
            onValueChange = {
                viewModel.updateFormState(
                    formState.copy(
                        lastName = it,
                        lastNameError = null
                    )
                )
            },
            label = { Text(stringResource(R.string.last_name)) },
            isError = formState.lastNameError != null
        )
        if (formState.lastNameError != null) {
            Text(formState.lastNameError, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onTakePhotoClicked() }) {
            Text(stringResource(R.string.take_id_photo))
        }
        if (formState.photoUriError != null) {
            Text(formState.photoUriError, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (viewModel.validateForm()) {
                viewModel.registerUserWithPhoto()
            }
        }) {
            Text(stringResource(R.string.register))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateFormState(
                formState.copy(
                    email = "example@example.com",
                    password = "password",
                    firstName = "John",
                    lastName = "Doe"
                )
            )
        }) {
            Text(stringResource(R.string.fill_form_with_fake_data))
        }
        Spacer(modifier = Modifier.height(16.dp))
        formState.photoUri?.let {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val painter = rememberImagePainter(
                    data = it,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                )
                Image(
                    painter = painter,
                    contentDescription = "ID Photo",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

private fun createImageFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
}

const val AUTHORITY = "com.example.storitechnicaltest.fileprovider"
