package com.CodeShark.SocietyQ

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.* // Keep existing filled icons
import androidx.compose.material.icons.outlined.* // Keep existing outlined icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.CodeShark.SocietyQ.data.UserPreferences // Import UserPreferences
import com.CodeShark.SocietyQ.screens.ComplaintScreen
import com.CodeShark.SocietyQ.screens.BillsScreen
import com.CodeShark.SocietyQ.screens.InfoQrScreen // <<<--- ADD IMPORT for InfoQrScreen
import com.CodeShark.SocietyQ.screens.LoginScreen // Import LoginScreen
import com.CodeShark.SocietyQ.screens.LostFoundScreen
import com.CodeShark.SocietyQ.ui.theme.RwaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Determine the start destination based on login status
        val startDestination = if (UserPreferences.isLoggedIn(this)) {
            AppDestinations.DASHBOARD
        } else {
            AppDestinations.LOGIN
        }

        setContent {
            RwaApp(startDestination = startDestination) // Pass start destination
        }
    }
}

// --- RwaApp NavHost ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RwaApp(startDestination: String) { // Accept start destination
    RwaTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination,

            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500, easing = LinearOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500, easing = LinearOutSlowInEasing)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500, easing = LinearOutSlowInEasing)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500, easing = LinearOutSlowInEasing)
                )
            }

        ) {
            // --- Add Login Screen Route ---
            composable(AppDestinations.LOGIN) {
                LoginScreen(navController = navController)
            }
            composable(AppDestinations.DASHBOARD) {
                RwaScreenUI(navController = navController)
            }

            // --- Restore Actual Screen Calls ---
            composable(AppDestinations.NOTICE_LIST) {
                // Call the actual NoticeScreen composable
                NoticeScreen(navController = navController) // <<<--- FIXED
            }
            composable(AppDestinations.PERSONAL_NOTICE_LIST) {
                // Call the actual PersonalNoticeScreen composable
                PersonalNoticeScreen(navController = navController) // <<<--- FIXED
            }
            composable(AppDestinations.DELIVERY_LIST) {
                // Call the actual DeliveryScreen composable
                DeliveryScreen(navController = navController) // <<<--- FIXED
            }
            composable(AppDestinations.BILL_LIST) {
                BillsScreen(navController = navController) // Was already correct
            }
            composable(AppDestinations.EVENT_LIST) {
                // Call the actual EventScreen composable
                EventScreen(navController = navController) // <<<--- FIXED
            }
            composable(AppDestinations.COMPLAINT_FORM) {
                ComplaintScreen() // Was already correct
            }
            composable(AppDestinations.INFO_QR) {
                InfoQrScreen(navController = navController) // Was already correct
            }
            composable(AppDestinations.LOST_FOUND_LIST) {
                LostFoundScreen(navController = navController) // Was already correct
            }

            // Add composable blocks for other destinations as needed
        }
    }
}

