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
import com.powerly.core.model.contribution.PlugType
import com.SharaSpot.ui.dialogs.MyBasicBottomSheet

/**
 * Screen for verifying plug compatibility
 */
@Composable
fun PlugCheckContributionScreen(
    chargerId: String,
    viewModel: ContributionViewModel,
    onDismiss: () -> Unit
) {
    var selectedPlugType by remember { mutableStateOf<PlugType?>(null) }
    var isWorking by remember { mutableStateOf(true) }
    var powerOutput by remember { mutableStateOf("") }
    var vehicleTested by remember { mutableStateOf("") }

    val powerOptions = listOf("3.3kW", "7kW", "22kW", "50kW", "60kW", "120kW", "150kW")

    MyBasicBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🔌 Verify Plug",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Report plug compatibility",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Plug type selection
            Text(
                text = "Plug Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PlugType.values().forEach { plugType ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPlugType = plugType },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPlugType == plugType) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        text = plugType.displayName,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (selectedPlugType != null) {
                Spacer(modifier = Modifier.height(16.dp))

                // Working status
                Text(
                    text = "Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isWorking,
                        onClick = { isWorking = true },
                        label = { Text("Working") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isWorking,
                        onClick = { isWorking = false },
                        label = { Text("Not Working") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Power output
                Text(
                    text = "Power Output (optional)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var expandedPower by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedPower,
                    onExpandedChange = { expandedPower = it }
                ) {
                    OutlinedTextField(
                        value = powerOutput,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        placeholder = { Text("Select power output") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPower) }
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPower,
                        onDismissRequest = { expandedPower = false }
                    ) {
                        powerOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    powerOutput = option
                                    expandedPower = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vehicle tested
                Text(
                    text = "Vehicle Tested (optional)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = vehicleTested,
                    onValueChange = { vehicleTested = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., Tata Nexon EV") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = {
                        selectedPlugType?.let {
                            viewModel.verifyPlug(
                                chargerId = chargerId,
                                plugType = it,
                                isWorking = isWorking,
                                powerOutput = powerOutput.ifBlank { null },
                                vehicleTested = vehicleTested.ifBlank { null }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Verification")
                }
            }
        }
    }
}
