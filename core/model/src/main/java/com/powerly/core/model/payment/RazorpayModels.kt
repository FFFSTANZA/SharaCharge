package com.powerly.core.model.payment

import com.google.gson.annotations.SerializedName
import com.powerly.core.model.api.BaseResponse

// ==================== CREATE ORDER ====================

/**
 * Request body for creating a Razorpay order
 * POST /api/payments/razorpay/order
 */
data class CreateRazorpayOrderRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String = "INR",
    @SerializedName("receipt") val receipt: String? = null,
    @SerializedName("notes") val notes: Map<String, String>? = null
)

/**
 * Response from creating a Razorpay order
 */
data class CreateRazorpayOrderResponse(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("amount") val amount: Int, // Amount in paise (e.g., 50000 for ₹500)
    @SerializedName("currency") val currency: String,
    @SerializedName("receipt") val receipt: String? = null,
    @SerializedName("status") val status: String = "created"
)

class CreateRazorpayOrderApiResponse : BaseResponse<CreateRazorpayOrderResponse>()

// ==================== VERIFY PAYMENT ====================

/**
 * Request body for verifying a Razorpay payment
 * POST /api/payments/razorpay/verify
 *
 * The signature verification MUST be done on the backend for security
 */
data class VerifyRazorpayPaymentRequest(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("payment_id") val paymentId: String,
    @SerializedName("signature") val signature: String
)

/**
 * Response from verifying a Razorpay payment
 */
data class VerifyRazorpayPaymentResponse(
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("status") val status: String, // "SUCCESS", "FAILED", etc.
    @SerializedName("message") val message: String? = null,
    @SerializedName("transaction_id") val transactionId: String? = null,
    @SerializedName("new_balance") val newBalance: Double? = null // Updated user balance
)

class VerifyRazorpayPaymentApiResponse : BaseResponse<VerifyRazorpayPaymentResponse>()

// ==================== PAYMENT STATUS ====================

/**
 * Response for getting payment status
 * GET /api/payments/razorpay/status/{order_id}
 */
data class RazorpayPaymentStatusResponse(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("payment_id") val paymentId: String?,
    @SerializedName("status") val status: PaymentStatus,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String = "INR",
    @SerializedName("payment_method") val paymentMethod: String? = null,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("updated_at") val updatedAt: Long? = null,
    @SerializedName("error_code") val errorCode: String? = null,
    @SerializedName("error_description") val errorDescription: String? = null
)

class RazorpayPaymentStatusApiResponse : BaseResponse<RazorpayPaymentStatusResponse>()

// ==================== WEBHOOK ====================

/**
 * Razorpay webhook event payload
 * POST /api/webhooks/razorpay
 *
 * This is handled on the backend only for server-to-server verification
 * Reference: https://razorpay.com/docs/webhooks/
 */
data class RazorpayWebhookEvent(
    @SerializedName("entity") val entity: String, // "event"
    @SerializedName("account_id") val accountId: String,
    @SerializedName("event") val event: String, // "payment.captured", "payment.failed", etc.
    @SerializedName("contains") val contains: List<String>, // ["payment"]
    @SerializedName("payload") val payload: RazorpayWebhookPayload,
    @SerializedName("created_at") val createdAt: Long
)

data class RazorpayWebhookPayload(
    @SerializedName("payment") val payment: RazorpayWebhookPayment? = null,
    @SerializedName("order") val order: RazorpayWebhookOrder? = null
)

data class RazorpayWebhookPayment(
    @SerializedName("entity") val entity: RazorpayPaymentEntity
)

data class RazorpayPaymentEntity(
    @SerializedName("id") val id: String,
    @SerializedName("entity") val entity: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("currency") val currency: String,
    @SerializedName("status") val status: String,
    @SerializedName("order_id") val orderId: String?,
    @SerializedName("method") val method: String?, // "card", "upi", "netbanking", "wallet"
    @SerializedName("captured") val captured: Boolean,
    @SerializedName("email") val email: String?,
    @SerializedName("contact") val contact: String?,
    @SerializedName("created_at") val createdAt: Long
)

data class RazorpayWebhookOrder(
    @SerializedName("entity") val entity: RazorpayOrderEntity
)

data class RazorpayOrderEntity(
    @SerializedName("id") val id: String,
    @SerializedName("entity") val entity: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("currency") val currency: String,
    @SerializedName("receipt") val receipt: String?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: Long
)
