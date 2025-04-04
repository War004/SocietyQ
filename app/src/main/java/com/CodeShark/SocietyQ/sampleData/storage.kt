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

// --- NEW: Lost and Found Data Structures ---

/**
 * Enum to represent the status of a Lost and Found item.
 */
enum class LostFoundStatus {
    LOST, FOUND
}

/**
 * Represents a single item reported as lost or found.
 *
 * @param id Unique identifier for the item.
 * @param name The name or title of the item (e.g., "Black Wallet").
 * @param description Optional detailed description.
 * @param imageUrl Optional URL string for the item's image. Use placeholder URLs for sample data.
 * @param status Whether the item is [LOST] or [FOUND].
 * @param dateReported The date the item was reported.
 * @param location A description of where the item was lost or found.
 * @param contactInfo Instructions on who to contact (e.g., "Contact Security Desk").
 */
data class LostFoundItemData(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val status: LostFoundStatus,
    val dateReported: LocalDate,
    val location: String,
    val contactInfo: String = "Contact Security Desk" // Default contact info
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