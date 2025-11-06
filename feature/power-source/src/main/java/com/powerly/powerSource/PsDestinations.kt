package com.SharaSpot.powerSource

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.SharaSpot.charge.ChargingDialog
import com.SharaSpot.charge.ChargingScreen
import com.SharaSpot.lib.AppRoutes
import com.SharaSpot.lib.showMessage
import com.SharaSpot.powerSource.boarding.OnBoardingDialog
import com.SharaSpot.powerSource.contribution.ContributeBottomSheet
import com.SharaSpot.powerSource.details.PowerSourceScreen
import com.SharaSpot.powerSource.details.SourceEvents
import com.SharaSpot.powerSource.media.MediaScreen
import com.SharaSpot.powerSource.reviews.create.FeedbackDialog
import com.SharaSpot.powerSource.reviews.list.ReviewScreen
import com.SharaSpot.ui.HomeUiState

fun NavGraphBuilder.powersourceDestinations(
    navController: NavHostController,
    viewModel: PsViewModel,
    uiState: HomeUiState,
    onRefreshUser: () -> Unit,
    openActiveOrders: () -> Unit,
    openCompletedOrders: () -> Unit
) {
    navigation<AppRoutes.PowerSource>(startDestination = AppRoutes.PowerSource.Details("")) {
        composable<AppRoutes.PowerSource.Details> { backStackEntry ->
            val ps = backStackEntry.toRoute<AppRoutes.PowerSource.Details>()
            PowerSourceScreen(
                powerSourceId = ps.id,
                viewModel = viewModel,
                uiState = uiState,
                onNavigate = {
                    when (it) {
                        is SourceEvents.Charge -> {
                            navController.navigate(AppRoutes.PowerSource.ChargingDialog)
                        }

                        is SourceEvents.HowToCharge -> {
                            navController.navigate(AppRoutes.PowerSource.OnBoarding)
                        }

                        is SourceEvents.Balance -> {
                            navController.navigate(AppRoutes.Payment.Balance.Show)
                        }

                        is SourceEvents.Close -> {
                            navController.popBackStack()
                        }

                        is SourceEvents.Reviews -> {
                            val route = AppRoutes.PowerSource.Reviews(ps.id)
                            navController.navigate(route)
                        }

                        is SourceEvents.Media -> {
                            val route = AppRoutes.PowerSource.Media
                            navController.navigate(route)
                        }

                        is SourceEvents.Contribute -> {
                            val route = AppRoutes.PowerSource.Contribute(ps.id)
                            navController.navigate(route)
                        }

                        else -> {}
                    }
                }
            )
        }

        dialog<AppRoutes.PowerSource.Reviews> { backStackEntry ->
            ReviewScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.PowerSource.Media> {
            MediaScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.PowerSource.OnBoarding> {
            OnBoardingDialog(onDismiss = { navController.popBackStack() })
        }

        dialog<AppRoutes.PowerSource.ChargingDialog> {
            ChargingDialog(
                powerSource = { viewModel.powerSource },
                onDismiss = { navController.popBackStack() },
                onError = { message -> navController.showMessage(message, isError = true) },
                onStartCharging = { chargePointId, time, connector ->
                    val route = AppRoutes.PowerSource.Charging(
                        chargePointId = chargePointId,
                        quantity = time.toQuantity,
                        connector = connector?.number,
                        orderId = ""
                    )
                    navController.navigate(route)
                }
            )
        }

        composable<AppRoutes.PowerSource.Charging> { backStackEntry ->
            ChargingScreen(
                openSessionHistory = { session ->
                    // update user to update balance after charging completion
                    onRefreshUser()
                    // back to home tabs screen
                    navController.popBackStack(
                        AppRoutes.Navigation,
                        inclusive = false
                    )
                    // move to completed sessions
                    openCompletedOrders()
                    val route = AppRoutes.PowerSource.Feedback(session.id, session.chargePointName)
                    navController.navigate(route)
                },
                openActiveSessions = {
                    // back to home tabs screen
                    navController.popBackStack(
                        AppRoutes.Navigation,
                        inclusive = false
                    )
                    openActiveOrders()
                },
                onBack = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.PowerSource.Feedback> {
            FeedbackDialog(
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.PowerSource.Contribute> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.PowerSource.Contribute>()
            ContributeBottomSheet(
                chargerId = route.chargerId,
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}