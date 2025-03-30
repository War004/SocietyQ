package com.cryptic.rwa.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptic.rwa.sampleData.DeliveryData
import com.cryptic.rwa.sampleData.EventData
import com.cryptic.rwa.sampleData.ListItemAction
import com.cryptic.rwa.sampleData.NoticeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

// --- Base Interface for List ViewModels ---

/**
 * Defines a common contract for ViewModels that manage a list of items of type [T].
 * Ensures consistency in how list data, loading states, and errors are handled and exposed.
 *
 * @param T The type of data items managed by the ViewModel.
 */
interface ListViewModel<T> {
    /**
     * A [StateFlow] emitting the current list of items ([T]).
     * Designed to be observed by the UI for displaying the list.
     */
    val items: StateFlow<List<T>>

    /**
     * A [StateFlow] emitting the current loading state (true if loading, false otherwise).
     * Useful for showing/hiding loading indicators in the UI.
     */
    val isLoading: StateFlow<Boolean>

    /**
     * A [StateFlow] emitting an optional error message string.
     * Emits a non-null string when an error occurs, null otherwise.
     * UI can observe this to display error messages (e.g., in a Snackbar).
     */
    val error: StateFlow<String?>

    /**
     * Initiates the process of loading or refreshing the list items.
     * Implementations should handle asynchronous fetching and update the [items], [isLoading], and [error] states.
     */
    fun loadItems()

    /**
     * Handles a click event on a specific item in the list.
     *
     * @param id The unique identifier of the clicked item.
     */
    fun onItemClick(id: String)

    /**
     * Handles specific actions triggered from within list items (e.g., button clicks).
     * Allows for more complex interactions beyond simple item clicks.
     *
     * @param action The [ListItemAction] representing the user's interaction.
     */
    fun onAction(action: ListItemAction)

    /**
     * Consumes (clears) the current error message.
     * Typically called by the UI after the error has been displayed to the user.
     */
    fun consumeError()
}


// --- Sample Data Instances ---
// Note: Using sample data for demonstration and development purposes.
// In a real application, this data would be fetched from a repository (network/database).

val sampleNotice1 = NoticeData(
    id = "N001",
    heading = "Water Supply Disruption Tomorrow",
    issueDate = LocalDate.now().minusDays(1),
    expiryDate = LocalDate.now().plusDays(1),
    issuer = "Maintenance Dept.",
    isUnread = true
)

val sampleNotice2 = NoticeData(
    id = "N002",
    heading = "Monthly Meeting Minutes",
    issueDate = LocalDate.now().minusWeeks(1),
    expiryDate = null, // Example of a notice without an expiry date
    issuer = "Society Secretary",
    isUnread = false
)

val sampleDelivery1 = DeliveryData(
    id = "D101",
    productName = "Amazon Package",
    receivedDate = LocalDate.now(),
    isUnread = true
)

val sampleDelivery2 = DeliveryData(
    id = "D102",
    productName = "Courier Document",
    receivedDate = LocalDate.now().minusDays(3),
    location = "Collected by Resident", // Example with a specific location/status
    isUnread = false
)

val sampleEvent1 = EventData(
    id = "E201",
    eventName = "Annual General Meeting",
    eventDate = LocalDate.now().plusWeeks(2),
    organizer = "Managing Committee",
    isUnread = true
)

val sampleEvent2 = EventData(
    id = "E202",
    eventName = "Community Cleanup Drive",
    eventDate = LocalDate.now().plusDays(5),
    eventEndDate = LocalDate.now().plusDays(5), // Example of a single-day event
    organizer = "Volunteer Group",
    isUnread = false
)

val samplePersonalNotice1 = NoticeData(
    id = "PN001",
    heading = "Your Maintenance Request Approved",
    issueDate = LocalDate.now().minusDays(2),
    expiryDate = null,
    issuer = "Maintenance Team",
    isUnread = true
)

val samplePersonalNotice2 = NoticeData(
    id = "PN002",
    heading = "Monthly Fees are pending",
    issueDate = LocalDate.now().minusDays(1),
    expiryDate = LocalDate.now().plusDays(3), // Expiry indicates deadline
    issuer = "Accounts",
    isUnread = false
)


// --- Notice ViewModel ---

/**
 * ViewModel responsible for managing and providing data related to general notices.
 * Implements the [ListViewModel] interface for [NoticeData].
 */
class NoticeViewModel : ViewModel(), ListViewModel<NoticeData> {

    // --- State Flows ---

    // Internal mutable state flow for the list of notices.
    private val _items = MutableStateFlow<List<NoticeData>>(emptyList())
    // Public immutable state flow exposing the list of notices. UI observes this.
    override val items: StateFlow<List<NoticeData>> = _items.asStateFlow()