data class EssentialItem(
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RwaScreenUI(navController: NavController) {

    val context = LocalContext.current
    // Essential items list remains the same
    val essentialItems = listOf(
        EssentialItem(Icons.Outlined.Description, "Notice"),
        EssentialItem(Icons.Outlined.EditNote, "Complaint"),
        EssentialItem(Icons.Outlined.Notifications, "Personal Notice"),
        EssentialItem(Icons.Outlined.LocalShipping, "Delivery"),
        EssentialItem(Icons.AutoMirrored.Outlined.ReceiptLong, "Bills"),
        EssentialItem(Icons.Outlined.Build, "Maintenance"), // Target for Toast
        EssentialItem(Icons.Outlined.StarOutline, "Events"),
        EssentialItem(Icons.Outlined.QrCode,"Info QR"),
        EssentialItem(Icons.Outlined.ShoppingBag, "Lost & Found") // <<<--- THIS ITEM
    )

    Scaffold(
        // TopAppBar remains the same
        topBar = {
            TopAppBar(
                title = { Text("SocietyQ") },
                navigationIcon = {
                    IconButton(onClick = {/*TODO*/}){
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = "App Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick =  {/*TODO Handle search Click*/}){
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = {
                        UserPreferences.logout(context)
                        navController.navigate(AppDestinations.LOGIN) {
                            popUpTo(AppDestinations.DASHBOARD) { inclusive = true }
                            launchSingleTop = true
                        }
                    }){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // ImportantCard, Filter Chips, Title remain the same
            Spacer(modifier = Modifier.height(16.dp))
            ImportantCard()
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {showUnderConstructionToast(context) },
                    label = { Text("Show all") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.List,
                            contentDescription = "Show all filter"
                        )
                    }
                )
                Spacer(Modifier.weight(1f))
                AssistChip(
                    onClick = { showUnderConstructionToast(context) },
                    label = { Text("History") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "History filter"
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Daily Essentials",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))


            // --- Essentials Grid ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(essentialItems) { item ->
                    EssentialGridItem(
                        item = item,
                        onClick = {
                            // --- Updated Navigation Logic ---
                            when (item.label) {
                                "Notice" -> navController.navigate(AppDestinations.NOTICE_LIST)
                                "Complaint" -> navController.navigate(AppDestinations.COMPLAINT_FORM)
                                "Personal Notice" -> navController.navigate(AppDestinations.PERSONAL_NOTICE_LIST)
                                "Delivery" -> navController.navigate(AppDestinations.DELIVERY_LIST)
                                "Bills" -> navController.navigate(AppDestinations.BILL_LIST)
                                "Events" -> navController.navigate(AppDestinations.EVENT_LIST)
                                "Info QR" -> navController.navigate(AppDestinations.INFO_QR)

                                // --- UPDATE Navigation for Lost & Found ---
                                "Lost & Found" -> navController.navigate(AppDestinations.LOST_FOUND_LIST) // <<<--- NAVIGATE HERE

                                // --- Use Toast for unimplemented items ---
                                "Maintenance" -> showUnderConstructionToast(context)
                                // Remove Lost & Found from toast items

                                else -> {
                                    // Fallback for any other unexpected items
                                    println("Navigation not implemented for: ${item.label}")
                                    showUnderConstructionToast(context)
                                }
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


// --- ImportantCard, EssentialGridItem remain the same ---
@Composable
fun ImportantCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Adjust corner radius as needed
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Light purple-ish
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle with 'A'
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer), // Adjust color
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A", // TODO: Make this dynamic if needed
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer // Adjust color
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Column
            Column(modifier = Modifier.weight(1f)) { // Takes available space
                Text(
                    text = "Important",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Parcel with Guard", // TODO: Make dynamic
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.7f) // Slightly muted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Placeholder Shapes - Replace with actual icons or drawables if available
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    imageVector = Icons.Filled.ChangeHistory, // Placeholder Triangle
                    contentDescription = null, // Decorative
                    modifier = Modifier.size(20.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
                Icon(
                    imageVector = Icons.Filled.Circle, // Placeholder Circle
                    contentDescription = null, // Decorative
                    modifier = Modifier.size(20.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
                Icon(
                    imageVector = Icons.Filled.Square, // Placeholder Square
                    contentDescription = null, // Decorative
                    modifier = Modifier.size(20.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun EssentialGridItem(
    item: EssentialItem,
    onClick: () -> Unit, // Add onClick parameter
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)) // Clip for ripple effect
            .clickable(onClick = onClick) // Make the whole item clickable
            .padding(vertical = 8.dp), // Add padding inside the clickable area
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label, // Important for accessibility
            modifier = Modifier.size(36.dp), // Adjust size as needed
            tint = MaterialTheme.colorScheme.primary // Or desired icon color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall, // Small label text
            fontSize = 11.sp, // Fine-tune font size
            maxLines = 1 // Ensure text doesn't wrap excessively
        )
    }
}


// --- AppDestinations object ---
// (Ensure it includes LOGIN)
object AppDestinations {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val NOTICE_LIST = "notice_list"
    const val PERSONAL_NOTICE_LIST = "personal_notice_list"
    const val DELIVERY_LIST = "delivery_list"
    const val BILL_LIST = "bill_list"
    const val EVENT_LIST = "event_list"
    const val COMPLAINT_FORM = "complaint_form"
    const val INFO_QR = "info_qr"
    const val LOST_FOUND_LIST = "lost_found_list" // <<<--- ADD THIS ROUTE
    // Add routes for other items if they get screens
    // const val MAINTENANCE_LIST = "maintenance_list"
}


/**
 * Displays a short "Under construction" Toast message.
 *
 * @param context The application or activity context.
 */
fun showUnderConstructionToast(context: Context) {
    Toast.makeText(context, "Under construction", Toast.LENGTH_SHORT).show()
}
