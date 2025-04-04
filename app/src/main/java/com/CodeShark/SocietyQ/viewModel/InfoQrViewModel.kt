package com.CodeShark.SocietyQ.viewModel

// Import necessary classes
import android.app.Application // Import Application
import androidx.lifecycle.AndroidViewModel // Import AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.CodeShark.SocietyQ.data.UserPreferences // Import UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Info QR Screen.
 * Holds the data to be encoded in the QR code.
 * Inherits from AndroidViewModel to safely access Application context.
 *
 * @param application The application instance.
 */
class InfoQrViewModel(application: Application) : AndroidViewModel(application) { // Inherit from AndroidViewModel

    // --- State for User Data ---
    // Fetch the actual username from UserPreferences
    private val _userName = MutableStateFlow("") // Initialize empty
    val userName: StateFlow<String> = _userName.asStateFlow()

    // Keep userId hardcoded as a placeholder for now
    private val _userId = MutableStateFlow("837ED1100E31W") // Treat UID as a String
    val userId: StateFlow<String> = _userId.asStateFlow()

    // --- Initialization block to fetch username ---
    init {
        viewModelScope.launch {
            // Get username from UserPreferences using the application context
            val loggedInUsername = UserPreferences.getLoggedInUsername(getApplication()) ?: "Unknown User"
            _userName.value = loggedInUsername
        }
    }

    // --- Derived State for JSON Payload ---
    /**
     * StateFlow emitting the JSON string to be encoded in the QR code.
     * Combines the fetched username and placeholder UID.
     */
    val qrCodeJsonData: StateFlow<String> = combine(userName, userId) { name, uid ->
        // Basic JSON formatting. Consider using a JSON library (like kotlinx.serialization) for complex objects.
        // Ensure the name is properly escaped if it might contain special JSON characters.
        // For simplicity, assuming username doesn't contain quotes or backslashes here.
        """{"Name": "$name", "uid": "$uid"}"""
    }.stateIn(
        scope = viewModelScope,
        // Keep alive while subscribed + 5s buffer
        started = SharingStarted.WhileSubscribed(5000L),
        // Initial value uses the initially empty _userName
        initialValue = """{"Name": "", "uid": "${_userId.value}"}"""
    )

    // No specific actions needed for this simple screen yet.
}
