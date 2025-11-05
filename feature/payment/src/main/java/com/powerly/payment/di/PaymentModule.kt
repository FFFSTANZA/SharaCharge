package com.SharaSpot.payment.di

import com.SharaSpot.payment.PaymentManager
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.SharaSpot.payment")
class PaymentModule {
    @Single
    fun providePaymentManager(): PaymentManager {
        return object : PaymentManager {
            override fun initializePayment(): Boolean = false

            override fun processPayment(
                amount: Double,
                currency: String,
                onSuccess: () -> Unit,
                onError: (String) -> Unit
            ) {
                onError("Payment integration not available")
            }

            override fun validatePayment(paymentDetails: Map<String, Any>): Boolean = false
        }
    }
}