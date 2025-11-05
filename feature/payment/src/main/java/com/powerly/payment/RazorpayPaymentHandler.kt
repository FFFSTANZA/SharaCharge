package com.SharaSpot.payment

import android.app.Activity
import android.util.Log
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.SharaSpot.core.model.payment.PaymentStatus
import com.SharaSpot.core.model.payment.PaymentTransaction
import java.util.UUID

/**
 * RazorpayPaymentHandler
 *
 * Handles Razorpay payment callbacks and integrates with Activity.
 * Implements PaymentResultWithDataListener to receive payment results.
 *
 * Usage:
 * 1. Make your Activity implement PaymentResultWithDataListener
 * 2. Delegate the callback methods to RazorpayPaymentHandler
 * 3. Provide callbacks for success, error, and cancellation
 *
 * Example in Activity:
 * ```
 * class YourActivity : ComponentActivity(), PaymentResultWithDataListener {
 *     private lateinit var paymentHandler: RazorpayPaymentHandler
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         paymentHandler = RazorpayPaymentHandler(
 *             activity = this,
 *             onPaymentSuccess = { transaction ->
 *                 // Handle successful payment
 *                 showSuccessMessage(transaction)
 *             },
 *             onPaymentError = { error ->
 *                 // Handle payment error
 *                 showErrorMessage(error)
 *             }
 *         )
 *     }
 *
 *     override fun onPaymentSuccess(razorpayPaymentId: String, paymentData: PaymentData?) {
 *         paymentHandler.onPaymentSuccess(razorpayPaymentId, paymentData)
 *     }
 *
 *     override fun onPaymentError(code: Int, message: String, paymentData: PaymentData?) {
 *         paymentHandler.onPaymentError(code, message, paymentData)
 *     }
 * }
 * ```
 */
class RazorpayPaymentHandler(
    private val activity: Activity,
    private val onPaymentSuccess: (PaymentTransaction) -> Unit,
    private val onPaymentError: (PaymentError) -> Unit
) : PaymentResultWithDataListener {

    companion object {
        private const val TAG = "RazorpayPaymentHandler"
    }

    private var razorpayManager: RazorpayManager? = null
    private var currentOrderId: String? = null
    private var currentAmount: Double = 0.0

    /**
     * Initialize payment handler with RazorpayManager
     */
    fun initializeWithManager(manager: RazorpayManager) {
        this.razorpayManager = manager
    }

    /**
     * Set current order details for transaction tracking
     */
    fun setOrderDetails(orderId: String, amount: Double) {
        this.currentOrderId = orderId
        this.currentAmount = amount
    }

    /**
     * Called when payment is successful
     */
    override fun onPaymentSuccess(razorpayPaymentId: String, paymentData: PaymentData?) {
        Log.d(TAG, "Payment successful: $razorpayPaymentId")

        // Create payment transaction
        val transaction = PaymentTransaction(
            id = UUID.randomUUID().toString(),
            orderId = paymentData?.orderId ?: currentOrderId ?: "",
            razorpayPaymentId = razorpayPaymentId,
            amount = currentAmount,
            currency = "INR",
            status = PaymentStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            paymentMethod = extractPaymentMethod(paymentData)
        )

        // Notify success callback
        onPaymentSuccess(transaction)

        // Also notify RazorpayManager if available
        razorpayManager?.handlePaymentSuccess(razorpayPaymentId, paymentData)
    }

    /**
     * Called when payment fails or is cancelled
     */
    override fun onPaymentError(code: Int, message: String, paymentData: PaymentData?) {
        Log.e(TAG, "Payment error: Code=$code, Message=$message")

        // Determine if payment was cancelled or failed
        val status = when (code) {
            0 -> PaymentStatus.CANCELLED // User cancelled
            else -> PaymentStatus.FAILED
        }

        // Create error details
        val error = PaymentError(
            code = code.toString(),
            message = message
        )

        // Create failed transaction (optional - for tracking)
        val transaction = PaymentTransaction(
            id = UUID.randomUUID().toString(),
            orderId = paymentData?.orderId ?: currentOrderId ?: "",
            razorpayPaymentId = null,
            amount = currentAmount,
            currency = "INR",
            status = status,
            timestamp = System.currentTimeMillis(),
            paymentMethod = null
        )

        // Notify error callback
        onPaymentError(error)

        // Also notify RazorpayManager if available
        razorpayManager?.handlePaymentError(code, message)
    }

    /**
     * Extract payment method from PaymentData
     */
    private fun extractPaymentMethod(paymentData: PaymentData?): String? {
        return try {
            // PaymentData contains information about the payment method used
            // This is a best-effort extraction
            paymentData?.let {
                when {
                    it.userContact != null -> "UPI"
                    it.userEmail != null -> "Card"
                    else -> "Unknown"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting payment method", e)
            null
        }
    }

    /**
     * Handle UPI payment response (for direct UPI intent)
     */
    fun handleUpiPaymentResponse(response: String): PaymentTransaction {
        // Parse UPI response
        // Format: txnId=...&responseCode=...&Status=...&txnRef=...
        val params = response.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }

        val status = when (params["Status"]?.lowercase()) {
            "success" -> PaymentStatus.SUCCESS
            "failure" -> PaymentStatus.FAILED
            else -> PaymentStatus.CANCELLED
        }

        return PaymentTransaction(
            id = UUID.randomUUID().toString(),
            orderId = currentOrderId ?: "",
            razorpayPaymentId = params["txnId"],
            amount = currentAmount,
            currency = "INR",
            status = status,
            timestamp = System.currentTimeMillis(),
            paymentMethod = "UPI"
        )
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        razorpayManager?.cleanup()
        razorpayManager = null
        currentOrderId = null
        currentAmount = 0.0
    }
}

/**
 * Extension function to handle payment result in Activity
 */
fun Activity.handlePaymentResult(
    requestCode: Int,
    resultCode: Int,
    data: android.content.Intent?,
    handler: RazorpayPaymentHandler
) {
    if (requestCode == UPI_REQUEST_CODE) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val response = data.getStringExtra("response") ?: ""
            val transaction = handler.handleUpiPaymentResponse(response)

            if (transaction.status == PaymentStatus.SUCCESS) {
                // Handle success via handler's callback
                Log.d("PaymentResult", "UPI payment successful")
            } else {
                // Handle failure
                val error = PaymentError(
                    code = "UPI_ERROR",
                    message = "UPI payment failed or cancelled"
                )
                Log.e("PaymentResult", "UPI payment failed")
            }
        } else {
            // Payment cancelled
            val error = PaymentError(
                code = "UPI_CANCELLED",
                message = "Payment cancelled by user"
            )
            Log.w("PaymentResult", "UPI payment cancelled")
        }
    }
}