    // Internal mutable state flow for the loading state.
    private val _isLoading = MutableStateFlow(false)
    // Public immutable state flow exposing the loading state.
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Internal mutable state flow for error messages.
    private val _error = MutableStateFlow<String?>(null)
    // Public immutable state flow exposing potential error messages.
    override val error: StateFlow<String?> = _error.asStateFlow()

    // --- Initialization ---

    init {
        // Load notices when the ViewModel is first created.
        loadItems()
    }

    // --- Core Logic Methods ---

    /**
     * Loads the list of general notices.
     * This implementation simulates a network delay and uses sample data.
     */
    override fun loadItems() {
        // Launch a coroutine within the ViewModel's scope.
        viewModelScope.launch {
            _isLoading.value = true // Indicate loading started.
            _error.value = null     // Clear any previous errors.
            try {
                // Simulate network request delay.
                delay(1500)
                // Update the items state with sample data.
                // TODO: Replace this with actual data fetching from a repository.
                _items.value = listOf(sampleNotice1, sampleNotice2)
            } catch (e: Exception) {
                // If an error occurs during loading, update the error state.
                _error.value = "Failed to load notices: ${e.message}"
                println("Error loading notices: ${e.message}") // Log the error as well
            } finally {
                // Ensure the loading state is set to false regardless of success or error.
                _isLoading.value = false
            }
        }
    }

    /**
     * Handles click events on individual notice items.
     * It logs the click and triggers the ViewDetails action.
     *
     * @param id The ID of the clicked notice.
     */
    override fun onItemClick(id: String) {
        println("Notice Clicked: $id")
        // Delegate the click to the onAction method with a specific ViewDetails action.
        onAction(ListItemAction.ViewDetails(id, "Notice"))
    }

    /**
     * Handles specific actions related to notice list items.
     * Currently handles the ViewDetails action.
     *
     * @param action The [ListItemAction] performed.
     */
    override fun onAction(action: ListItemAction) {
        viewModelScope.launch {
            // Process the received action.
            when (action) {
                is ListItemAction.ViewDetails -> {
                    // Handle the action to view details of a specific notice.
                    println("Viewing Details for ${action.itemType} ID: ${action.itemId}")
                    // TODO: Implement navigation logic to the notice detail screen.
                }
                // Handle other potential actions specific to notices if added later.
                else -> println("Action $action received for Notice item.") // Generic log for unhandled actions
            }
        }
    }

    /**
     * Clears the current error message state.
     * Called by the UI after displaying the error.
     */
    override fun consumeError() {
        _error.value = null
    }
}

// --- Delivery ViewModel ---

/**
 * ViewModel responsible for managing and providing data related to deliveries.
 * Implements the [ListViewModel] interface for [DeliveryData].
 */
class DeliveryViewModel : ViewModel(), ListViewModel<DeliveryData> {

    // --- State Flows ---
    private val _items = MutableStateFlow<List<DeliveryData>>(emptyList())
    override val items: StateFlow<List<DeliveryData>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // --- Initialization ---
    init {
        // Load deliveries when the ViewModel is first created.
        loadItems()
    }

    // --- Core Logic Methods ---

