package com.SharaSpot.payment.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel
import com.SharaSpot.core.model.payment.StripCard
import com.SharaSpot.payment.methods.PaymentViewModel
import com.SharaSpot.payment.wallet.options.WalletOptionsDialog
import com.SharaSpot.resources.R
import com.SharaSpot.ui.dialogs.alert.MyAlertDialog
import com.SharaSpot.ui.dialogs.alert.rememberAlertDialogState
import com.SharaSpot.ui.dialogs.loading.rememberScreenState
import com.SharaSpot.ui.dialogs.rememberMyDialogState
import kotlinx.coroutines.launch

private const val TAG = "WalletScreen"

@Composable
internal fun WalletScreen(
    viewModel: PaymentViewModel = koinViewModel(),
    addPaymentMethod: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberScreenState()
    val deleteCardDialog = rememberAlertDialogState()
    var walletOptionsDialog = rememberMyDialogState()
    val paymentMethods = remember { viewModel.paymentMethods }
    var selectedCard by remember { mutableStateOf<StripCard?>(null) }


    fun defaultCard(card: StripCard) {
        coroutineScope.launch {
            screenState.loading = true
            val default = viewModel.setDefaultCard(card)
            screenState.loading = false
            if (default && paymentMethods.isNotEmpty()) screenState.showSuccess()
        }
    }

    fun deleteCard(stripCard: StripCard) {
        coroutineScope.launch {
            screenState.loading = true
            val deleted = viewModel.deleteCard(stripCard.id)
            screenState.loading = false
            if (deleted) screenState.showSuccess()
        }
    }

    LaunchedEffect(Unit) {
        screenState.loading = true
        viewModel.loadPaymentMethods()
        screenState.loading = false
        if (paymentMethods.isEmpty()) addPaymentMethod()
    }

    WalletOptionsDialog(
        card = { selectedCard },
        state = walletOptionsDialog,
        onRemove = { deleteCardDialog.show() },
        onMakeDefault = { defaultCard(selectedCard!!) }
    )

    MyAlertDialog(
        state = deleteCardDialog,
        message = stringResource(R.string.payment_delete_card_message),
        positiveButton = stringResource(R.string.yes),
        negativeButton = stringResource(R.string.no),
        positiveButtonClick = { deleteCard(selectedCard!!) }
    )


    WalletScreenContent(
        screenState = screenState,
        cards = { paymentMethods },
        onAddCard = addPaymentMethod,
        onSelectCard = {
            selectedCard = it
            walletOptionsDialog.show()
        },
        onClose = onBack
    )
}