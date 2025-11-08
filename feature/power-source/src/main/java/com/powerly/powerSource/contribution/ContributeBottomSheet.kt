package com.SharaSpot.powerSource.contribution

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.contribution.ContributionType
import com.SharaSpot.ui.dialogs.MyBasicBottomSheet
import com.SharaSpot.ui.dialogs.loading.LoadingDialog
import com.SharaSpot.ui.dialogs.loading.rememberLoadingState
import org.koin.androidx.compose.koinViewModel

/**
 * Bottom sheet showing contribution options
 */
@Composable
fun ContributeBottomSheet(
    chargerId: String,
    viewModel: ContributionViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val contributionState by viewModel.contributionState.collectAsState()
    val loadingState = rememberLoadingState()

    var showPhotoContribution by remember { mutableStateOf(false) }
    var showReviewContribution by remember { mutableStateOf(false) }
    var showWaitTimeContribution by remember { mutableStateOf(false) }
    var showPlugCheckContribution by remember { mutableStateOf(false) }
    var showStatusUpdateContribution by remember { mutableStateOf(false) }

    // Handle contribution state
    LaunchedEffect(contributionState) {
        when (val state = contributionState) {
            is ContributionState.Submitting,
            is ContributionState.Uploading -> {
                loadingState.show = true
            }
            is ContributionState.Success -> {
                loadingState.show = false
                Toast.makeText(
                    context,
                    "+${state.evCoinsEarned} EVCoins earned! 🎉",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetState()
                onDismiss()
            }
            is ContributionState.Error -> {
                loadingState.show = false
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {
                loadingState.show = false
            }
        }
    }

    MyBasicBottomSheet(onDismiss = onDismiss) {
        LoadingDialog(state = loadingState)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Contribute",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Help the community by sharing updates",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Contribution options
            ContributionType.values().forEach { type ->
                ContributionOptionItem(
                    icon = type.icon,
                    title = type.displayName,
                    evCoins = type.evCoinsReward,
                    onClick = {
                        when (type) {
                            ContributionType.PHOTO -> showPhotoContribution = true
                            ContributionType.REVIEW -> showReviewContribution = true
                            ContributionType.WAIT_TIME -> showWaitTimeContribution = true
                            ContributionType.PLUG_CHECK -> showPlugCheckContribution = true
                            ContributionType.STATUS_UPDATE -> showStatusUpdateContribution = true
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Sub-screens
    if (showPhotoContribution) {
        PhotoContributionScreen(
            chargerId = chargerId,
            viewModel = viewModel,
            onDismiss = { showPhotoContribution = false }
        )
    }

    if (showReviewContribution) {
        ReviewContributionScreen(
            chargerId = chargerId,
            viewModel = viewModel,
            onDismiss = { showReviewContribution = false }
        )
    }

    if (showWaitTimeContribution) {
        WaitTimeContributionScreen(
            chargerId = chargerId,
            viewModel = viewModel,
            onDismiss = { showWaitTimeContribution = false }
        )
    }

    if (showPlugCheckContribution) {
        PlugCheckContributionScreen(
            chargerId = chargerId,
            viewModel = viewModel,
            onDismiss = { showPlugCheckContribution = false }
        )
    }

    if (showStatusUpdateContribution) {
        StatusUpdateContributionScreen(
            chargerId = chargerId,
            viewModel = viewModel,
            onDismiss = { showStatusUpdateContribution = false }
        )
    }
}

@Composable
private fun ContributionOptionItem(
    icon: String,
    title: String,
    evCoins: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "+$evCoins EVCoins",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