    /**
     * Loads the list of deliveries.
     * This implementation simulates a network delay and uses sample data.
     */
    override fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                delay(1000) // Simulate network delay (shorter than notices)
                // TODO: Replace with actual data fetching
                _items.value = listOf(sampleDelivery1, sampleDelivery2)
            } catch (e: Exception) {
                _error.value = "Failed to load deliveries: ${e.message}"
                println("Error loading deliveries: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Handles click events on individual delivery items.
     * Logs the click and triggers the ViewDetails action.
     *
     * @param id The ID of the clicked delivery.
     */
    override fun onItemClick(id: String) {
        println("Delivery Clicked: $id")
        onAction(ListItemAction.ViewDetails(id, "Delivery"))
    }

    /**
     * Handles specific actions related to delivery list items.
     * Handles ViewDetails and Track actions.
     *
     * @param action The [ListItemAction] performed.
     */
    override fun onAction(action: ListItemAction) {
        viewModelScope.launch {
            when (action) {
                is ListItemAction.Track -> {
                    // Handle the action to track a specific delivery.
                    println("Tracking Delivery ID: ${action.deliveryId}, URL: ${action.trackingUrl}")
                    // TODO: Implement logic to open the tracking URL (e.g., using an Intent).
                }
                is ListItemAction.ViewDetails -> {
                    // Handle the action to view details of a specific delivery.
                    println("Viewing Details for ${action.itemType} ID: ${action.itemId}")
                    // TODO: Implement navigation logic to the delivery detail screen.
                }
                // Handle other potential actions specific to deliveries if added later.
                else -> println("Action $action received for Delivery item.")
            }
        }
    }

    /**
     * Clears the current error message state.
     */
    override fun consumeError() {
        _error.value = null
    }
}


// --- Personal Notice ViewModel ---

/**
 * ViewModel responsible for managing and providing data related to *personal* notices (specific to the user).
 * Implements the [ListViewModel] interface, reusing [NoticeData] as the item type.
 */
class PersonalNoticeViewModel : ViewModel(), ListViewModel<NoticeData> {

    // --- State Flows ---
    private val _items = MutableStateFlow<List<NoticeData>>(emptyList())
    override val items: StateFlow<List<NoticeData>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // --- Initialization ---
    init {
        // Load personal notices when the ViewModel is first created.
        loadItems()
    }

    // --- Core Logic Methods ---

    /**
     * Loads the list of personal notices.
     * This implementation simulates a network delay and uses sample data specific to personal notices.
     */
    override fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                delay(1200) // Simulate network delay
                // In a real app, fetch *personal* notices from the repository here.
                _items.value = listOf(samplePersonalNotice1, samplePersonalNotice2)
            } catch (e: Exception) {
                _error.value = "Failed to load personal notices: ${e.message}"
                println("Error loading personal notices: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Handles click events on individual personal notice items.
     * Logs the click and triggers the ViewDetails action, specifying "Personal Notice".
     *
     * @param id The ID of the clicked personal notice.
     */
    override fun onItemClick(id: String) {
        println("Personal Notice Clicked: $id")
        // Trigger a generic ViewDetails action, clearly identifying the type.
        onAction(ListItemAction.ViewDetails(id, "Personal Notice"))
    }

    /**
     * Handles specific actions related to personal notice list items.
     * Currently primarily handles the ViewDetails action.
     *
     * @param action The [ListItemAction] performed.
     */
    override fun onAction(action: ListItemAction) {
        viewModelScope.launch {
            when (action) {
                is ListItemAction.ViewDetails -> {
                    // Handle viewing details for a personal notice.
                    println("Viewing Details for ${action.itemType} ID: ${action.itemId}")
                    // TODO: Implement navigation to a detail screen specifically for personal notices (or a generic one).
                }
                // Add other specific actions for personal notices if needed (e.g., Mark as Read, Archive).
                else -> println("Action $action received for Personal Notice item.")
            }
        }
    }

    /**
     * Clears the current error message state.
     * Called after the error has been shown to the user.
     */
    override fun consumeError() {
        _error.value = null
    }
}

// --- Event ViewModel ---

/**
 * ViewModel responsible for managing and providing data related to events.
 * Implements the [ListViewModel] interface for [EventData].
 */
class EventViewModel : ViewModel(), ListViewModel<EventData> {

    // --- State Flows ---
    private val _items = MutableStateFlow<List<EventData>>(emptyList())
    override val items: StateFlow<List<EventData>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // --- Initialization ---
    init {
        // Load events when the ViewModel is first created.
        loadItems()
    }

    // --- Core Logic Methods ---

    /**
     * Loads the list of events.
     * This implementation simulates a network delay and uses sample data.
     */
    override fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                delay(1200) // Simulate network delay
                // TODO: Replace with actual data fetching
                _items.value = listOf(sampleEvent1, sampleEvent2)
            } catch (e: Exception) {
                _error.value = "Failed to load events: ${e.message}"
                println("Error loading events: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Handles click events on individual event items.
     * Logs the click and triggers the ViewDetails action.
     *
     * @param id The ID of the clicked event.
     */
    override fun onItemClick(id: String) {
        println("Event Clicked: $id")
        onAction(ListItemAction.ViewDetails(id, "Event"))
    }

    /**
     * Handles specific actions related to event list items.
     * Handles ViewDetails and Rsvp actions.
     *
     * @param action The [ListItemAction] performed.
     */
    override fun onAction(action: ListItemAction) {
        viewModelScope.launch {
            when (action) {
                is ListItemAction.Rsvp -> {
                    // Handle the action to RSVP for a specific event.
                    println("RSVPing for Event ID: ${action.eventId}, Attending: ${action.attending}")
                    // TODO: Implement API call to update the RSVP status on the backend.
                    // TODO: Consider updating the local item state or reloading the list to reflect the change.
                }
                is ListItemAction.ViewDetails -> {
                    // Handle the action to view details of a specific event.
                    println("Viewing Details for ${action.itemType} ID: ${action.itemId}")
                    // TODO: Implement navigation logic to the event detail screen.
                }
                // Handle other potential actions specific to events if added later.
                else -> println("Action $action received for Event item.")
            }
        }
    }

    /**
     * Clears the current error message state.
     */
    override fun consumeError() {
        _error.value = null
    }
}