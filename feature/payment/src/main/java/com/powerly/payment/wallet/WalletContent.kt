package com.SharaSpot.payment.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.payment.PaymentCard
import com.SharaSpot.resources.R
import com.SharaSpot.ui.containers.MyColumn
import com.SharaSpot.ui.containers.MyRow
import com.SharaSpot.ui.screen.MyScreen
import com.SharaSpot.ui.containers.MySurface
import com.SharaSpot.ui.screen.ScreenHeader
import com.SharaSpot.ui.extensions.asPadding
import com.SharaSpot.ui.components.ButtonLarge
import com.SharaSpot.ui.components.MyIconButton
import com.SharaSpot.ui.dialogs.loading.ScreenState
import com.SharaSpot.ui.dialogs.loading.rememberScreenState
import com.SharaSpot.ui.extensions.onClick
import com.SharaSpot.ui.theme.AppTheme
import com.SharaSpot.ui.theme.SharaSpotColors

@Preview
@Composable
private fun WalletScreenPreview() {
    val card = PaymentCard(
        id = "pm_1Pw4vh07uReUg76VRgy6Oi2K",
        cardNumber = "4242",
        paymentOption = "visa",
        default = true
    )
    AppTheme {
        WalletScreenContent(
            cards = { listOf(card) },
            onAddCard = {},
            onClose = {},
            onSelectCard = {}
        )
    }
}

@Composable
internal fun WalletScreenContent(
    screenState: ScreenState= rememberScreenState(),
    cards: () -> List<PaymentCard>,
    onAddCard: () -> Unit,
    onClose: () -> Unit,
    onSelectCard: (PaymentCard) -> Unit
) {
    MyScreen(
        screenState = screenState,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        background = Color.White,
        header = {
            ScreenHeader(
                title = stringResource(R.string.wallet),
                onClose = onClose
            )
        },
        footer = {
            ButtonLarge(
                text = stringResource(R.string.payment_add_new_card),
                color = Color.White,
                background = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddCard
            )
        },
        footerPadding = 16.dp.asPadding
    ) {
        cards().forEach { card ->
            ItemCard(
                card = card,
                onClick = { onSelectCard(card) }
            )
        }
    }
}

@Composable
private fun ItemCard(
    card: PaymentCard,
    onClick: () -> Unit
) {
    MyColumn(
        horizontalAlignment = Alignment.Start,
        spacing = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .onClick(onClick)
    ) {
        if (card.default) {
            MySurface(
                color = MaterialTheme.colorScheme.tertiary,
                cornerRadius = 4.dp,
                surfaceModifier = Modifier.height(24.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.payment_default),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
        MyRow {
            MySurface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_card),
                    contentDescription = "",
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
            MyColumn(
                spacing = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = card.paymentOption,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = card.cardNumberHidden,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            MyIconButton(
                icon = R.drawable.ic_three_dot_horizontal,
                onClick = onClick
            )
        }
        HorizontalDivider(color = SharaSpotColors.Outline)
    }
}