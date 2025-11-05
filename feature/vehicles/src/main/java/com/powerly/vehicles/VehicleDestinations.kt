package com.SharaSpot.vehicles

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.SharaSpot.lib.AppRoutes
import com.SharaSpot.lib.IRoute
import com.SharaSpot.vehicles.newVehicle.VehicleAddScreen
import com.SharaSpot.vehicles.vehicleDetails.make.MakesScreen
import com.SharaSpot.vehicles.vehicleDetails.model.ModelScreen
import com.SharaSpot.vehicles.vehicleDetails.options.DetailsScreen
import com.SharaSpot.vehicles.vehicleList.VehiclesScreen

fun NavGraphBuilder.vehiclesDestinations(
    navController: NavHostController,
    viewModel: VehiclesViewModel
) {
    navigation<AppRoutes.Vehicles>(startDestination = AppRoutes.Vehicles.List) {
        composable<AppRoutes.Vehicles.List> {
            VehiclesScreen(
                viewModel = viewModel,
                addNewVehicle = { navController.navigate(AppRoutes.Vehicles.New) },
                onBack = { navController.popBackStack() }
            )
        }
        composable<AppRoutes.Vehicles.New> {
            VehicleAddScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                openManufacturer = {
                    navController.navigate(AppRoutes.Vehicles.New.Manufacturer)
                },
                openModel = {
                    navController.navigate(AppRoutes.Vehicles.New.Model)
                },
                openOptions = {
                    navController.navigate(AppRoutes.Vehicles.New.Options)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Manufacturer> {
            val viewModel = viewModel
            MakesScreen(
                viewModel = viewModel,
                direction = AppRoutes.Vehicles.New.Manufacturer,
                onClose = { navController.popBackStack() },
                onNext = {
                    viewModel.setMake(it)
                    navController.next(AppRoutes.Vehicles.New.Model)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Model> {
            val viewModel = viewModel
            ModelScreen(
                viewModel = viewModel,
                direction = AppRoutes.Vehicles.New.Model,
                onClose = { navController.popBackStack() },
                onNext = {
                    viewModel.setModel(it)
                    navController.next(AppRoutes.Vehicles.New.Options)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Options> {
            DetailsScreen(
                viewModel = viewModel,
                direction = AppRoutes.Vehicles.New.Options,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


private fun NavHostController.next(destination: IRoute) {
    this.navigate(destination) {
        popUpTo(AppRoutes.Vehicles.New) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
