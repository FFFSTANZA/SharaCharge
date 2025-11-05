package com.SharaSpot.payment

/**
 * Payment manager interface for handling payment operations.
 * This is a placeholder interface for future payment integration.
 */
interface PaymentManager {

    /**
     * Initializes the payment system with required configuration.
     *
     * @return True if initialization was successful, false otherwise
     */
    fun initializePayment(): Boolean

    /**
     * Processes a payment transaction.
     *
     * @param amount The payment amount
     * @param currency The currency code (e.g., "USD", "SAR")
     * @param onSuccess Callback invoked when payment is successful
     * @param onError Callback invoked when payment fails with error message
     */
    fun processPayment(
        amount: Double,
        currency: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Validates payment information before processing.
     *
     * @param paymentDetails Map containing payment details to validate
     * @return True if payment details are valid, false otherwise
     */
    fun validatePayment(paymentDetails: Map<String, Any>): Boolean
}
