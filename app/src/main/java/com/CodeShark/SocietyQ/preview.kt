package com.CodeShark.SocietyQ

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.CodeShark.SocietyQ.screens.BaseListScreen
import com.CodeShark.SocietyQ.screens.DeliveryItem
import com.CodeShark.SocietyQ.screens.EventItem
import com.CodeShark.SocietyQ.screens.NoticeItem
import com.CodeShark.SocietyQ.viewModel.DeliveryViewModel
import com.CodeShark.SocietyQ.viewModel.EventViewModel
import com.CodeShark.SocietyQ.viewModel.NoticeViewModel
import com.CodeShark.SocietyQ.viewModel.PersonalNoticeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun NoticeScreen(
    viewModel: NoticeViewModel = viewModel(),
    navController: NavController = rememberNavController() // Default for preview
) {
    BaseListScreen(
        viewModel = viewModel,
        screenTitle = "Notices",
        itemContent = { notice, onItemClick, onAction ->
            NoticeItem(notice, onItemClick, onAction)
        },
        navController = navController
    )
}

@Composable
fun DeliveryScreen(
    viewModel: DeliveryViewModel = viewModel(),
    navController: NavController = rememberNavController() // Default for preview
) {
    BaseListScreen(
        viewModel = viewModel,
        screenTitle = "Deliveries",
        itemContent = { delivery, onItemClick, onAction ->
            DeliveryItem(delivery, onItemClick, onAction)
        },
        navController = navController
    )
}

@Composable
fun EventScreen(
    viewModel: EventViewModel = viewModel(),
    navController: NavController = rememberNavController() // Default for preview
) {
    BaseListScreen(
        viewModel = viewModel,
        screenTitle = "Events",
        itemContent = { event, onItemClick, onAction ->
            EventItem(event, onItemClick, onAction)
        },
        navController = navController
    )
}

// --- Date Formatting (Helper) ---
fun formatDate(date: LocalDate): String {
    // You can customize this format as needed
    return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}
/**
 * Composable function entry point for the Personal Notices screen,
 * primarily used for structuring previews or direct composition if needed elsewhere.
 * It delegates the main UI structure to BaseListScreen.
 *
 * @param viewModel The PersonalNoticeViewModel instance.
 * @param navController The NavController for navigation.
 */
@Composable
fun PersonalNoticeScreen( // Renamed from the file name to avoid conflict if needed
    viewModel: PersonalNoticeViewModel = viewModel(),
    navController: NavController = rememberNavController() // Default for preview
) {
    BaseListScreen(
        viewModel = viewModel,
        screenTitle = "Personal Notices", // Specific title
        itemContent = { notice, onItemClick, onAction ->
            // Reuse NoticeItem for displaying personal notices
            NoticeItem(notice = notice, onItemClick = onItemClick, onAction = onAction)
        },
        navController = navController
    )
}