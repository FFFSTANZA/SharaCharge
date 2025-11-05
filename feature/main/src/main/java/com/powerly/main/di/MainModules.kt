package com.SharaSpot.main.di

import com.SharaSpot.account.di.AccountModule
import com.SharaSpot.home.di.HomeModule
import com.SharaSpot.orders.di.OrdersModule
import com.SharaSpot.scan.di.ScanModule
import org.koin.ksp.generated.module

object MainModules {
    val homeModule = HomeModule().module
    val accountModule = AccountModule().module
    val scanModule = ScanModule().module
    val ordersModule = OrdersModule().module
}