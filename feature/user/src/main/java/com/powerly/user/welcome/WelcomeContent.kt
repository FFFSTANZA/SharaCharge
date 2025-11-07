package com.SharaSpot.user.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.SharaSpot.resources.R
import com.SharaSpot.ui.dialogs.signIn.SignInButton
import com.SharaSpot.ui.dialogs.signIn.SignInOptions
import com.SharaSpot.ui.extensions.onClick
import com.SharaSpot.ui.theme.AppTheme
import com.SharaSpot.ui.theme.Spacing

private const val TAG = "WelcomeScreen"

@Preview
@Composable
private fun WelcomeScreenPreview() {
    AppTheme {
        WelcomeScreenContent(
            appVersion = "Version: debug 0.1",
            selectedLanguage = "Arabic",
            onShowLanguageDialog = {},
            onShowOptionsDialog = {},
            onOpenUserAgreement = {},
            signInEvents = {}
        )
    }
}


@Composable
internal fun WelcomeScreenContent(
    appVersion: String,
    selectedLanguage: String,
    onShowOptionsDialog: () -> Unit,
    onShowLanguageDialog: () -> Unit,
    onOpenUserAgreement: (Int) -> Unit,
    signInEvents: (SignInOptions) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.m)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "SharaSpot Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.xl)
                .size(120.dp)
        )
        Text(
            text = stringResource(id = R.string.welcome),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(id = R.string.welcome_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        SectionLanguage(
            selectedLanguage = selectedLanguage,
            onSelectLanguage = onShowLanguageDialog
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.m),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                SignInButton(
                    title = R.string.login_option_email,
                    icon = R.drawable.sign_in_email,
                    color = MaterialTheme.colorScheme.onBackground,
                    iconTint = MaterialTheme.colorScheme.onBackground,
                    background = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    onClick = { signInEvents(SignInOptions.Email) }
                )
                SignInButton(
                    title = R.string.login_option_other,
                    background = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onShowOptionsDialog
                )
            }
        }
        SectionTermsAndConditions(
            openPrivacyPolicy = {
                onOpenUserAgreement(1)
            },
            openTermsOfService = {
                onOpenUserAgreement(2)
            },
        )
        Text(
            text = appVersion,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionLanguage(
    selectedLanguage: String,
    onSelectLanguage: () -> Unit
) {
    Text(
        text = stringResource(id = R.string.select_app_language),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    )

    Surface(
        onClick = onSelectLanguage,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedLanguage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "Select language",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectionTermsAndConditions(
    openPrivacyPolicy: () -> Unit,
    openTermsOfService: () -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = Spacing.xs,
            alignment = Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(
                R.string.welcome_user_agreement,
                stringResource(id = R.string.app_name)
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = stringResource(R.string.welcome_privacy_policy),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.onClick(openPrivacyPolicy)
        )
        Text(
            text = stringResource(R.string.welcome_and),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = stringResource(R.string.welcome_terms_of_service),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.onClick(openTermsOfService)
        )
    }
}