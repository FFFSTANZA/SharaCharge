package com.SharaSpot.payment.methods.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import org.koin.androidx.compose.koinViewModel
import com.SharaSpot.core.model.payment.PaymentCard
import com.SharaSpot.payment.methods.PaymentViewModel
import com.SharaSpot.ui.dialogs.MyScreenBottomSheet
import kotlinx.coroutines.launch

@Composable
internal fun PaymentMethodsDialog(
    viewModel: PaymentViewModel = koinViewModel(),
    paymentPurpose: PaymentPurpose = PaymentPurpose.SELECT_DEFAULT,
    onAddPaymentMethod: () -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { viewModel.screenState }
    val paymentMethods = remember { viewModel.paymentMethods }
    var selectedCard by remember { mutableStateOf<PaymentCard?>(null) }

    fun saveDefaultMethod(card: PaymentCard) {
        selectedCard = card
        coroutineScope.launch {
            screenState.loading = true
            val default = viewModel.setDefaultCard(card)
            screenState.loading = false
            if (default) onDismiss()
        }
    }

    fun onSelectMethod(method: PaymentCard) {
        if (paymentPurpose == PaymentPurpose.SELECT_DEFAULT) {
            saveDefaultMethod(method)
        } else {
            onSelectMethod(method)
            selectedCard = null
        }
    }


    LaunchedEffect(Unit) {
        screenState.loading = true
        viewModel.loadPaymentMethods()
        screenState.loading = false
    }

    MyScreenBottomSheet(onDismiss = onDismiss) {
        PaymentMethodsScreenContent(
            screenState = screenState,
            methods = { paymentMethods },
            onAdd = onAddPaymentMethod,
            onSelect = ::onSelectMethod,
            onClose = onDismiss
        )
    }
}

enum class PaymentPurpose {
    SELECT_DEFAULT,
    SELECT_CARD,
}
