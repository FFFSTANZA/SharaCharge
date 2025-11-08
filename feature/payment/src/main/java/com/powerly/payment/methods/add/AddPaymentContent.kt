package com.SharaSpot.payment.methods.add

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.SharaSpot.resources.R
import com.SharaSpot.ui.components.ButtonLarge
import com.SharaSpot.ui.dialogs.loading.ScreenState
import com.SharaSpot.ui.dialogs.loading.rememberScreenState
import com.SharaSpot.ui.extensions.isPreview
import com.SharaSpot.ui.screen.MyScreen
import com.SharaSpot.ui.screen.ScreenHeader
import com.SharaSpot.ui.theme.AppTheme
import com.SharaSpot.ui.theme.SharaSpotColors
import com.SharaSpot.ui.theme.myBorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "AddPaymentScreen"

@Preview
@Composable
private fun AddPaymentScreenPreview() {
    AppTheme {
        AddPaymentScreenContent(
            cardNumber = flow { emit("") },
            onAddCard = {},
            onScanCard = {},
            onClose = {}
        )
    }
}

@Composable
internal fun AddPaymentScreenContent(
    screenState: ScreenState = rememberScreenState(),
    cardNumber: Flow<String>,
    onAddCard: (Map<String, Any>) -> Unit,
    onScanCard: () -> Unit,
    onClose: () -> Unit
) {
    var cardParms by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    MyScreen(
        screenState = screenState,
        header = {
            ScreenHeader(
                title = stringResource(R.string.payment_card_add),
                onClose = onClose
            )
        },
        spacing = 16.dp,
        modifier = Modifier.padding(16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = myBorder
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 16.dp,
                        horizontal = 16.dp
                    )
            ) {
                Text(
                    text = "Payment card input removed - Stripe integration disabled",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Card payment functionality has been removed. Please integrate a new payment provider.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}