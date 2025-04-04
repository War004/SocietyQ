package com.CodeShark.SocietyQ.screens

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.CodeShark.SocietyQ.ui.theme.RwaTheme
import com.CodeShark.SocietyQ.viewModel.InfoQrViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set


/**
 * Screen to display user information as a QR code using ZXing.
 *
 * @param viewModel The InfoQrViewModel instance.
 * @param navController The NavController for navigation (used for back button).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoQrScreen(
    viewModel: InfoQrViewModel = viewModel(), // Use viewModel() to get the instance
    navController: NavController // Pass NavController explicitly
) {
    // Collect state from ViewModel using collectAsStateWithLifecycle
    val qrDataString by viewModel.qrCodeJsonData.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    // State to hold the generated Bitmap
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    // Coroutine scope for background generation
    val coroutineScope = rememberCoroutineScope()
    // Remember the size for QR code generation
    val qrCodeSize = 256 // Define QR code size in pixels

    // Effect to generate QR code when data changes
    LaunchedEffect(qrDataString, userName) {
        // Only generate if data is valid and not the initial placeholder
        if (qrDataString.isNotEmpty() && userName.isNotEmpty() && qrDataString != """{"Name": "", "uid": "${viewModel.userId.value}"}""") {
            // Launch in background thread
            coroutineScope.launch(Dispatchers.Default) {
                qrBitmap = createQrBitmap(qrDataString, qrCodeSize)
            }
        } else {
            qrBitmap = null // Reset bitmap if data is invalid/loading
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Info QR") },
                navigationIcon = {
                    // Button to navigate back
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                // Match the styling of other TopAppBars
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(horizontal = 32.dp, vertical = 24.dp), // Add padding around content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            Text(
                text = "Scan QR Code", // Title
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Display descriptive text including the username
            Text(
                text = "Share your info ($userName) by letting others scan this code.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp) // Space before QR code
            )

            // --- QR Code Display ---
            Box(
                modifier = Modifier
                    .size(240.dp) // Display size (can be different from generation size)
                    .background(androidx.compose.ui.graphics.Color.White, shape = RoundedCornerShape(8.dp)) // White background
                    .padding(16.dp), // Padding inside the white box
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    // Display the generated Bitmap using Compose Image
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "User Info QR Code",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit // Fit the bitmap within the box
                    )
                } else {
                    // Show loading indicator while bitmap is null or being generated
                    CircularProgressIndicator()
                    // Optionally add text like "Generating QR Code..."
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Space after QR code
        }
    }
}

/**
 * Generates a QR code Bitmap from the given content string using ZXing.
 *
 * @param content The string data to encode in the QR code.
 * @param size The desired width and height of the QR code in pixels.
 * @return The generated Bitmap, or null if generation fails.
 */
private fun createQrBitmap(content: String, size: Int): Bitmap? {
    return try {
        // Configure QR code hints (optional but recommended)
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L, // Error correction level
            EncodeHintType.MARGIN to 1 // Margin around QR code (ZXing default is 4)
        )

        // Encode the content into a BitMatrix
        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )

        // Create a Bitmap from the BitMatrix
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bitmap
    } catch (e: Exception) {
        // Log error in a real app
        println("Error generating QR Bitmap: ${e.message}")
        e.printStackTrace()
        null // Return null on failure
    }
}


@Preview(showBackground = true, device = "id:pixel_6")
@Composable
private fun InfoQrScreenPreview() {
    RwaTheme {
        // Provide a dummy NavController for preview
        // Note: ViewModel data (username) won't be fetched in preview unless mocked
        // QR code won't generate in preview without actual data/ViewModel.
        InfoQrScreen(navController = rememberNavController())
    }
}
