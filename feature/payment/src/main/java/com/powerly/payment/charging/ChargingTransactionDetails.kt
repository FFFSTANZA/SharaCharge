package com.SharaSpot.payment.charging

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.powerly.core.model.payment.ChargingTransaction
import com.SharaSpot.ui.containers.MyColumn
import com.SharaSpot.ui.containers.MyRow
import com.SharaSpot.ui.containers.MySurfaceColumn
import com.SharaSpot.ui.theme.SharaSpotColors

/**
 * Composable to display charging transaction pricing breakdown
 *
 * Shows transparent pricing without GST:
 * - Energy Consumed
 * - Rate per kWh
 * - Charging Cost
 * - Platform Fee (if applicable)
 * - Total Amount
 */
@Composable
fun ChargingTransactionDetails(
    transaction: ChargingTransaction,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true
) {
    MySurfaceColumn(
        modifier = modifier.padding(16.dp),
        spacing = 12.dp
    ) {
        if (showHeader) {
            Text(
                text = "Payment Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
            HorizontalDivider(color = SharaSpotColors.Outline)
        }

        // Energy Consumed
        TransactionDetailRow(
            label = "Energy Consumed",
            value = transaction.getFormattedEnergy()
        )

        // Rate per kWh
        TransactionDetailRow(
            label = "Rate",
            value = transaction.getFormattedRate()
        )

        HorizontalDivider(color = SharaSpotColors.Outline)

        // Charging Cost
        TransactionDetailRow(
            label = "Charging Cost",
            value = transaction.getFormattedChargingCost(),
            valueColor = MaterialTheme.colorScheme.secondary
        )

        // Platform Fee (only show if > 0)
        if (transaction.hasPlatformFee()) {
            TransactionDetailRow(
                label = "Platform Fee",
                value = transaction.getFormattedPlatformFee(),
                valueColor = MaterialTheme.colorScheme.secondary
            )
        }

        HorizontalDivider(color = SharaSpotColors.Outline)

        // Total Amount
        TransactionDetailRow(
            label = "Total Paid",
            value = transaction.getFormattedTotal(),
            labelWeight = FontWeight.SemiBold,
            valueWeight = FontWeight.SemiBold,
            valueColor = MaterialTheme.colorScheme.primary
        )

        // Disclaimer
        Text(
            text = "All prices are inclusive. No hidden fees.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * Simple row showing a label and value
 */
@Composable
private fun TransactionDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelWeight: FontWeight = FontWeight.Normal,
    valueWeight: FontWeight = FontWeight.Normal,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary
) {
    MyRow(
        modifier = modifier.fillMaxWidth(),
        spacing = 8.dp
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = labelWeight,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            fontWeight = valueWeight
        )
    }
}

/**
 * Compact version of transaction details (for lists)
 */
@Composable
fun ChargingTransactionSummary(
    transaction: ChargingTransaction,
    modifier: Modifier = Modifier
) {
    MyColumn(
        modifier = modifier,
        spacing = 4.dp
    ) {
        MyRow(spacing = 8.dp) {
            Text(
                text = transaction.getFormattedEnergy(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = transaction.getFormattedRate(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Text(
            text = transaction.getFormattedTotal(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
