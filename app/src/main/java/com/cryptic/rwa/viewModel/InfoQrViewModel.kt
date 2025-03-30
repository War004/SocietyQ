package com.cryptic.rwa.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine // Ensure combine is imported

/**
 * ViewModel for the Info QR Screen.
 * Holds the data to be encoded in the QR code.
 */
class InfoQrViewModel : ViewModel() {

    // --- State for User Data ---
    // In a real app, this might come from user preferences, database, or network
    private val _userName = MutableStateFlow("Mouse321")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userId = MutableStateFlow("837ED1100E31W") // Treat UID as a String
    val userId: StateFlow<String> = _userId.asStateFlow()

    // --- Derived State for JSON Payload ---
    /**
     * StateFlow emitting the JSON string to be encoded in the QR code.
     */
    val qrCodeJsonData: StateFlow<String> = combine(userName, userId) { name, uid ->
        // Basic JSON formatting. Consider using a JSON library (like kotlinx.serialization) for complex objects.
        """{"Name": "$name", "uid": "$uid"}"""
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L), // Keep alive for 5s after last subscriber
        initialValue = """{"Name": "", "uid": ""}""" // Initial empty value
    )

    // No specific actions needed for this simple screen yet.
}
