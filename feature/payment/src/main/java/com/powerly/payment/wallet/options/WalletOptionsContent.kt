package com.SharaSpot.payment.wallet.options

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.core.model.util.Item
import com.SharaSpot.ui.screen.ScreenHeader
import com.SharaSpot.ui.dialogs.ItemOptionsMenu
import com.SharaSpot.ui.dialogs.MyBottomSheet
import com.SharaSpot.ui.dialogs.MyDialogState
import com.SharaSpot.ui.dialogs.rememberMyDialogState
import com.SharaSpot.ui.theme.AppTheme

/**
 * please start interactive mode to show bottom sheet preview
 */
@Preview
@Composable
private fun WalletOptionsDialogPreview() {
    val options = listOf(
        Item(id = 0, label = "Make Default", selected = true),
        Item(id = 1, label = "Delete", selected = false),
        Item(id = 2, label = "Update", selected = false),
    )
    AppTheme {
        WalletOptionsDialogContent(
            state = rememberMyDialogState(visible = true),
            title = "Visa",
            options = options,
            onSelect = {}
        )
    }
}


@Composable
internal fun WalletOptionsDialogContent(
    state: MyDialogState,
    title: String,
    options: List<Item>,
    onSelect: (Item) -> Unit
) {
    MyBottomSheet(
        state = state,
        modifier = Modifier.padding(16.dp),
        background = Color.White,
        header = {
            ScreenHeader(
                title = title,
                layoutDirection = LayoutDirection.Rtl,
                onClose = { state.dismiss() }
            )
        }
    ) {
        options.forEach {
            ItemOptionsMenu(
                title = it.label,
                selected = { it.selected },
                onSelect = { onSelect(it) }
            )
        }
    }
}