package com.SharaSpot.powerSource.di

import com.SharaSpot.charge.di.ChargeModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.SharaSpot.powerSource")
class PowerSourceModule


object PsModules {
    val psModule = PowerSourceModule().module
    val chargeModule = ChargeModule().module
}