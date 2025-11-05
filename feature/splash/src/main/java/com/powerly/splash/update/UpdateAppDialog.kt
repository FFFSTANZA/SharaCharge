package com.SharaSpot.splash.update

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import org.koin.androidx.compose.koinViewModel
import com.SharaSpot.resources.R
import com.SharaSpot.splash.SplashViewModel
import com.SharaSpot.ui.dialogs.MyScreenBottomSheet
import com.SharaSpot.ui.extensions.safeStartActivity

@Composable
internal fun UpdateAppDialog(
    viewModel: SplashViewModel = koinViewModel()
) {
    val context = LocalContext.current
    // Remember the app version string
    val appVersion = remember {
        String.format(
            "%s %s",
            context.getString(R.string.app_name),
            viewModel.appVersion
        )
    }

    BackHandler { }

    /**
     * Function to open the app link in app store
     */
    fun updateApp() {
        val appLink = viewModel.appLink
        val intent = Intent(Intent.ACTION_VIEW, appLink.toUri())
        context.safeStartActivity(intent)
    }


    MyScreenBottomSheet(
        dismissOnBackPress = false,
        onDismiss = { }
    ) {
        UpdateAppScreenContent(
            appVersion = appVersion,
            onUpdate = ::updateApp
        )
    }
}
