package com.SharaSpot.powerSource.contribution

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SharaSpot.core.model.contribution.ChargerStatus
import com.SharaSpot.ui.dialogs.MyBasicBottomSheet

/**
 * Screen for updating charger status
 */
@Composable
fun StatusUpdateContributionScreen(
    chargerId: String,
    viewModel: ContributionViewModel,
    onDismiss: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf<ChargerStatus?>(null) }

    MyBasicBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "✅ Update Status",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Current charger status",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Status options
            ChargerStatus.values().filter { it != ChargerStatus.UNKNOWN }.forEach { status ->
                val statusIcon = when (status) {
                    ChargerStatus.AVAILABLE -> "✅"
                    ChargerStatus.BUSY -> "🔴"
                    ChargerStatus.NOT_WORKING -> "❌"
                    ChargerStatus.MAINTENANCE -> "🔧"
                    else -> ""
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedStatus = status },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedStatus == status) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = statusIcon,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = status.displayName,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    selectedStatus?.let {
                        viewModel.updateStatus(chargerId, it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedStatus != null
            ) {
                Text("Submit Status")
            }
        }
    }
}
