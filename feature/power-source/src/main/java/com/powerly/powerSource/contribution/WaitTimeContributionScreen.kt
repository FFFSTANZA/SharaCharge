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
import com.powerly.core.model.contribution.WaitTimeOption
import com.SharaSpot.ui.dialogs.MyBasicBottomSheet

/**
 * Screen for reporting wait time
 */
@Composable
fun WaitTimeContributionScreen(
    chargerId: String,
    viewModel: ContributionViewModel,
    onDismiss: () -> Unit
) {
    var selectedWaitTime by remember { mutableStateOf<WaitTimeOption?>(null) }
    var queueLength by remember { mutableStateOf("") }

    MyBasicBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "⏱️ Report Wait Time",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Current waiting time at this station",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Wait time options
            Text(
                text = "Estimated wait time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            WaitTimeOption.values().forEach { option ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedWaitTime = option },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedWaitTime == option) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        text = option.displayName,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Queue length (optional)
            Text(
                text = "Vehicles in queue (optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = queueLength,
                onValueChange = { queueLength = it.filter { char -> char.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Number of vehicles waiting") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    selectedWaitTime?.let {
                        viewModel.reportWaitTime(
                            chargerId = chargerId,
                            waitTime = it,
                            queueLength = queueLength.toIntOrNull()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedWaitTime != null
            ) {
                Text("Submit Wait Time")
            }
        }
    }
}
