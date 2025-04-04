package com.CodeShark.SocietyQ.screens

// Import standard Image (Might not be needed if SubcomposeAsyncImageContent handles everything)
// import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContactSupport // Icon for contact
import androidx.compose.material.icons.filled.HelpOutline // Placeholder icon
import androidx.compose.material.icons.filled.LocationOn // Icon for location
import androidx.compose.material.icons.outlined.CalendarToday // Icon for date
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
// --- Updated Coil Imports ---
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage // Use SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent // Use SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.CodeShark.SocietyQ.sampleData.LostFoundItemData
import com.CodeShark.SocietyQ.sampleData.LostFoundStatus
import com.CodeShark.SocietyQ.sampleData.formatDate
import com.CodeShark.SocietyQ.ui.theme.RwaTheme
import com.CodeShark.SocietyQ.viewModel.LostFoundViewModel
import java.time.LocalDate

// LostFoundScreen composable remains the same as before...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundScreen(
    navController: NavController,
    viewModel: LostFoundViewModel = viewModel()
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show Snackbar on error
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short // Use Short for errors unless critical
            )
            viewModel.consumeError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // Match other screens
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && items.isEmpty()) {
                CircularProgressIndicator()
            } else if (!isLoading && items.isEmpty() && error == null) {
                Text("No lost or found items reported yet.")
            } else if (items.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Two columns
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp), // Padding around the grid
                    verticalArrangement = Arrangement.spacedBy(12.dp), // Spacing between rows
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Spacing between columns
                ) {
                    items(items = items, key = { it.id }) { item ->
                        LostFoundGridItem(item = item, onClick = { viewModel.onItemClick(item.id) })
                    }
                }
            }
            // Error state is primarily handled by the Snackbar
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundGridItem(
    item: LostFoundItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (item.status) {
        LostFoundStatus.LOST -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        LostFoundStatus.FOUND -> Color(0xFFC8E6C9) // Light Green
    }
    val statusTextColor = when (item.status) {
        LostFoundStatus.LOST -> MaterialTheme.colorScheme.onErrorContainer
        LostFoundStatus.FOUND -> Color(0xFF1B5E20) // Dark Green
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            // --- Image Section (Using SubcomposeAsyncImage) ---
            // AsyncImage is suitable here as it handles URLs.
            // Currently loads placeholder URLs, will load real API URLs in the future.
            SubcomposeAsyncImage( // Changed to SubcomposeAsyncImage
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl) // Load the image URL (placeholder or future API URL)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer), // Background applied here affects the whole area
                contentScale = ContentScale.Crop,
                // The lambda here is the content slot for SubcomposeAsyncImage
                content = {
                    // 'this' is SubcomposeAsyncImageScope, which has painter
                    val state = painter.state
                    when (state) {
                        is AsyncImagePainter.State.Loading -> {
                            // Show placeholder while loading
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                            }
                        }
                        is AsyncImagePainter.State.Error, is AsyncImagePainter.State.Empty -> {
                            // Show placeholder icon on error or if URL is null/invalid
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.HelpOutline, contentDescription = "Image unavailable", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                        is AsyncImagePainter.State.Success -> {
                            // Display the loaded image using SubcomposeAsyncImageContent
                            // It handles applying the modifier, contentScale etc. correctly
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            )
            // --- End Image Section ---


            // Content Section (Remains the same)
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                AssistChip(
                    onClick = { /* No action needed */ },
                    label = { Text(item.status.name, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = statusColor,
                        labelColor = statusTextColor
                    ),
                    border = null,
                    modifier = Modifier.height(24.dp)
                )
                IconAndText(
                    icon = Icons.Outlined.CalendarToday,
                    text = formatDate(item.dateReported),
                    contentDesc = "Date Reported",
                    textStyle = MaterialTheme.typography.bodySmall
                )
                IconAndText(
                    icon = Icons.Filled.LocationOn,
                    text = item.location,
                    contentDesc = "Location",
                    textStyle = MaterialTheme.typography.bodySmall
                )
                IconAndText(
                    icon = Icons.Filled.ContactSupport,
                    text = item.contactInfo,
                    contentDesc = "Contact Info",
                    textStyle = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// IconAndText composable (assuming it's accessible or defined)

// Previews remain the same...
@Preview(showBackground = true)
@Composable
private fun LostFoundScreenPreview() {
    RwaTheme {
        LostFoundScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, widthDp = 180)
@Composable
private fun LostFoundGridItemPreview() {
    val sampleItem = LostFoundItemData(
        id = "1",
        name = "Blue Water Bottle",
        description = "Steel blue water bottle",
        imageUrl = null,
        status = LostFoundStatus.FOUND,
        dateReported = LocalDate.now(),
        location = "Clubhouse Gym",
        contactInfo = "Contact Security Desk"
    )
    RwaTheme {
        LostFoundGridItem(item = sampleItem, onClick = {})
    }
}

// --- Add IconAndText if it's not accessible from BaseScreen ---
/*
@Composable
fun IconAndText(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    contentDesc: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = 0.7f),
    textColor: Color = tint, // Default text color to match icon tint
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodySmall
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            modifier = Modifier.size(16.dp), // Smaller icon size
            tint = tint
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = textStyle,
            color = textColor,
            fontSize = 12.sp // Slightly smaller font for meta-info
        )
    }
}
*/
