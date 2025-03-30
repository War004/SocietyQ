package com.cryptic.rwa.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay // For simulating submission delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// --- Data Structures ---

enum class ComplaintType {
    COMPLAINT, SUGGESTION
}

// --- ViewModel ---

class ComplaintViewModel : ViewModel() {

    // --- State Flows for UI ---

    private val _selectedType = MutableStateFlow(ComplaintType.COMPLAINT)
    val selectedType: StateFlow<ComplaintType> = _selectedType.asStateFlow()

    // In a real app, this might come from resources or a remote config
    val availableSubjects = listOf(
        "Select Subject...", // Placeholder/Hint
        "Water Supply",
        "Electricity",
        "Cleanliness",
        "Security",
        "Parking",
        "Park/Common Area",
        "Noise Issue",
        "Maintenance Request",
        "Others"
    )

    private val _selectedSubject = MutableStateFlow(availableSubjects[0]) // Default to hint
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _otherSubjectText = MutableStateFlow("")
    val otherSubjectText: StateFlow<String> = _otherSubjectText.asStateFlow()

    private val _detailsText = MutableStateFlow("")
    val detailsText: StateFlow<String> = _detailsText.asStateFlow()

    // Holds URIs from the picker temporarily. Caching is needed for robustness!
    private val _selectedMediaUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMediaUris: StateFlow<List<Uri>> = _selectedMediaUris.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // For field-specific errors shown below the field
    private val _subjectError = MutableStateFlow<String?>(null)
    val subjectError: StateFlow<String?> = _subjectError.asStateFlow()

    private val _detailsError = MutableStateFlow<String?>(null)
    val detailsError: StateFlow<String?> = _detailsError.asStateFlow()

    // For general errors shown in a Snackbar
    private val _snackbarError = MutableStateFlow<String?>(null)
    val snackbarError: StateFlow<String?> = _snackbarError.asStateFlow()

    // --- Derived State for Submit Button ---

    val isSubmitEnabled: StateFlow<Boolean> = combine(
        selectedSubject, otherSubjectText, detailsText, isLoading
    ) { subject, otherSubject, details, loading ->
        val subjectValid = subject != availableSubjects[0] && (subject != "Others" || otherSubject.isNotBlank())
        val detailsValid = details.isNotBlank()
        subjectValid && detailsValid && !loading
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    // --- Event Handlers ---

    fun onTypeSelected(type: ComplaintType) {
        _selectedType.value = type
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
        // Clear subject error when a valid selection is made (or Others is chosen)
        if (subject != availableSubjects[0]) {
            _subjectError.value = null
        }
        // Clear 'other' text if switching away from 'Others'
        if (subject != "Others") {
            _otherSubjectText.value = ""
        }
    }

    fun onOtherSubjectChanged(text: String) {
        _otherSubjectText.value = text
        // Clear subject error if user starts typing in 'Others' field
        if (_selectedSubject.value == "Others" && text.isNotBlank()) {
            _subjectError.value = null
        }
    }

    fun onDetailsChanged(text: String) {
        _detailsText.value = text
        // Clear details error if user starts typing
        if (text.isNotBlank()) {
            _detailsError.value = null
        }
    }

    fun onMediaSelected(uris: List<Uri>) {
        // TODO: Implement robust handling:
        // 1. Check limits (number/size)
        // 2. Copy selected files to app's private cache directory *immediately*
        // 3. Store URIs/paths of the *cached* files in the stateflow
        // 4. Handle potential file copying errors
        _selectedMediaUris.update { currentList ->
            // Simple add for now, replace with cached file logic
            (currentList + uris).distinct()
        }
    }

    fun onRemoveMedia(uri: Uri) {
        // TODO: If using cached files, delete the cached file as well
        _selectedMediaUris.update { currentList ->
            currentList.filterNot { it == uri }
        }
    }

    fun onSubmitClicked() {
        if (validateForm()) {
            viewModelScope.launch {
                _isLoading.value = true
                // --- TODO: Replace with actual submission logic ---
                // 1. Gather all data: type, subject (or otherSubject), details, cached media paths
                // 2. Prepare data transfer object (DTO)
                // 3. Enqueue WorkManager job for upload / API call
                // 4. Observe WorkInfo for success/failure feedback

                // --- Simulation ---
                println("Submitting: Type=${_selectedType.value}, Subject=${_selectedSubject.value}, Other=${_otherSubjectText.value}, Details=${_detailsText.value}, Media=${_selectedMediaUris.value}")
                delay(2000) // Simulate network delay
                val success = true // Simulate success/failure
                // --- End Simulation ---

                _isLoading.value = false
                if (success) {
                    // Optionally navigate back or clear form
                    _snackbarError.value = "Submission Successful!" // Use a success snackbar if needed
                    // clearForm() // Example function to reset state
                } else {
                    _snackbarError.value = "Submission Failed. Please try again."
                }
            }
        }
    }

    fun consumeSnackbarError() {
        _snackbarError.value = null
    }

    // --- Private Helpers ---

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate Subject
        val subject = _selectedSubject.value
        val otherSubject = _otherSubjectText.value
        if (subject == availableSubjects[0]) {
            _subjectError.value = "Please select a subject"
            isValid = false
        } else if (subject == "Others" && otherSubject.isBlank()) {
            _subjectError.value = "Please specify the subject for 'Others'"
            // Also mark the 'other' text field as error visually if needed
            isValid = false
        } else {
            _subjectError.value = null
        }

        // Validate Details
        if (_detailsText.value.isBlank()) {
            _detailsError.value = "Please provide details"
            isValid = false
        } else {
            _detailsError.value = null
        }

        // TODO: Add validation for media if needed (e.g., at least one image required?)

        return isValid
    }

    private fun clearForm() {
        _selectedType.value = ComplaintType.COMPLAINT
        _selectedSubject.value = availableSubjects[0]
        _otherSubjectText.value = ""
        _detailsText.value = ""
        _selectedMediaUris.value = emptyList()
        _subjectError.value = null
        _detailsError.value = null
        // Don't clear snackbar error here, let it be shown
    }
}