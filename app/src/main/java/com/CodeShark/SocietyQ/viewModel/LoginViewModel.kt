package com.CodeShark.SocietyQ.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CodeShark.SocietyQ.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login Screen.
 */
class LoginViewModel : ViewModel() {

    // --- UI State ---
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // --- Event Handlers ---

    fun onUsernameChange(newUsername: String) {
        _username.update { newUsername }
        _loginError.value = null // Clear error on input change
    }

    fun onPasswordChange(newPassword: String) {
        _password.update { newPassword }
        // Note: Password validation is not implemented as per requirement.
        _loginError.value = null // Clear error on input change
    }

    /**
     * Attempts to log the user in.
     * For now, it only checks if the username is not blank and saves it.
     *
     * @param context Context needed to access SharedPreferences.
     * @param onLoginSuccess Lambda to execute on successful login (e.g., navigate).
     */
    fun attemptLogin(context: Context, onLoginSuccess: () -> Unit) {
        val currentUsername = _username.value

        if (currentUsername.isBlank()) {
            _loginError.value = "Username cannot be empty"
            return
        }

        // --- Password Check Placeholder ---
        // In a real app, you would validate the password against a backend or secure storage.
        // For this requirement, we ignore the password field's content for login logic.
        // val currentPassword = _password.value
        // if (currentPassword.isBlank()) {
        //     _loginError.value = "Password cannot be empty"
        //     return
        // }
        // --- End Placeholder ---


        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                // Simulate network/validation delay (optional)
                // delay(500)

                // Save the username to storage upon "successful" login
                UserPreferences.saveLogin(context.applicationContext, currentUsername)

                // Execute the success callback (e.g., navigation)
                onLoginSuccess()

            } catch (e: Exception) {
                _loginError.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun consumeLoginError() {
        _loginError.value = null
    }
}
