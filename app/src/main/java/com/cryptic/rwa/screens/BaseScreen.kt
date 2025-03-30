package com.cryptic.rwa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cryptic.rwa.sampleData.DeliveryData
import com.cryptic.rwa.sampleData.EventData
import com.cryptic.rwa.sampleData.ListItemAction
import com.cryptic.rwa.sampleData.NoticeData
import com.cryptic.rwa.viewModel.ListViewModel
import com.cryptic.rwa.sampleData.formatDate
import androidx.compose.ui.text.TextStyle

// --- Helper composable for Icon + Text pairs (Make sure this is available) ---
@Composable
fun IconAndText(
    icon: ImageVector,
    text: String,
    contentDesc: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = 0.7f),
    textColor: Color = tint, // Default text color to match icon tint
    textStyle: TextStyle = MaterialTheme.typography.bodySmall // Use androidx.compose.ui.text.TextStyle
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


@Composable
fun BaseListItem(
    id: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isUnread: Boolean = false,
    heading: String,
    // Line 2 parameters
    line2StartText: String? = null,
    line2StartIcon: ImageVector? = null,
    contentDescLine2Start: String? = null,
    line2EndText: String? = null,
    line2EndIcon: ImageVector? = null,
    contentDescLine2End: String? = null,
    line2EndTextColor: Color? = null, // Added color parameter for line 2 end text
    // Line 3 parameters
    line3Text: String? = null,
    line3Icon: ImageVector? = null,
    contentDescLine3: String? = null,
    line3Alignment: Alignment.Horizontal = Alignment.End, // Default alignment for line 3
    line3TextStyle: TextStyle = MaterialTheme.typography.labelSmall, // Style for line 3
    // Optional middle content slot
    middleContent: (@Composable ColumnScope.() -> Unit)? = null,
    // Slot for action buttons
    actionsContent: (@Composable RowScope.() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = { onClick(id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp, end = 12.dp) // Padding for content excluding actions
                .fillMaxWidth()
        ) {
            // --- Top Section: Unread Indicator & Heading ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = heading,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f) // Allow heading to take available space
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Middle Section: Line 2 (Start and End) ---
            val hasLine2Content = line2StartText != null || line2EndText != null
            if (hasLine2Content) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    // Arrange based on content: SpaceBetween if both exist, Start otherwise
                    horizontalArrangement = if (line2StartText != null && line2EndText != null) Arrangement.Start else Arrangement.Start
                ) {
                    // Line 2 Start Content (e.g., Issue Date)
                    if (line2StartText != null && line2StartIcon != null && contentDescLine2Start != null) {
                        IconAndText(
                            icon = line2StartIcon,
                            text = line2StartText,
                            contentDesc = contentDescLine2Start
                        )
                    } else if (line2StartText != null) {
                        // Fallback if only text is provided for start
                        Text(line2StartText, style = MaterialTheme.typography.bodySmall)
                    }

                    // Spacer if both start and end content exist
                    if (line2StartText != null && line2EndText != null) {
                        Spacer(modifier = Modifier.width(16.dp)) // Space between dates
                    }


                    // Line 2 End Content (e.g., Expiry Date)
                    if (line2EndText != null && line2EndIcon != null && contentDescLine2End != null) {
                        IconAndText(
                            icon = line2EndIcon,
                            text = line2EndText,
                            contentDesc = contentDescLine2End,
                            textColor = line2EndTextColor ?: LocalContentColor.current.copy(alpha = 0.7f) // Use provided color or default
                        )
                    } else if (line2EndText != null) {
                        // Fallback if only text is provided for end
                        Text(
                            line2EndText,
                            style = MaterialTheme.typography.bodySmall,
                            color = line2EndTextColor ?: LocalContentColor.current.copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp)) // Reduced spacer after line 2
            }


            // --- Optional Middle Content Slot ---
            middleContent?.invoke(this)

            // Add spacer only if middle content OR line 3 OR actions exist
            if (middleContent != null || line3Text != null || actionsContent != null) {
                Spacer(modifier = Modifier.height(8.dp)) // Adjusted spacer
            }

            // --- Bottom Section: Line 3 (e.g., Issuer) ---
            if (line3Text != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = when (line3Alignment) { // Apply alignment
                        Alignment.CenterHorizontally -> Arrangement.Center
                        Alignment.End -> Arrangement.End
                        else -> Arrangement.Start
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (line3Icon != null && contentDescLine3 != null) {
                        IconAndText(
                            icon = line3Icon,
                            text = line3Text,
                            contentDesc = contentDescLine3,
                            textStyle = line3TextStyle // Apply specific style for line 3
                        )
                    } else {
                        // Fallback if only text is provided for line 3
                        Text(line3Text, style = line3TextStyle)
                    }
                }
            }

            // Add spacer only if both line 3 and actions exist
            if (line3Text != null && actionsContent != null) {
                Spacer(modifier = Modifier.height(8.dp))
            } else if (actionsContent != null && line3Text == null && middleContent == null && !hasLine2Content) {
                // Add spacer before actions if there's no other content below the title
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Actions Row ---
            actionsContent?.let { content ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, top = 4.dp), // Padding for the actions row itself
                    horizontalArrangement = Arrangement.End, // Align buttons to the end
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    content() // Invoke the actions composable lambda
                }
            }
        } // End Main Column
    } // End Card
}



