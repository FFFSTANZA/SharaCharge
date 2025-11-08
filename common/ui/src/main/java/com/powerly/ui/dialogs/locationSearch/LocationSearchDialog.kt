package com.SharaSpot.ui.dialogs.locationSearch

import androidx.compose.runtime.Composable
import com.powerly.core.model.location.Target
import com.SharaSpot.ui.dialogs.MyDialogState
import com.SharaSpot.ui.dialogs.MyScreenBottomSheet

@Composable
fun LocationSearchDialog(
    state: MyDialogState,
    onSelectPlace: (Target) -> Unit
) {
    MyScreenBottomSheet(state = state) {
        LocationSearchScreen(
            onSelectPlace = {
                state.dismiss()
                onSelectPlace(it)
            },
            onClose = { state.dismiss() },
        )
    }
}