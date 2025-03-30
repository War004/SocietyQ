package com.cryptic.rwa.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cryptic.rwa.viewModel.ComplaintType
import com.cryptic.rwa.viewModel.ComplaintViewModel
import com.google.accompanist.permissions.*
import android.os.Build

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ComplaintScreen(
    viewModel: ComplaintViewModel = viewModel() // Obtain ViewModel instance
) {
    // --- State Collection ---
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()
    val otherSubjectText by viewModel.otherSubjectText.collectAsStateWithLifecycle()
    val detailsText by viewModel.detailsText.collectAsStateWithLifecycle()
    val selectedMediaUris by viewModel.selectedMediaUris.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val subjectError by viewModel.subjectError.collectAsStateWithLifecycle()
    val detailsError by viewModel.detailsError.collectAsStateWithLifecycle()
    val isSubmitEnabled by viewModel.isSubmitEnabled.collectAsStateWithLifecycle()
    val snackbarError by viewModel.snackbarError.collectAsStateWithLifecycle()

    // --- UI State ---
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var showDropdownMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // --- Activity Result Launchers ---
    // Multiple Media Picker (Requires API Level checks or Accompanist Permissions)
    val multipleMediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                viewModel.onMediaSelected(uris)
            }
        }
    )

    // --- Permission Handling (Using Accompanist) ---
    // Determine the correct permissions to request based on the runtime SDK version
    val permissionsToRequest = remember { // Use remember to calculate the list once
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 (API 33) and above
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            // For versions below Android 13
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    // Now use the dynamically determined list
    val mediaPermissions = rememberMultiplePermissionsState(
        permissions = permissionsToRequest
    )



    // --- Effect for Snackbar ---
    LaunchedEffect(snackbarError) {
        snackbarError?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.consumeSnackbarError() // Reset error after showing
        }
    }

    // --- UI Structure ---
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        // Optional: Add a TopAppBar if needed
        topBar = {
            TopAppBar(title = { Text("Submit Complaint / Suggestion") })
        },
        bottomBar = {
            // Bottom bar for the Submit button ensures it's always visible
            Surface(shadowElevation = 8.dp) { // Add elevation for separation
                Button(
                    onClick = viewModel::onSubmitClicked,
                    enabled = isSubmitEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp) // Padding for the button within the bottom bar
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary // Color for spinner on button
                        )
                    } else {
                        Text("SUBMIT")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold (excluding bottomBar area)
                .verticalScroll(scrollState) // Make the main content area scrollable
                .padding(horizontal = 16.dp, vertical = 20.dp) // Padding for form content
        ) {

            // 1. Complaint / Suggestion Type Toggle
            ComplaintTypeSelector(
                selectedType = selectedType,
                onTypeSelected = viewModel::onTypeSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Subject Dropdown
            ExposedDropdownMenuBox(
                expanded = showDropdownMenu,
                onExpandedChange = { showDropdownMenu = !showDropdownMenu }
            ) {
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = {}, // Selection handled by menu items
                    readOnly = true,
                    label = { Text("Subject") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdownMenu) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // Anchor for the dropdown menu
                    isError = subjectError != null && selectedSubject == viewModel.availableSubjects[0],
                    supportingText = {
                        // Only show error if it's the initial hint selected
                        if (subjectError != null && selectedSubject == viewModel.availableSubjects[0]) {
                            Text(subjectError!!)
                        }
                    }
                )
                ExposedDropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    viewModel.availableSubjects.forEach { subject ->
                        // Don't show the initial hint in the dropdown list itself
                        if (subject != viewModel.availableSubjects[0]) {
                            DropdownMenuItem(
                                text = { Text(subject) },
                                onClick = {
                                    viewModel.onSubjectSelected(subject)
                                    showDropdownMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // 3. "Others" Subject Text Field (Conditional)
            if (selectedSubject == "Others") {
                Spacer(modifier = Modifier.height(8.dp)) // Space between dropdown and 'others' field
                OutlinedTextField(
                    value = otherSubjectText,
                    onValueChange = viewModel::onOtherSubjectChanged,
                    label = { Text("Please specify subject") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = subjectError != null && otherSubjectText.isBlank(),
                    supportingText = {
                        if (subjectError != null && otherSubjectText.isBlank()) {
                            Text(subjectError!!)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Details Text Field
            OutlinedTextField(
                value = detailsText,
                onValueChange = viewModel::onDetailsChanged,
                label = { Text("Details") },
                placeholder = { Text("Describe your ${selectedType.name.lowercase()} here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp), // Minimum height for details
                isError = detailsError != null,
                supportingText = {
                    if (detailsError != null) {
                        Text(detailsError!!)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Add Media Button
            Button(
                onClick = {
                    // Check permissions before launching picker
                    if (mediaPermissions.allPermissionsGranted) {
                        multipleMediaPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    } else {
                        mediaPermissions.launchMultiplePermissionRequest()
                    }
                },
                // Optional: Style as OutlinedButton if preferred
                // colors = ButtonDefaults.outlinedButtonColors(),
                // border = ButtonDefaults.outlinedButtonBorder
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Photos / Videos")
            }
            // Display rationale if permission denied
            if (!mediaPermissions.allPermissionsGranted && mediaPermissions.shouldShowRationale) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Media permission is needed to attach photos or videos. Please grant permission.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // 6. Media Previews
            if (selectedMediaUris.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(items = selectedMediaUris, key = { uri -> uri.toString() }) { uri ->
                        MediaPreviewItem(
                            uri = uri,
                            onRemoveClick = { viewModel.onRemoveMedia(uri) }
                        )
                    }
                }
            }

            // Spacer at the bottom to ensure content doesn't stick directly to bottom bar
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Helper Composables ---

@Composable
fun ComplaintTypeSelector(
    selectedType: ComplaintType,
    onTypeSelected: (ComplaintType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        ComplaintType.entries.forEach { type ->
            val isSelected = selectedType == type
            val buttonColors = if (isSelected) {
                ButtonDefaults.buttonColors() // Default filled button
            } else {
                ButtonDefaults.outlinedButtonColors() // Outlined button
            }
            val border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null

            Button(
                onClick = { onTypeSelected(type) },
                colors = buttonColors,
                border = border,
                modifier = Modifier.weight(1f) // Make buttons take equal space
            ) {
                Text(type.name.replaceFirstChar { it.uppercase() }) // Capitalize COMPLAINT/SUGGESTION
            }
        }
    }
}

@Composable
fun MediaPreviewItem(
    uri: Uri,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp) // Size of the preview item
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = "Selected media preview",
            contentScale = ContentScale.Crop, // Crop to fit the box
            modifier = Modifier.matchParentSize()
        )
        // Remove Button (Small icon button top-right corner)
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp) // Padding around the icon button
                .size(20.dp) // Small size for the remove button
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)) // Semi-transparent background
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove media",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp) // Size of the 'X' icon itself
            )
        }
    }
}

// --- Preview ---

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun ComplaintScreenPreview() {
    // Use a fake ViewModel or provide initial state for preview if needed
    MaterialTheme { // Wrap in your app's theme
        ComplaintScreen()
    }
}