// Base Screen Structure (Can be extracted further if needed)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T, VM : ListViewModel<T>> BaseListScreen(
    viewModel: VM,
    screenTitle: String,
    itemContent: @Composable (T, (String) -> Unit, (ListItemAction) -> Unit) -> Unit,
    navController: NavController
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
                duration = SnackbarDuration.Long
            )
            viewModel.consumeError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { TopAppBar(title = { Text(screenTitle) },
            navigationIcon = {
                IconButton(onClick = {navController.navigateUp()}){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        ) } // Example TopAppBar
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && items.isEmpty()) { // Show loading only if list is empty initially
                CircularProgressIndicator()
            } else if (items.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = items,
                        // Provide a stable key based on your data's unique ID
                        key = { item -> (item as? NoticeData)?.id ?: (item as? DeliveryData)?.id ?: (item as? EventData)?.id ?: item.hashCode() } // More robust key
                    ) { itemData ->
                        // Pass lambdas connecting to ViewModel functions
                        itemContent(itemData, viewModel::onItemClick, viewModel::onAction)
                    }
                }
            } else if (!isLoading && error == null) {
                // Handle empty state (no items, not loading, no error)
                Text("No items found.")
            }
            // Error is handled by Snackbar, but you could show error text here too
        }
    }
}


// --- NoticeItem using BaseListItem (Achieves the desired look) ---
@Composable
fun NoticeItem(
    notice: NoticeData,
    onItemClick: (String) -> Unit,
    onAction: (ListItemAction) -> Unit // Still accept action lambda, even if not used here
) {
    // Determine expiry color (as in the target code)
    val expiryColor = notice.expiryDate?.let { expiry ->
        if (expiry.isBefore(LocalDate.now())) {
            MaterialTheme.colorScheme.error // Indicate expired
        } else {
            LocalContentColor.current.copy(alpha = 0.7f) // Default subtle
        }
    }

    BaseListItem(
        id = notice.id,
        onClick = onItemClick,
        isUnread = notice.isUnread,
        heading = notice.heading,
        // Line 2: Issue Date (Start) and Expiry Date (End)
        line2StartText = formatDate(notice.issueDate),
        line2StartIcon = Icons.Outlined.CalendarToday,
        contentDescLine2Start = "Issue date",
        line2EndText = notice.expiryDate?.let { formatDate(it) },
        line2EndIcon = notice.expiryDate?.let { Icons.Outlined.EventBusy },
        contentDescLine2End = "Expiry date",
        line2EndTextColor = expiryColor, // Pass the calculated color
        // Line 3: Issuer Info
        line3Text = notice.issuer,
        line3Icon = Icons.Outlined.PersonOutline,
        contentDescLine3 = "Issued by",
        line3Alignment = Alignment.End, // Align issuer to the end
        line3TextStyle = MaterialTheme.typography.labelSmall, // Use smaller text for issuer
        // No middle content or actions needed for this specific item
        middleContent = null,
        actionsContent = null
    )
}


