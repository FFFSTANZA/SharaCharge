package com.SharaSpot.user.welcome.language

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import com.SharaSpot.ui.dialogs.languages.LanguagesDialogContent


@Composable
internal fun LanguagesDialog(
    viewModel: LanguagesViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val activity = LocalActivity.current
    LanguagesDialogContent(
        selectedLanguage = viewModel.language,
        onSelect = {
            viewModel.changeAppLanguage(
                lang = it,
                activity = activity!!
            )
        },
        onDismiss = onDismiss
    )
}

