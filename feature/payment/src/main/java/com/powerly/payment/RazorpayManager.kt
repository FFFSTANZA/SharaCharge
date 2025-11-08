package com.SharaSpot.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.powerly.core.model.payment.PaymentStatus
import com.powerly.core.model.payment.PaymentTransaction
import org.json.JSONObject
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * RazorpayManager - Handles Razorpay payment integration for Tamil Nadu market
 *
 * Features:
 * - Standard Razorpay checkout flow
 * - Direct UPI intent for UPI payments (opens UPI apps directly)
 * - Payment signature verification
 * - Support for multiple payment methods (Card, UPI, Net Banking, Wallets)
 */
class RazorpayManager(
    private val context: Context,
    private val razorpayKeyId: String
) {

    companion object {
        private const val TAG = "RazorpayManager"
        private const val MERCHANT_NAME = "SharaSpot"

        // UPI app package names for direct intent
        const val PHONEPE_PACKAGE = "com.phonepe.app"
        const val GPAY_PACKAGE = "com.google.android.apps.nbu.paisa.user"
        const val PAYTM_PACKAGE = "net.one97.paytm"
        const val BHIM_PACKAGE = "in.org.npci.upiapp"
        const val AMAZON_PAY_PACKAGE = "in.amazon.mShop.android.shopping"
    }

    private var checkout: Checkout? = null
    private var currentPaymentCallback: PaymentCallback? = null

    init {
        // Preload Razorpay SDK
        Checkout.preload(context.applicationContext)
    }

    /**
     * Initialize payment with amount and currency
     * Returns a JSONObject with payment details
     */
    fun initializePayment(
        amount: Double,
        currency: String = "INR",
        orderId: String? = null,
        customerName: String? = null,
        customerEmail: String? = null,
        customerPhone: String? = null
    ): JSONObject {
        return JSONObject().apply {
            put("key", razorpayKeyId)
            put("amount", (amount * 100).toInt()) // Convert to paise
            put("currency", currency)
            put("name", MERCHANT_NAME)
            put("description", "Add balance to wallet")

            // Order ID (if provided by backend)
            orderId?.let { put("order_id", it) }

            // Customer details
            if (!customerName.isNullOrEmpty() || !customerEmail.isNullOrEmpty() || !customerPhone.isNullOrEmpty()) {
                val prefill = JSONObject().apply {
                    customerName?.let { put("name", it) }
                    customerEmail?.let { put("email", it) }
                    customerPhone?.let { put("contact", it) }
                }
                put("prefill", prefill)
            }

            // Theme customization
            val theme = JSONObject().apply {
                put("color", "#FF6B35") // App theme color
            }
            put("theme", theme)

            // Notes (optional metadata)
            val notes = JSONObject().apply {
                put("merchant_name", MERCHANT_NAME)
                put("platform", "android")
            }
            put("notes", notes)
        }
    }

    /**
     * Open UPI apps directly for payment
     * This provides a better UX than Razorpay's standard UPI flow
     */
    fun openUpiApps(
        activity: Activity,
        orderId: String,
        amount: Double,
        upiId: String = "merchant@upi" // Replace with actual merchant UPI ID
    ): Boolean {
        return try {
            val uri = Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", MERCHANT_NAME)
                .appendQueryParameter("tr", orderId)
                .appendQueryParameter("am", amount.toString())
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tn", "Add balance to wallet")
                .build()

            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Check if any UPI app is installed
            val packageManager = activity.packageManager
            val resolvedActivities = packageManager.queryIntentActivities(intent, 0)

            if (resolvedActivities.isNotEmpty()) {
                activity.startActivityForResult(intent, UPI_REQUEST_CODE)
                true
            } else {
                Log.e(TAG, "No UPI app found on device")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening UPI apps", e)
            false
        }
    }

    /**
     * Check if specific UPI app is installed
     */
    fun isUpiAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get list of installed UPI apps
     */
    fun getInstalledUpiApps(): List<UpiApp> {
        val apps = mutableListOf<UpiApp>()

        if (isUpiAppInstalled(PHONEPE_PACKAGE)) {
            apps.add(UpiApp("PhonePe", PHONEPE_PACKAGE))
        }
        if (isUpiAppInstalled(GPAY_PACKAGE)) {
            apps.add(UpiApp("Google Pay", GPAY_PACKAGE))
        }
        if (isUpiAppInstalled(PAYTM_PACKAGE)) {
            apps.add(UpiApp("Paytm", PAYTM_PACKAGE))
        }
        if (isUpiAppInstalled(BHIM_PACKAGE)) {
            apps.add(UpiApp("BHIM", BHIM_PACKAGE))
        }
        if (isUpiAppInstalled(AMAZON_PAY_PACKAGE)) {
            apps.add(UpiApp("Amazon Pay", AMAZON_PAY_PACKAGE))
        }

        return apps
    }

    /**
     * Process payment using Razorpay standard checkout
     */
    fun processPayment(
        activity: Activity,
        paymentOptions: JSONObject,
        callback: PaymentCallback
    ) {
        try {
            currentPaymentCallback = callback

            checkout = Checkout().apply {
                setKeyID(razorpayKeyId)
                setImage(com.SharaSpot.ui.R.drawable.ic_launcher) // Replace with actual app logo

                // Open Razorpay checkout
                open(activity, paymentOptions)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in payment processing", e)
            callback.onPaymentError(
                PaymentError(
                    code = "CHECKOUT_ERROR",
                    message = e.message ?: "Error opening checkout",
                    exception = e
                )
            )
        }
    }

    /**
     * Verify payment signature using HMAC SHA256
     * This should be done on the backend for security, but can be done on client as well
     */
    fun verifyPaymentSignature(
        orderId: String,
        paymentId: String,
        signature: String,
        keySecret: String
    ): Boolean {
        return try {
            val payload = "$orderId|$paymentId"
            val expectedSignature = generateSignature(payload, keySecret)

            // Constant time comparison to prevent timing attacks
            MessageDigest.isEqual(
                signature.toByteArray(),
                expectedSignature.toByteArray()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying signature", e)
            false
        }
    }

    /**
     * Generate HMAC SHA256 signature
     */
    private fun generateSignature(payload: String, secret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        mac.init(secretKey)

        val hash = mac.doFinal(payload.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Handle payment result from Razorpay
     * Should be called from Activity's onPaymentSuccess/onPaymentError
     */
    fun handlePaymentSuccess(razorpayPaymentId: String, paymentData: PaymentData?) {
        currentPaymentCallback?.onPaymentSuccess(
            PaymentResult(
                paymentId = razorpayPaymentId,
                orderId = paymentData?.orderId,
                signature = paymentData?.signature,
                rawData = paymentData
            )
        )
        currentPaymentCallback = null
    }

    fun handlePaymentError(code: Int, message: String) {
        currentPaymentCallback?.onPaymentError(
            PaymentError(
                code = code.toString(),
                message = message
            )
        )
        currentPaymentCallback = null
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        currentPaymentCallback = null
        checkout = null
    }
}

// Callback interface for payment results
interface PaymentCallback {
    fun onPaymentSuccess(result: PaymentResult)
    fun onPaymentError(error: PaymentError)
}

// Payment result data class
data class PaymentResult(
    val paymentId: String,
    val orderId: String?,
    val signature: String?,
    val rawData: PaymentData?
)

// Payment error data class
data class PaymentError(
    val code: String,
    val message: String,
    val exception: Exception? = null
)

// UPI App data class
data class UpiApp(
    val name: String,
    val packageName: String
)

// Request codes
const val UPI_REQUEST_CODE = 1001
const val RAZORPAY_REQUEST_CODE = 1002