// DELIVERY ITEM (Using BaseListItem)
@Composable
fun DeliveryItem(
    delivery: DeliveryData,
    onItemClick: (String) -> Unit,
    onAction: (ListItemAction) -> Unit
) {
    BaseListItem(
        id = delivery.id,
        onClick = onItemClick,
        isUnread = delivery.isUnread,
        heading = delivery.productName,
        // Line 2: Received Date
        line2StartText = formatDate(delivery.receivedDate),
        line2StartIcon = Icons.Outlined.CalendarToday,
        contentDescLine2Start = "Received date",
        // Line 3: Location
        line3Text = delivery.location,
        line3Icon = Icons.Outlined.MeetingRoom, // Changed icon for location
        contentDescLine3 = "Location",
        line3Alignment = Alignment.Start, // Align location to start
        // --- Add Action Button ---
        actionsContent = {
            Button(
                onClick = { onAction(ListItemAction.Track(delivery.id, null)) },
                modifier = Modifier.heightIn(min = 36.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Filled.TrackChanges, contentDescription="Track", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Track", fontSize = 12.sp)
            }
        }
    )
}

// EVENT ITEM (Using BaseListItem)
@Composable
fun EventItem(
    event: EventData,
    onItemClick: (String) -> Unit,
    onAction: (ListItemAction) -> Unit
) {
    BaseListItem(
        id = event.id,
        onClick = onItemClick,
        isUnread = event.isUnread,
        heading = event.eventName,
        // Line 2: Event Date (Start) and End Date (End)
        line2StartText = formatDate(event.eventDate),
        line2StartIcon = Icons.Outlined.Event,
        contentDescLine2Start = "Event date",
        line2EndText = event.eventEndDate?.let { "Ends: ${formatDate(it)}" },
        line2EndIcon = event.eventEndDate?.let { Icons.Outlined.Schedule },
        contentDescLine2End = "Event end date",
        // Line 3: Organizer
        line3Text = event.organizer,
        line3Icon = Icons.Outlined.Groups,
        contentDescLine3 = "Organizer",
        line3Alignment = Alignment.Start, // Align organizer to start
        // --- Add Action Buttons ---
        actionsContent = {
            OutlinedButton(
                onClick = { onAction(ListItemAction.Rsvp(event.id, false)) },
                modifier = Modifier.heightIn(min = 36.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) { Text("Maybe", fontSize = 12.sp) }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onAction(ListItemAction.Rsvp(event.id, true)) },
                modifier = Modifier.heightIn(min = 36.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Filled.Rsvp, contentDescription="RSVP Yes", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Attend", fontSize = 12.sp)
            }
        }
    )
}
// --- TODO: BillItem Example ---
/*
@Composable
fun BillItem(bill: BillData, onItemClick: (String) -> Unit, onAction: (ListItemAction) -> Unit) {
    BaseListItem(
        id = bill.id,
        onClick = onItemClick,
        // ... Map BillData ...
        actionsContent = {
            if (bill.isPending) { // Only show Pay if pending
                Button(onClick = { onAction(ListItemAction.Pay(bill.id)) }) {
                     Icon(Icons.Filled.Payment, contentDescription="Pay Bill", modifier = Modifier.size(18.dp))
                     Spacer(Modifier.width(4.dp))
                    Text("Pay Now")
                }
            }
        }
    )
}*/

