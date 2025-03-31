import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CodeShark.SocietyQ.sampleData.BillData
import com.CodeShark.SocietyQ.sampleData.ListItemAction
import com.CodeShark.SocietyQ.viewModel.ListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.UUID

/**
 * ViewModel responsible for managing and providing data related to bills.
 * Implements the [ListViewModel] interface for [BillData].
 */
class BillViewModel : ViewModel(), ListViewModel<BillData> {

    // --- State Flows ---
    private val _items = MutableStateFlow<List<BillData>>(emptyList())
    override val items: StateFlow<List<BillData>> = _items.asStateFlow()

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
     * Loads the list of bills.
     * This implementation simulates a network delay and generates sample data.
     */
    override fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                delay(1300) // Simulate network delay
                // Generate sample bills
                _items.value = generateSampleBills()
            } catch (e: Exception) {
                _error.value = "Failed to load bills: ${e.message}"
                println("Error loading bills: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Handles click events on individual bill items.
     * Logs the click and triggers the ViewDetails action.
     *
     * @param id The ID of the clicked bill.
     */
    override fun onItemClick(id: String) {
        println("Bill Clicked: $id")
        // You might want to navigate to a detailed bill view
        onAction(ListItemAction.ViewDetails(id, "Bill"))
    }

    /**
     * Handles specific actions related to bill list items (Pay, Download Receipt).
     *
     * @param action The [ListItemAction] performed.
     */
    override fun onAction(action: ListItemAction) {
        viewModelScope.launch {
            when (action) {
                is ListItemAction.PayBill -> {
                    println("Attempting to pay bill ID: ${action.billId}")
                    // TODO: Implement payment initiation logic (e.g., navigate to payment gateway)
                    // Simulate payment success/failure and update item state or reload list
                    _error.value = "Payment for bill ${action.billId} initiated (simulation)."
                }
                is ListItemAction.DownloadReceipt -> {
                    println("Attempting to download receipt for bill ID: ${action.billId}")
                    // TODO: Implement receipt download logic (e.g., API call, file generation)
                    _error.value = "Receipt download for bill ${action.billId} requested (simulation)." // Use snackbar for feedback
                }
                is ListItemAction.ViewDetails -> {
                    println("Viewing Details for ${action.itemType} ID: ${action.itemId}")
                    // TODO: Implement navigation to a bill detail screen if needed.
                }
                // Handle other potential actions if added later.
                else -> println("Action $action received for Bill item.")
            }
        }
    }

    /**
     * Clears the current error message state.
     */
    override fun consumeError() {
        _error.value = null
    }

    // --- Sample Data Generation ---
    private fun generateSampleBills(): List<BillData> {
        val bills = mutableListOf<BillData>()
        val currentMonth = YearMonth.now()
        val billAmount = 500.00
        val billType = "Maintenance" // Example bill type

        // Generate 10 past paid bills
        for (i in 1..10) {
            val month = currentMonth.minusMonths(i.toLong())
            bills.add(
                BillData(
                    id = UUID.randomUUID().toString(),
                    billType = billType,
                    monthYear = month,
                    amount = billAmount,
                    dueDate = month.atEndOfMonth().minusDays(5), // Due towards end of month
                    isPaid = true,
                    paymentDate = month.atDay(15) // Paid mid-month
                )
            )
        }

        // Generate 1 current/upcoming unpaid bill
        val upcomingMonth = currentMonth // Or currentMonth.plusMonths(1) if you prefer next month
        bills.add(
            BillData(
                id = UUID.randomUUID().toString(),
                billType = billType,
                monthYear = upcomingMonth,
                amount = billAmount,
                dueDate = upcomingMonth.atEndOfMonth().minusDays(5),
                isPaid = false,
                paymentDate = null
            )
        )

        // Sort bills by month, descending (most recent first)
        return bills.sortedByDescending { it.monthYear }
    }
}
