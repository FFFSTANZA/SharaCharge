package com.SharaSpot.payment.balance.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.powerly.core.model.payment.PaymentCard
import com.SharaSpot.resources.R

@Composable
internal fun paymentMethodDetails(
    card: PaymentCard?,
    shortTitle: Boolean = true
): Pair<String, Int> {
    val title: String
    val icon: Int

    // Check for Razorpay payment types first
    if (card != null) {
        when {
            card.isUpi -> {
                title = "UPI"
                // Note: Using generic cash icon. Add specific UPI icon to resources for branding
                icon = R.drawable.ic_payment_cash_logo
                return Pair(title, icon)
            }
            card.isNetBanking -> {
                title = "Net Banking"
                // Note: Using card icon as placeholder. Add specific net banking icon if needed
                icon = R.drawable.icon_card
                return Pair(title, icon)
            }
            card.isWallet -> {
                title = card.paymentOption // e.g., "Paytm", "Mobikwik"
                // Note: Using balance icon. Add wallet-specific icons for different providers
                icon = R.drawable.ic_payment_balance
                return Pair(title, icon)
            }
        }
    }

    // Legacy payment types
    when (card?.paymentType) {
        PaymentCard.CASH_ON_DELIVERY -> {
            title = stringResource(R.string.payment_cash_on_delivery)
            icon = R.drawable.ic_payment_cash_logo
        }

        PaymentCard.PAYMENT_METHOD_CARD -> {
            title = if (shortTitle) card.paymentOption
            else "${card.paymentOption} ${card.cardNumber}"
            icon = if (card.isMada) R.drawable.mada_card
            else R.drawable.icon_card
        }

        PaymentCard.PAYPAL -> {
            title = stringResource(R.string.payment_paypal)
            icon = R.drawable.paypal
        }

        PaymentCard.PAYMENT_METHOD_BALANCE -> {
            title = stringResource(R.string.balance)
            icon = R.drawable.ic_payment_balance
        }

        else -> {
            title = stringResource(R.string.order_payment_method)
            icon = R.drawable.ic_payment_cash_logo
        }
    }
    return Pair(title, icon)
}