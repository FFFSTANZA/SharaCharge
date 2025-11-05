package com.SharaSpot.payment.balance.withdraw

import androidx.compose.runtime.Composable
import com.SharaSpot.ui.dialogs.MyScreenBottomSheet

@Composable
internal fun WithdrawBalanceDialog(onDismiss: () -> Unit) {
    MyScreenBottomSheet(onDismiss = onDismiss) {
        WithdrawScreen(onClose = onDismiss)
    }
}