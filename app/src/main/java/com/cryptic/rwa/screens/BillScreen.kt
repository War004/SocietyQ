package com.cryptic.rwa.screens

import BillViewModel
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle // Paid status
import androidx.compose.material.icons.filled.Download // Download receipt
import androidx.compose.material.icons.filled.Error // Unpaid status
import androidx.compose.material.icons.filled.Payment // Pay bill
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cryptic.rwa.sampleData.BillData
import com.cryptic.rwa.sampleData.ListItemAction
import com.cryptic.rwa.sampleData.formatDate
import com.cryptic.rwa.sampleData.formatMonthYear
import java.time.LocalDate
import java.util.Locale

/**
 * Composable function entry point for the Bills screen.
 * Delegates the main UI structure to BaseListScreen.
 *
 * @param viewModel The BillViewModel instance.
 * @param navController The NavController for navigation.
 */
@Composable
fun BillsScreen(
    viewModel: BillViewModel = viewModel(),
    navController: NavController = rememberNavController() // Default for preview
) {
    BaseListScreen(
        viewModel = viewModel,
        screenTitle = "Bills", // Specific title for this screen
        itemContent = { bill, onItemClick, onAction ->
            // Pass the specific BillItem composable
            BillItem(bill = bill, onItemClick = onItemClick, onAction = onAction)
        },
        navController = navController
    )
}

/**
 * Composable responsible for displaying a single bill item within the list.
 * Uses the BaseListItem for consistent structure.
 *
 * @param bill The [BillData] to display.
 * @param onItemClick Lambda function triggered when the item is clicked.
 * @param onAction Lambda function to handle actions like Pay or Download.
 */
@Composable
fun BillItem(
    bill: BillData,
    onItemClick: (String) -> Unit,
    onAction: (ListItemAction) -> Unit
) {
    val statusIcon = if (bill.isPaid) Icons.Filled.CheckCircle else Icons.Filled.Error
    val statusColor = if (bill.isPaid) Color(0xFF388E3C) /* Green */ else MaterialTheme.colorScheme.error
    val statusDesc = if (bill.isPaid) "Paid" else "Pending"

    BaseListItem(
        id = bill.id,
        onClick = onItemClick,
        isUnread = !bill.isPaid, // Use isUnread visually for pending bills if desired
        heading = "${bill.billType} - ${formatMonthYear(bill.monthYear)}",
        // --- Add Status Icon next to Heading ---
        // Note: BaseListItem doesn't have a dedicated slot here, so we prepend it in the heading text logic
        // or modify BaseListItem. For simplicity, let's adjust the heading slightly or add it visually.
        // A better approach would be to enhance BaseListItem or use a custom layout here.
        // Let's use the isUnread dot for now and add an icon in line 2.

        // Line 2: Amount (Start) and Due Date (End)
        line2StartText = String.format(Locale.getDefault(), "₹%.2f", bill.amount), // Format amount
        line2StartIcon = Icons.Outlined.CurrencyRupee,
        contentDescLine2Start = "Bill amount",
        line2EndText = "Due: ${formatDate(bill.dueDate)}",
        line2EndIcon = Icons.Outlined.DateRange,
        contentDescLine2End = "Due date",
        line2EndTextColor = if (!bill.isPaid && bill.dueDate.isBefore(LocalDate.now())) MaterialTheme.colorScheme.error else null, // Highlight overdue

        // Line 3: Payment Status (using IconAndText for visual cue)
        line3Text = if (bill.isPaid && bill.paymentDate != null) "Paid on: ${formatDate(bill.paymentDate)}" else statusDesc,
        line3Icon = statusIcon, // Show status icon here
        contentDescLine3 = statusDesc,
        line3Alignment = Alignment.Start, // Align status to start
        line3TextStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = statusColor), // Style status text

        // --- Actions Content Slot ---
        actionsContent = {
            if (bill.isPaid) {
                // Show "Download Receipt" button for paid bills
                OutlinedButton(
                    onClick = { onAction(ListItemAction.DownloadReceipt(bill.id)) },
                    modifier = Modifier.heightIn(min = 36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp) // More padding
                ) {
                    Icon(
                        Icons.Filled.Download,
                        contentDescription = null, // Button text is descriptive enough
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Receipt", fontSize = 12.sp)
                }
            } else {
                // Show "Pay Now" button for unpaid bills
                Button(
                    onClick = { onAction(ListItemAction.PayBill(bill.id)) },
                    modifier = Modifier.heightIn(min = 36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp) // More padding
                ) {
                    Icon(
                        Icons.Filled.Payment,
                        contentDescription = null, // Button text is descriptive enough
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Pay Now", fontSize = 12.sp)
                }
            }
        }
    )
}
