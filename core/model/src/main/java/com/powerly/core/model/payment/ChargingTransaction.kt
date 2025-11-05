package com.powerly.core.model.payment

/**
 * ChargingTransaction model for SharaSpot
 *
 * Simplified transaction model without GST (no GST registration).
 * All amounts are inclusive and transparent.
 */
data class ChargingTransaction(
    val id: String,
    val chargerId: String,
    val userId: String,
    val energyConsumed: Double,  // kWh
    val ratePerKwh: Double,      // ₹ per kWh
    val chargingCost: Double,    // Base charging cost
    val platformFee: Double,     // SharaSpot commission (if any)
    val totalAmount: Double,     // Final amount paid
    val timestamp: Long,
    val razorpayOrderId: String?,
    val razorpayPaymentId: String?,
    val paymentMethod: String?,  // UPI, CARD, NET_BANKING, WALLET
    val status: PaymentStatus,
    val receiptUrl: String? = null,
    val chargerName: String? = null,
    val chargerLocation: String? = null
) {
    /**
     * Get formatted energy consumed
     */
    fun getFormattedEnergy(): String {
        return String.format("%.2f kWh", energyConsumed)
    }

    /**
     * Get formatted rate
     */
    fun getFormattedRate(): String {
        return String.format("₹%.2f per kWh", ratePerKwh)
    }

    /**
     * Get formatted charging cost
     */
    fun getFormattedChargingCost(): String {
        return String.format("₹%.2f", chargingCost)
    }

    /**
     * Get formatted platform fee
     */
    fun getFormattedPlatformFee(): String {
        return String.format("₹%.2f", platformFee)
    }

    /**
     * Get formatted total amount
     */
    fun getFormattedTotal(): String {
        return String.format("₹%.2f", totalAmount)
    }

    /**
     * Check if platform fee is applicable
     */
    fun hasPlatformFee(): Boolean {
        return platformFee > 0
    }
}

/**
 * Request body for creating a charging transaction
 */
data class CreateChargingTransactionRequest(
    val chargerId: String,
    val energyConsumed: Double,
    val ratePerKwh: Double,
    val chargingCost: Double,
    val platformFee: Double,
    val totalAmount: Double,
    val paymentMethodId: String? = null
)

/**
 * Response after creating a charging transaction
 */
data class ChargingTransactionResponse(
    val transactionId: String,
    val razorpayOrderId: String,
    val amount: Double,
    val currency: String = "INR",
    val status: PaymentStatus
)

/**
 * Firestore document structure for charging transactions
 */
data class ChargingTransactionDocument(
    val id: String = "",
    val userId: String = "",
    val chargerId: String = "",
    val energyConsumed: Double = 0.0,
    val ratePerKwh: Double = 0.0,
    val chargingCost: Double = 0.0,
    val platformFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val razorpayOrderId: String? = null,
    val razorpayPaymentId: String? = null,
    val paymentMethod: String? = null,
    val status: String = "PENDING",
    val timestamp: Long = System.currentTimeMillis(),
    val receiptUrl: String? = null,
    val chargerName: String? = null,
    val chargerLocation: String? = null
) {
    /**
     * Convert to ChargingTransaction domain model
     */
    fun toChargingTransaction(): ChargingTransaction {
        return ChargingTransaction(
            id = id,
            chargerId = chargerId,
            userId = userId,
            energyConsumed = energyConsumed,
            ratePerKwh = ratePerKwh,
            chargingCost = chargingCost,
            platformFee = platformFee,
            totalAmount = totalAmount,
            timestamp = timestamp,
            razorpayOrderId = razorpayOrderId,
            razorpayPaymentId = razorpayPaymentId,
            paymentMethod = paymentMethod,
            status = PaymentStatus.valueOf(status),
            receiptUrl = receiptUrl,
            chargerName = chargerName,
            chargerLocation = chargerLocation
        )
    }

    companion object {
        /**
         * Create from ChargingTransaction domain model
         */
        fun fromChargingTransaction(transaction: ChargingTransaction): ChargingTransactionDocument {
            return ChargingTransactionDocument(
                id = transaction.id,
                userId = transaction.userId,
                chargerId = transaction.chargerId,
                energyConsumed = transaction.energyConsumed,
                ratePerKwh = transaction.ratePerKwh,
                chargingCost = transaction.chargingCost,
                platformFee = transaction.platformFee,
                totalAmount = transaction.totalAmount,
                razorpayOrderId = transaction.razorpayOrderId,
                razorpayPaymentId = transaction.razorpayPaymentId,
                paymentMethod = transaction.paymentMethod,
                status = transaction.status.name,
                timestamp = transaction.timestamp,
                receiptUrl = transaction.receiptUrl,
                chargerName = transaction.chargerName,
                chargerLocation = transaction.chargerLocation
            )
        }
    }
}
