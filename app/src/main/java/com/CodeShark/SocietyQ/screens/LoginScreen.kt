package com.CodeShark.SocietyQ.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Placeholder logo/icon
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.CodeShark.SocietyQ.AppDestinations
import com.CodeShark.SocietyQ.ui.theme.RwaTheme
import com.CodeShark.SocietyQ.viewModel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val username by viewModel.username.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show Snackbar for login errors
    LaunchedEffect(loginError) {
        loginError?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.consumeLoginError() // Clear error after showing
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp), // Add horizontal padding
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // App Logo Placeholder
            Icon(
                imageVector = Icons.Filled.AccountCircle, // Replace with your actual logo
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Login to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Username Field
            OutlinedTextField(
                value = username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = loginError != null // Show error state if there's a general login error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                isError = loginError != null // Show error state if there's a general login error
            )
            // Display error message below fields if present
            if (loginError != null) {
                Text(
                    text = loginError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    viewModel.attemptLogin(context) {
                        // On Success: Navigate to Dashboard and clear Login from backstack
                        navController.navigate(AppDestinations.DASHBOARD) {
                            popUpTo(AppDestinations.LOGIN) { inclusive = true } // Remove login screen
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Disable button while loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("LOGIN")
                }
            }

            // Optional: Add Forgot Password or Sign Up links
            // TextButton(onClick = { /* TODO */ }) { Text("Forgot Password?") }
            // TextButton(onClick = { /* TODO */ }) { Text("Don't have an account? Sign Up") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    RwaTheme {
        // Provide a dummy NavController for the preview
        LoginScreen(navController = NavController(LocalContext.current))
    }
}
