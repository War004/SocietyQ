package com.CodeShark.SocietyQ.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CodeShark.SocietyQ.sampleData.ListItemAction
import com.CodeShark.SocietyQ.sampleData.LostFoundItemData
import com.CodeShark.SocietyQ.sampleData.LostFoundStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

/**
 * ViewModel responsible for managing and providing data related to Lost and Found items.
 * Implements the [ListViewModel] interface for [LostFoundItemData].
 */
class LostFoundViewModel : ViewModel(), ListViewModel<LostFoundItemData> {

    // --- State Flows ---
    private val _items = MutableStateFlow<List<LostFoundItemData>>(emptyList())
    override val items: StateFlow<List<LostFoundItemData>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // --- Initialization ---
    init {
        loadItems()
    }

    // --- Core Logic Methods ---

    /**
     * Loads the list of lost and found items.
     *
     * IMPORTANT: This implementation uses hardcoded sample data for demonstration.
     * In a real application, this data would be fetched from a repository
     * connected to a backend API.
     */
    override fun loadItems() {
        viewModelScope.launch {
            if (_items.value.isEmpty()) { // Only show initial loading indicator
                _isLoading.value = true
            }
            _error.value = null
            try {
                // Simulate network delay
                delay(1500)

                // *** PROTOTYPE ONLY: Using Sample Data ***
                // TODO: Replace with actual data fetching from an API
                _items.value = createSampleLostFoundData()

            } catch (e: Exception) {
                _error.value = "Failed to load lost & found items: ${e.message}"
                println("Error loading lost & found items: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Handles click events on individual lost/found items.
     * Currently logs the click. Future implementation could navigate to a detail screen.
     *
     * @param id The ID of the clicked item.
     */
    override fun onItemClick(id: String) {
        println("Lost/Found Item Clicked: $id")
        // TODO: Implement navigation to item detail screen if needed
        onAction(ListItemAction.ViewDetails(id, "LostFoundItem"))
    }

    /**
     * Handles specific actions related to lost/found list items.
     * Currently only handles ViewDetails.
     *
     * @param action The [ListItemAction] performed.
     */
    override fun onAction(action: ListItemAction) {
        when (action) {
            is ListItemAction.ViewDetails -> {
                println("Viewing Details for ${action.itemType} ID: ${action.itemId}")
                // TODO: Implement navigation logic if a detail screen is added.
            }
            // Handle other potential actions specific to lost/found if added later.
            else -> println("Action $action received but not handled for LostFound item.")
        }
    }

    /**
     * Clears the current error message state.
     */
    override fun consumeError() {
        _error.value = null
    }

    // --- Sample Data Generation ---
    private fun createSampleLostFoundData(): List<LostFoundItemData> {
        // Use placeholder images from placehold.co
        // Format: https://placehold.co/{width}x{height}/{background_color}/{text_color}?text={your_text}
        return listOf(
            LostFoundItemData(
                id = UUID.randomUUID().toString(),
                name = "Blue Water Bottle",
                description = "Steel blue water bottle, slightly scratched near the bottom.",
                imageUrl = "https://placehold.co/600x400/E0E0E0/757575?text=Bottle", // Placeholder
                status = LostFoundStatus.FOUND,
                dateReported = LocalDate.now().minusDays(1),
                location = "Clubhouse Gym"
            ),
            LostFoundItemData(
                id = UUID.randomUUID().toString(),
                name = "Set of Keys",
                description = "Bunch of 3 keys on a red keychain.",
                imageUrl = "https://placehold.co/600x400/E0E0E0/757575?text=Keys", // Placeholder
                status = LostFoundStatus.LOST,
                dateReported = LocalDate.now().minusDays(3),
                location = "Near Block C Entrance"
            ),
            LostFoundItemData(
                id = UUID.randomUUID().toString(),
                name = "Black Wallet",
                description = "Leather wallet, contains ID card.",
                imageUrl = "https://placehold.co/600x400/E0E0E0/757575?text=Wallet", // Placeholder
                status = LostFoundStatus.FOUND,
                dateReported = LocalDate.now().minusDays(5),
                location = "Security Desk"
            ),
            LostFoundItemData(
                id = UUID.randomUUID().toString(),
                name = "Child's Toy Car",
                description = "Small red toy car.",
                imageUrl = null, // Example with no image
                status = LostFoundStatus.FOUND,
                dateReported = LocalDate.now().minusWeeks(1),
                location = "Children's Play Area"
            ),
            LostFoundItemData(
                id = UUID.randomUUID().toString(),
                name = "Spectacles",
                description = "Black framed spectacles in a blue case.",
                imageUrl = "https://placehold.co/600x400/E0E0E0/757575?text=Specs", // Placeholder
                status = LostFoundStatus.LOST,
                dateReported = LocalDate.now().minusDays(2),
                location = "Park Bench"
            )
        ).shuffled() // Shuffle for variety on reload
    }
}
