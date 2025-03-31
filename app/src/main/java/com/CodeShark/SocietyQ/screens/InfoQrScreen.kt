package com.CodeShark.SocietyQ.screens

/*import com.cryptic.rwa.ui.theme.RwaTheme
import com.cryptic.rwa.viewModel.InfoQrViewModel
// --- Ensure this import is correct and resolved ---
import io.github.g0dkar.qrcode.compose.QRCode
// --- END Import Check ---

/**
 * Screen to display user information as a QR code.
 *
 * @param viewModel The InfoQrViewModel instance.
 * @param navController The NavController for navigation (used for back button).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoQrScreen(
    viewModel: InfoQrViewModel = viewModel(),
    navController: NavController = rememberNavController() // Default for preview
) {
    val qrData by viewModel.qrCodeJsonData.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle() // Get username for display

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Info QR") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // Match existing style
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp, vertical = 24.dp), // Add padding around content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            Text(
                text = "Scan QR Code", // Title similar to the example image
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Share your info (${userName}) by letting others scan this code.", // Descriptive text
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp) // Space before QR code
            )

            // --- QR Code Display ---
            // Check if qrData is not empty and not the initial placeholder value
            if (qrData.isNotEmpty() && qrData != """{"Name": "", "uid": ""}""") {
                Box(
                    modifier = Modifier
                        .size(240.dp) // Adjust size as needed
                        .background(Color.White, shape = RoundedCornerShape(8.dp)) // White background like example
                        .padding(16.dp), // Padding inside the white box
                    contentAlignment = Alignment.Center
                ) {
                    // Use the QrCode composable from the library
                    QRCode(
                        content = qrData, // The JSON string from ViewModel
                        modifier = Modifier.fillMaxSize() // Fill the padded Box
                        // You can customize colors, add logo etc. here if needed
                        // colors = QRCodeDefaults.colors(foreground = Color.Black, background = Color.White)
                    )
                }
            } else {
                // Placeholder or loading indicator if data isn't ready immediately
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Generating QR Code...") // Indicate loading
            }

            Spacer(modifier = Modifier.height(32.dp)) // Space after QR code

            // Optional: Add a share button similar to the example if needed
            // Button(onClick = { /* TODO: Implement share functionality */ }) {
            //     Icon(Icons.Default.Share, contentDescription = null)
            //     Spacer(Modifier.width(8.dp))
            //     Text("Share")
            // }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
private fun InfoQrScreenPreview() {
    RwaTheme {
        // Provide a dummy NavController for preview if needed
        InfoQrScreen(navController = rememberNavController())
    }
}*/
