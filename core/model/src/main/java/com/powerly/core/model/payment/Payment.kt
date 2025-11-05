package com.SharaSpot.core.model.payment

import com.SharaSpot.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName

class CardsResponse : BaseResponse<List<PaymentCard>>()
class CardUpdateResponse : BaseResponse<Any>()

// Razorpay Configuration
data class RazorpayConfig(
    val keyId: String,
    val keySecret: String? = null,  // Should only be stored on backend
    val webhookSecret: String? = null  // Should only be stored on backend
)

// Payment Status Enum
enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED
}

// Payment Type Enum
enum class PaymentType {
    CARD,
    UPI,
    NET_BANKING,
    WALLET
}

// Payment Transaction - Store only reference data in Firebase
data class PaymentTransaction(
    @SerializedName("id") val id: String,
    @SerializedName("order_id") val orderId: String,
    @SerializedName("razorpay_payment_id") val razorpayPaymentId: String? = null,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String = "INR",
    @SerializedName("status") val status: PaymentStatus,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("payment_method") val paymentMethod: String? = null
)

data class PaymentCard(
    @SerializedName("id") val id: String,
    @SerializedName("card_number") val cardNumber: String?,
    @SerializedName("payment_option") val paymentOption: String,
    @SerializedName("default") val default: Boolean,
    @SerializedName("card_type") val cardType: String? = null,
    @SerializedName("expiry_month") val expiryMonth: Int? = null,
    @SerializedName("expiry_year") val expiryYear: Int? = null,
    @SerializedName("payment_type") val paymentTypeValue: String? = null
) {
    val paymentType: Int get() = 1
    val razorpayPaymentType: PaymentType get() = when (paymentTypeValue) {
        "UPI" -> PaymentType.UPI
        "NET_BANKING" -> PaymentType.NET_BANKING
        "WALLET" -> PaymentType.WALLET
        else -> PaymentType.CARD
    }
    val isCard: Boolean get() = razorpayPaymentType == PaymentType.CARD
    val isUpi: Boolean get() = razorpayPaymentType == PaymentType.UPI
    val isNetBanking: Boolean get() = razorpayPaymentType == PaymentType.NET_BANKING
    val isWallet: Boolean get() = razorpayPaymentType == PaymentType.WALLET
    val isBalance: Boolean get() = false
    val isMada: Boolean get() = false
    val balance: String get() = "0.0"
    val cardNumberHidden: String get() = "************$cardNumber"

    companion object {
        const val CASH_ON_DELIVERY = 0
        const val PAYMENT_METHOD_CARD = 1
        const val POINT_CHECKOUT = 2
        const val PAYMENT_METHOD_EFAWATEER = 3
        const val PAYPAL = 4
        const val APPLE_PAY = 5
        const val PAYMENT_METHOD_BALANCE = 6
        const val PAYMENT_FAIL = "PAYMENT_FAIL"
        const val PAYFORT_DATA = "PAYFORT_DATA"
        const val POINT_URL = "POINT_URL"
        const val PAYMENT_MADA = "MADA"
        const val PAYMENT_STATUS = "paymentStatus"
    }
}
