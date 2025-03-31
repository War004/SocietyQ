package com.CodeShark.SocietyQ.sampleData

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.YearMonth
// --- Data Classes (Assumed Definitions) ---

data class NoticeData(val id: String, val heading: String, val issueDate: LocalDate, val expiryDate: LocalDate?, val issuer: String, val isUnread: Boolean)
data class DeliveryData(val id: String, val productName: String, val receivedDate: LocalDate, val location: String = "At Security Room", val isUnread: Boolean)
data class EventData(val id: String, val eventName: String, val eventDate: LocalDate, val organizer: String, val isUnread: Boolean, val eventEndDate: LocalDate? = null)

/**
 * Represents a single bill.
 *
 * @param id Unique identifier for the bill.
 * @param billType Type of the bill (e.g., "Maintenance", "Electricity").
 * @param monthYear The month and year the bill is for.
 * @param amount The amount due.
 * @param dueDate The date the payment is due.
 * @param isPaid Flag indicating if the bill has been paid.
 * @param paymentDate Optional date when the bill was paid.
 */
data class BillData(
    val id: String,
    val billType: String,
    val monthYear: YearMonth, // Use YearMonth for clarity
    val amount: Double,
    val dueDate: LocalDate,
    val isPaid: Boolean,
    val paymentDate: LocalDate? = null // Optional: Track when it was paid
)

fun formatDate(date: LocalDate): String {
    return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

// --- Helper function for formatting Month/Year ---
fun formatMonthYear(yearMonth: YearMonth): String {
    // Example format: "March 2025"
    return yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
}



sealed class ListItemAction {
    // Existing Actions
    data class Track(val deliveryId: String, val trackingUrl: String?) : ListItemAction() // Optional URL
    data class Rsvp(val eventId: String, val attending: Boolean) : ListItemAction()
    data class ViewDetails(val itemId: String, val itemType: String) : ListItemAction() // Generic details action

    // --- NEW: Bill Actions ---
    /**
     * Action to initiate payment for a specific bill.
     * @param billId The ID of the bill to pay.
     */
    data class PayBill(val billId: String) : ListItemAction()

    /**
     * Action to download the receipt for a paid bill.
     * @param billId The ID of the bill whose receipt is requested.
     */
    data class DownloadReceipt(val billId: String) : ListItemAction()

    // Add other actions as needed (e.g., ReportIssue for a bill)
}