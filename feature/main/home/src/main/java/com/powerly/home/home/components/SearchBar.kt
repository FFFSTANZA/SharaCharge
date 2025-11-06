package com.SharaSpot.home.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.SharaSpot.resources.R
import com.SharaSpot.ui.theme.MyColors

/**
 * Compact search bar for home screen
 * Opens search dialog when clicked
 */
@Composable
fun CompactSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MyColors.grey50,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search",
            modifier = Modifier.size(20.dp),
            tint = MyColors.grey700
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Search chargers, locations...",
            style = MaterialTheme.typography.bodyMedium,
            color = MyColors.grey700
        )

        Spacer(modifier = Modifier.weight(1f))

        // Voice search icon
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_mic_24),
            contentDescription = "Voice Search",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
