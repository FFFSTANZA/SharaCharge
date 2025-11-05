# Razorpay Payment Gateway Integration for Tamil Nadu Market

## Overview

This document provides a comprehensive guide for integrating Razorpay payment gateway into the SharaCharge application, specifically optimized for the Tamil Nadu market with support for UPI, Cards, Net Banking, and Wallets.

## Table of Contents

1. [Architecture](#architecture)
2. [Setup](#setup)
3. [Components](#components)
4. [Usage Guide](#usage-guide)
5. [Payment Flow](#payment-flow)
6. [Security Considerations](#security-considerations)
7. [Testing](#testing)
8. [Troubleshooting](#troubleshooting)

---

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────┐
│              User Interface Layer               │
│  (AddBalanceContent, UpiAppSelectionDialog)    │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│           Business Logic Layer                  │
│  (BalanceViewModel, RazorpayPaymentHandler)    │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│          Payment Integration Layer              │
│         (RazorpayManager, PaymentRepository)    │
└────────────────┬────────────────────────────────┘
                 │
         ┌───────┴────────┐
         │                │
┌────────▼─────┐  ┌──────▼────────┐
│   Razorpay   │  │    Firebase   │
│     SDK      │  │   Firestore   │
│  (Payments)  │  │ (Transaction  │
│              │  │   Tracking)   │
└──────────────┘  └───────────────┘
```

### Data Flow

1. **User Action**: User selects balance amount and payment method
2. **Order Creation**: Backend creates Razorpay order
3. **Payment Initiation**: RazorpayManager opens checkout or UPI intent
4. **Payment Processing**: Razorpay processes the payment
5. **Callback Handling**: RazorpayPaymentHandler receives result
6. **Verification**: Backend verifies payment signature
7. **Transaction Storage**: Firebase stores transaction reference
8. **Balance Update**: User balance is updated on success

---

## Setup

### 1. Dependencies

Already added to `feature/payment/build.gradle.kts`:

```kotlin
dependencies {
    // Razorpay Payment Gateway
    implementation("com.razorpay:checkout:1.6.40")
}
```

### 2. Firebase Remote Config

Add Razorpay API keys to Firebase Remote Config:

```json
{
  "razorpay_key_id": "rzp_test_xxxxxxxxxxxxx",
  "razorpay_key_secret": "SECRET_STORED_ON_BACKEND_ONLY",
  "razorpay_webhook_secret": "SECRET_STORED_ON_BACKEND_ONLY"
}
```

**SECURITY NOTE**: Only `razorpay_key_id` should be in Remote Config. Secrets must be stored securely on the backend.

### 3. AndroidManifest.xml

Add required permissions:

```xml
<manifest>
    <!-- Razorpay SDK Requirements -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <!-- For UPI Direct Intent -->
    <queries>
        <package android:name="com.phonepe.app" />
        <package android:name="com.google.android.apps.nbu.paisa.user" />
        <package android:name="net.one97.paytm" />
        <package android:name="in.org.npci.upiapp" />
        <package android:name="in.amazon.mShop.android.shopping" />
    </queries>
</manifest>
```

### 4. ProGuard Rules (if using R8/ProGuard)

Add to `proguard-rules.pro`:

```proguard
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}
```

---

## Components

### 1. Data Models (`core/model/payment/Payment.kt`)

#### RazorpayConfig
```kotlin
data class RazorpayConfig(
    val keyId: String,
    val keySecret: String? = null,  // Backend only
    val webhookSecret: String? = null  // Backend only
)
```

#### PaymentStatus
```kotlin
enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED
}
```

#### PaymentType
```kotlin
enum class PaymentType {
    CARD,
    UPI,
    NET_BANKING,
    WALLET
}
```

#### PaymentTransaction
```kotlin
data class PaymentTransaction(
    val id: String,
    val orderId: String,
    val razorpayPaymentId: String?,
    val amount: Double,
    val currency: String = "INR",
    val status: PaymentStatus,
    val timestamp: Long,
    val paymentMethod: String?
)
```

**IMPORTANT**: Only transaction references are stored in Firebase. All sensitive payment data is stored and managed by Razorpay.

### 2. RazorpayManager (`feature/payment/RazorpayManager.kt`)

Main class for Razorpay integration.

#### Key Methods:

- `initializePayment()`: Creates payment options JSONObject
- `processPayment()`: Opens Razorpay checkout
- `openUpiApps()`: Launches UPI apps directly (direct intent)
- `getInstalledUpiApps()`: Returns list of installed UPI apps
- `verifyPaymentSignature()`: Client-side signature verification (backend should also verify)

#### Example Usage:

```kotlin
// Initialize manager
val razorpayManager = RazorpayManager(
    context = applicationContext,
    razorpayKeyId = "rzp_test_xxxxxxxxxxxxx"
)

// Create payment options
val paymentOptions = razorpayManager.initializePayment(
    amount = 500.0, // Amount in INR
    currency = "INR",
    orderId = "order_123",
    customerName = "John Doe",
    customerEmail = "john@example.com",
    customerPhone = "+919876543210"
)

// Process payment
razorpayManager.processPayment(
    activity = this,
    paymentOptions = paymentOptions,
    callback = object : PaymentCallback {
        override fun onPaymentSuccess(result: PaymentResult) {
            // Handle success
        }

        override fun onPaymentError(error: PaymentError) {
            // Handle error
        }
    }
)
```

### 3. RazorpayPaymentHandler (`feature/payment/RazorpayPaymentHandler.kt`)

Handles payment callbacks from Razorpay SDK.

#### Implementation in Activity:

```kotlin
class PaymentActivity : ComponentActivity(), PaymentResultWithDataListener {

    private lateinit var paymentHandler: RazorpayPaymentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentHandler = RazorpayPaymentHandler(
            activity = this,
            onPaymentSuccess = { transaction ->
                // Update UI with success
                showSuccessMessage(transaction)
                updateBalance(transaction.amount)
            },
            onPaymentError = { error ->
                // Show error to user
                showErrorMessage(error.message)
            }
        )
    }

    override fun onPaymentSuccess(razorpayPaymentId: String, paymentData: PaymentData?) {
        paymentHandler.onPaymentSuccess(razorpayPaymentId, paymentData)
    }

    override fun onPaymentError(code: Int, message: String, paymentData: PaymentData?) {
        paymentHandler.onPaymentError(code, message, paymentData)
    }
}
```

### 4. UPI Direct Intent (`feature/payment/upi/UpiAppSelectionDialog.kt`)

Composable dialog for UPI app selection.

#### Features:
- Shows list of installed UPI apps
- Direct intent to selected UPI app (better UX)
- Fallback to Razorpay standard checkout
- Supports: PhonePe, Google Pay, Paytm, BHIM, Amazon Pay

#### Usage:

```kotlin
val razorpayManager = RazorpayManager(context, keyId)
val upiApps = razorpayManager.getInstalledUpiApps()

var showUpiDialog by remember { mutableStateOf(false) }

if (showUpiDialog) {
    UpiAppSelectionDialog(
        upiApps = upiApps,
        onAppSelected = { app ->
            razorpayManager.openUpiApps(
                activity = activity,
                orderId = orderId,
                amount = amount,
                upiId = "merchant@upi"
            )
            showUpiDialog = false
        },
        onUseRazorpay = {
            razorpayManager.processPayment(activity, paymentOptions, callback)
            showUpiDialog = false
        },
        onDismiss = { showUpiDialog = false }
    )
}
```

### 5. Payment Repository (`core/data/repositories/PaymentRepository.kt`)

Repository methods for Razorpay integration.

#### New Methods:

```kotlin
interface PaymentRepository {
    // Create Razorpay order on backend
    suspend fun createRazorpayOrder(amount: Double, currency: String = "INR"): ApiStatus<String>

    // Verify payment signature with backend
    suspend fun verifyPayment(orderId: String, paymentId: String, signature: String): ApiStatus<Boolean>

    // Get payment status
    suspend fun getPaymentStatus(orderId: String): ApiStatus<PaymentStatus>

    // Save transaction reference (NOT sensitive data)
    suspend fun savePaymentTransaction(transaction: PaymentTransaction): ApiStatus<Boolean>

    // Get transaction history
    suspend fun getPaymentTransactions(userId: String): ApiStatus<List<PaymentTransaction>>
}
```

---

## Usage Guide

### Complete Payment Flow Example

```kotlin
@Composable
fun PaymentScreen(viewModel: PaymentViewModel) {
    val context = LocalContext.current
    val activity = context as Activity

    // Get Razorpay key from Remote Config
    val razorpayKeyId = "rzp_test_xxxxxxxxxxxxx" // From Firebase Remote Config

    // Initialize RazorpayManager
    val razorpayManager = remember {
        RazorpayManager(context.applicationContext, razorpayKeyId)
    }

    // Initialize payment handler
    val paymentHandler = remember {
        RazorpayPaymentHandler(
            activity = activity,
            onPaymentSuccess = { transaction ->
                viewModel.handlePaymentSuccess(transaction)
            },
            onPaymentError = { error ->
                viewModel.handlePaymentError(error)
            }
        ).apply {
            initializeWithManager(razorpayManager)
        }
    }

    // UI State
    var showUpiDialog by remember { mutableStateOf(false) }
    var selectedAmount by remember { mutableStateOf(500.0) }

    Column {
        // Amount selection UI
        AmountSelector(
            selectedAmount = selectedAmount,
            onAmountSelected = { selectedAmount = it }
        )

        // Payment button
        Button(
            onClick = {
                viewModel.startPayment(selectedAmount) { orderId ->
                    // Show UPI app selection
                    showUpiDialog = true
                    paymentHandler.setOrderDetails(orderId, selectedAmount)
                }
            }
        ) {
            Text("Pay Now")
        }
    }

    // UPI App Selection Dialog
    if (showUpiDialog) {
        val upiApps = razorpayManager.getInstalledUpiApps()

        UpiAppSelectionDialog(
            upiApps = upiApps,
            onAppSelected = { app ->
                razorpayManager.openUpiApps(
                    activity = activity,
                    orderId = viewModel.currentOrderId,
                    amount = selectedAmount,
                    upiId = "yourmerchant@upi"
                )
                showUpiDialog = false
            },
            onUseRazorpay = {
                val paymentOptions = razorpayManager.initializePayment(
                    amount = selectedAmount,
                    orderId = viewModel.currentOrderId
                )
                razorpayManager.processPayment(
                    activity = activity,
                    paymentOptions = paymentOptions,
                    callback = object : PaymentCallback {
                        override fun onPaymentSuccess(result: PaymentResult) {
                            paymentHandler.onPaymentSuccess(result.paymentId, result.rawData)
                        }
                        override fun onPaymentError(error: PaymentError) {
                            paymentHandler.onPaymentError(0, error.message, null)
                        }
                    }
                )
                showUpiDialog = false
            },
            onDismiss = { showUpiDialog = false }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            razorpayManager.cleanup()
            paymentHandler.cleanup()
        }
    }
}
```

---

## Payment Flow

### Standard Razorpay Checkout Flow

```
1. User selects amount → 2. Create order (backend)
         ↓
3. Initialize payment options
         ↓
4. Open Razorpay checkout
         ↓
5. User completes payment
         ↓
6. Razorpay callback (success/failure)
         ↓
7. Verify signature (backend)
         ↓
8. Save transaction reference (Firebase)
         ↓
9. Update user balance
```

### UPI Direct Intent Flow

```
1. User selects amount → 2. Create order (backend)
         ↓
3. Show UPI app selection dialog
         ↓
4. User selects UPI app (PhonePe, GPay, etc.)
         ↓
5. Launch UPI app with payment details
         ↓
6. User completes payment in UPI app
         ↓
7. Return to app with result
         ↓
8. Verify payment (backend)
         ↓
9. Save transaction reference (Firebase)
         ↓
10. Update user balance
```

---

## Security Considerations

### 1. API Key Management

✅ **DO:**
- Store `razorpay_key_id` in Firebase Remote Config
- Store secrets (`key_secret`, `webhook_secret`) on backend only
- Use environment variables for backend secrets
- Rotate keys periodically

❌ **DON'T:**
- Hardcode keys in the app
- Store secrets in app code or resources
- Commit secrets to version control

### 2. Payment Verification

✅ **DO:**
- Always verify payment signature on backend
- Use webhook for server-to-server verification
- Double-check payment status from Razorpay Dashboard
- Log all payment attempts

❌ **DON'T:**
- Trust client-side verification alone
- Skip signature verification
- Process payment without backend confirmation

### 3. Data Storage

✅ **DO:**
- Store only transaction references in Firebase
- Let Razorpay store all sensitive payment data
- Use Razorpay Dashboard for payment reconciliation
- Implement proper access controls

❌ **DON'T:**
- Store card numbers or CVV
- Store full payment details
- Log sensitive payment information

### 4. PCI Compliance

Razorpay handles PCI compliance automatically:
- Card data never touches your server
- PCI DSS Level 1 compliant
- 3D Secure authentication
- Tokenization for saved cards

---

## Testing

### Test Mode Setup

1. Use Razorpay test API keys:
   - Key ID: `rzp_test_xxxxxxxxxxxxx`
   - Key Secret: (for backend only)

2. Test Cards:
   - Success: `4111 1111 1111 1111`
   - Failure: `4000 0000 0000 0002`
   - CVV: Any 3 digits
   - Expiry: Any future date

3. Test UPI:
   - UPI ID: `success@razorpay`
   - UPI ID: `failure@razorpay`

### Testing Checklist

- [ ] Test successful card payment
- [ ] Test failed card payment
- [ ] Test payment cancellation
- [ ] Test UPI payment (each app)
- [ ] Test network failure scenarios
- [ ] Test signature verification
- [ ] Test transaction storage
- [ ] Test balance update
- [ ] Test payment callback handling
- [ ] Test error messages

---

## Troubleshooting

### Common Issues

#### 1. Razorpay checkout not opening

**Solution:**
- Check internet connection
- Verify API key is correct
- Check ProGuard rules
- Ensure SDK is preloaded: `Checkout.preload(context)`

#### 2. Payment success but balance not updated

**Solution:**
- Check backend verification
- Verify webhook is configured
- Check Firebase transaction storage
- Look for backend errors in logs

#### 3. UPI apps not detected

**Solution:**
- Add `<queries>` to AndroidManifest.xml
- Target SDK 30+ requires package visibility declaration
- Test on physical device (emulators may not have UPI apps)

#### 4. Payment signature verification failed

**Solution:**
- Check key_secret on backend
- Verify order_id matches
- Ensure HMAC SHA256 implementation is correct
- Check for whitespace in keys

### Debug Logs

Enable Razorpay debug logs:

```kotlin
Checkout.setDebugMode(BuildConfig.DEBUG)
```

### Support Resources

- Razorpay Documentation: https://razorpay.com/docs/
- Razorpay Android SDK: https://razorpay.com/docs/payment-gateway/android-integration/
- Support: support@razorpay.com

---

## Backend Integration (TODO)

The following backend endpoints need to be implemented:

### 1. Create Order
```
POST /api/payments/razorpay/order
{
  "amount": 500.0,
  "currency": "INR"
}

Response:
{
  "order_id": "order_xxxxxxxxxxxxx",
  "amount": 50000,
  "currency": "INR"
}
```

### 2. Verify Payment
```
POST /api/payments/razorpay/verify
{
  "order_id": "order_xxxxxxxxxxxxx",
  "payment_id": "pay_xxxxxxxxxxxxx",
  "signature": "xxxxxxxxxxxxxxxxxxxxxx"
}

Response:
{
  "verified": true,
  "status": "SUCCESS"
}
```

### 3. Get Payment Status
```
GET /api/payments/razorpay/status/{order_id}

Response:
{
  "order_id": "order_xxxxxxxxxxxxx",
  "status": "SUCCESS",
  "payment_id": "pay_xxxxxxxxxxxxx"
}
```

### 4. Webhook Handler
```
POST /api/webhooks/razorpay
{
  // Razorpay webhook payload
}
```

---

## Razorpay Dashboard

Use Razorpay Dashboard for:
- View all payments
- Refund payments
- Download reports
- View settlement details
- Configure webhooks
- Manage API keys

Dashboard URL: https://dashboard.razorpay.com/

---

## Next Steps

1. **Backend Implementation**
   - Implement order creation endpoint
   - Implement signature verification endpoint
   - Configure Razorpay webhook
   - Set up proper error handling

2. **UI Enhancement**
   - Add custom UPI app icons
   - Improve error messages
   - Add payment method icons
   - Implement loading states

3. **Testing**
   - Test on multiple devices
   - Test with different UPI apps
   - Test edge cases (network failures, etc.)
   - Load testing

4. **Production Checklist**
   - Switch to production API keys
   - Configure production webhook
   - Set up monitoring and alerts
   - Review security measures
   - Update Firebase Remote Config

---

## Contact

For questions or issues related to this integration:
- Create an issue in the repository
- Contact the development team

---

Last Updated: 2025-11-05
Version: 1.0.0
