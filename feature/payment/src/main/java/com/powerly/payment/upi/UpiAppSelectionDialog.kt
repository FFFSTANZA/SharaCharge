package com.SharaSpot.payment.upi

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.SharaSpot.payment.UpiApp
import com.SharaSpot.resources.R
import com.SharaSpot.ui.components.ButtonLarge
import com.SharaSpot.ui.containers.MyColumn
import com.SharaSpot.ui.containers.MySurface
import com.SharaSpot.ui.containers.MySurfaceRow

/**
 * UPI App Selection Dialog
 *
 * Shows a dialog with list of installed UPI apps for the user to choose from.
 * This enables direct UPI intent flow for better user experience.
 */
@Composable
fun UpiAppSelectionDialog(
    upiApps: List<UpiApp>,
    onAppSelected: (UpiApp) -> Unit,
    onUseRazorpay: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            MyColumn(
                modifier = Modifier.padding(16.dp),
                spacing = 16.dp
            ) {
                // Title
                Text(
                    text = "Select UPI App",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Subtitle
                Text(
                    text = "Choose your preferred UPI app for payment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // UPI Apps List
                if (upiApps.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(upiApps) { app ->
                            UpiAppItem(
                                app = app,
                                onClick = { onAppSelected(app) }
                            )
                        }
                    }
                } else {
                    // No UPI apps installed
                    Text(
                        text = "No UPI apps found. Please install a UPI app or use Razorpay checkout.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }

                // Divider with "OR"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                // Use Razorpay Checkout button
                ButtonLarge(
                    text = "Use Razorpay Checkout",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    background = MaterialTheme.colorScheme.primary,
                    onClick = onUseRazorpay
                )

                // Cancel button
                ButtonLarge(
                    text = "Cancel",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                    background = Color.Transparent,
                    onClick = onDismiss
                )
            }
        }
    }
}

/**
 * UPI App Item
 *
 * Displays a single UPI app option in the selection dialog.
 */
@Composable
private fun UpiAppItem(
    app: UpiApp,
    onClick: () -> Unit
) {
    MySurfaceRow(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 8.dp,
        spacing = 12.dp
    ) {
        // App icon placeholder (TODO: Add actual app icons)
        MySurface(
            modifier = Modifier.size(40.dp),
            cornerRadius = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Image(
                painter = painterResource(getUpiAppIcon(app.packageName)),
                contentDescription = app.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }

        // App name
        Text(
            text = app.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Arrow icon
        Image(
            painter = painterResource(R.drawable.arrow_right),
            contentDescription = "Select",
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Get UPI app icon based on package name
 *
 * Note: Currently using placeholder icons. To add actual app icons:
 * 1. Download official app icons from respective UPI providers
 * 2. Add them to common/resources/src/main/res/drawable/
 * 3. Update the mappings below with actual resource IDs
 * 4. Ensure icons are properly licensed for use
 */
private fun getUpiAppIcon(packageName: String): Int {
    return when (packageName) {
        "com.phonepe.app" -> R.drawable.ic_payment_cash_logo // Placeholder for PhonePe
        "com.google.android.apps.nbu.paisa.user" -> R.drawable.ic_payment_cash_logo // Placeholder for Google Pay
        "net.one97.paytm" -> R.drawable.ic_payment_balance // Placeholder for Paytm
        "in.org.npci.upiapp" -> R.drawable.ic_payment_cash_logo // Placeholder for BHIM
        "in.amazon.mShop.android.shopping" -> R.drawable.ic_payment_cash_logo // Placeholder for Amazon Pay
        else -> R.drawable.ic_payment_cash_logo
    }
}
