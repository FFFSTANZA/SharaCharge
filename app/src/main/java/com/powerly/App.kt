package com.SharaSpot

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.SharaSpot.account.di.AccountModule
import com.SharaSpot.core.data.di.DataModule
import com.SharaSpot.core.database.di.DatabaseModule
import com.SharaSpot.core.network.di.NetworkModule
import com.SharaSpot.lib.di.LibModule
import com.SharaSpot.main.di.MainModules
import com.SharaSpot.orders.di.OrdersModule
import com.SharaSpot.payment.di.PaymentModule
import com.SharaSpot.powerSource.di.PsModules
import com.SharaSpot.splash.di.SplashModule
import com.SharaSpot.user.di.UserModule
import com.SharaSpot.vehicles.di.VehiclesModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@App)
            modules(
                LibModule().module,
                DatabaseModule().module,
                NetworkModule().module,
                DataModule().module,
                PaymentModule().module,
                OrdersModule().module,
                AccountModule().module,
                MainModules.homeModule,
                MainModules.ordersModule,
                MainModules.accountModule,
                MainModules.scanModule,
                VehiclesModule().module,
                SplashModule().module,
                UserModule().module,
                VehiclesModule().module,
                PsModules.psModule,
                PsModules.chargeModule,
                defaultModule,
            )

        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}


