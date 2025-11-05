# No-GST Pricing Implementation for SharaSpot

## Overview

This document provides a comprehensive guide to the no-GST pricing structure implementation for SharaSpot. SharaSpot operates below the GST registration threshold (₹40 lakhs annual turnover) and provides transparent, inclusive pricing without separate GST charges.

**Implementation Date**: November 5, 2025
**Version**: 1.0.0

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Core Components](#core-components)
3. [Implementation Details](#implementation-details)
4. [Usage Examples](#usage-examples)
5. [Migration Path](#migration-path)
6. [Testing](#testing)
7. [Deployment](#deployment)

---

## Architecture Overview

### Pricing Flow

```
┌─────────────┐
│   User      │
│  Charging   │
└──────┬──────┘
       │
       ▼
┌─────────────────────────┐
│  Energy Consumed (kWh)  │
│  × Rate per kWh         │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  PricingCalculator      │
│  - Charging Cost        │
│  - Platform Fee (5%)    │
│  - Total Amount         │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  ChargingTransaction    │
│  (Firestore Document)   │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Payment via Razorpay   │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Receipt Generated      │
│  (Not Tax Invoice)      │
└─────────────────────────┘
```

### Module Structure

```
SharaCharge/
├── common/lib/
│   └── src/main/java/com/powerly/lib/
│       ├── pricing/
│       │   └── PricingCalculator.kt       ✅ NEW
│       ├── receipt/
│       │   └── ReceiptGenerator.kt        ✅ NEW
│       └── config/
│           └── RemoteConfigManager.kt     ✅ NEW
├── core/model/
│   └── src/main/java/com/powerly/core/model/payment/
│       └── ChargingTransaction.kt         ✅ NEW
├── feature/payment/
│   └── src/main/java/com/powerly/payment/
│       ├── balance/add/
│       │   └── AddBalanceContent.kt       ✅ UPDATED
│       └── charging/
│           └── ChargingTransactionDetails.kt  ✅ NEW
└── docs/
    ├── FIRESTORE_SCHEMA.md                ✅ NEW
    ├── FIREBASE_REMOTE_CONFIG.md          ✅ NEW
    ├── PRICING_TRANSPARENCY.md            ✅ NEW
    ├── TERMS_AND_CONDITIONS.md            ✅ NEW
    └── NO_GST_PRICING_IMPLEMENTATION.md   ✅ THIS FILE
```

---

## Core Components

### 1. PricingCalculator.kt

**Location**: `common/lib/src/main/java/com/powerly/lib/pricing/PricingCalculator.kt`

**Purpose**: Calculate charging costs, platform fees, and totals

**Key Methods**:
```kotlin
// Calculate base charging cost
PricingCalculator.calculateChargingCost(energyKwh: Double, ratePerKwh: Double): Double

// Calculate platform fee (default 5%)
PricingCalculator.calculatePlatformFee(amount: Double, feePercent: Double = 0.05): Double

// Calculate total amount
PricingCalculator.calculateTotal(chargingCost: Double, platformFee: Double): Double

// Get full breakdown
PricingCalculator.calculateFullBreakdown(energyKwh: Double, ratePerKwh: Double): PricingBreakdown

// Format currency
PricingCalculator.formatINR(amount: Double): String  // Returns "₹1,234.56"
```

**Example**:
```kotlin
val breakdown = PricingCalculator.calculateFullBreakdown(
    energyKwh = 10.0,
    ratePerKwh = 12.0
)
// Result:
// - chargingCost: ₹120.00
// - platformFee: ₹6.00
// - totalAmount: ₹126.00
```

---

### 2. ChargingTransaction.kt

**Location**: `core/model/src/main/java/com/powerly/core/model/payment/ChargingTransaction.kt`

**Purpose**: Data model for charging transactions

**Structure**:
```kotlin
data class ChargingTransaction(
    val id: String,
    val chargerId: String,
    val userId: String,
    val energyConsumed: Double,     // kWh
    val ratePerKwh: Double,         // ₹ per kWh
    val chargingCost: Double,       // Base cost
    val platformFee: Double,        // SharaSpot commission
    val totalAmount: Double,        // Final amount
    val timestamp: Long,
    val razorpayOrderId: String?,
    val razorpayPaymentId: String?,
    val paymentMethod: String?,
    val status: PaymentStatus,
    val receiptUrl: String?,
    val chargerName: String?,
    val chargerLocation: String?
)
```

**No GST fields**: ❌ `gstAmount`, `gstRate`, `cgst`, `sgst`, `igst`

---

### 3. ReceiptGenerator.kt

**Location**: `common/lib/src/main/java/com/powerly/lib/receipt/ReceiptGenerator.kt`

**Purpose**: Generate payment receipts (NOT tax invoices)

**Methods**:
```kotlin
// Generate text receipt
ReceiptGenerator.generateReceiptText(transaction: ChargingTransaction): String

// Generate HTML receipt (for PDF conversion)
ReceiptGenerator.generateReceiptHtml(transaction: ChargingTransaction): String

// Generate receipt data for Firebase
ReceiptGenerator.generateReceiptData(transaction: ChargingTransaction): ReceiptData
```

**Receipt Format**:
```
----------------------------------------
        PAYMENT RECEIPT
     (Not a Tax Invoice)
----------------------------------------

           SharaSpot
      EV Charging Network

Transaction ID: txn_abc123
Date: 05-Nov-2025, 2:30 PM

Charger: Shell Station
Location: Anna Salai, Chennai

----------------------------------------

Energy Consumed: 15.00 kWh
Rate: ₹10.00 per kWh
Charging Cost: ₹150.00
Platform Fee: ₹7.50

      Total Paid: ₹157.50

----------------------------------------

Payment Method: UPI
Payment ID: pay_xyz123

----------------------------------------
  Thank you for using SharaSpot!

Note: This is a payment receipt
and not a tax invoice. SharaSpot
is not registered under GST.
----------------------------------------
```

---

### 4. RemoteConfigManager.kt

**Location**: `common/lib/src/main/java/com/powerly/lib/config/RemoteConfigManager.kt`

**Purpose**: Manage Firebase Remote Config for dynamic pricing configuration

**Key Flags**:
```kotlin
RemoteConfigManager.isGstEnabled()                    // false (current)
RemoteConfigManager.getPlatformFeePercentage()        // 0.05 (5%)
RemoteConfigManager.isPlatformFeeEnabled()            // true
RemoteConfigManager.getMinChargingAmount()            // ₹10.00
RemoteConfigManager.getMaxChargingAmount()            // ₹10,000.00
RemoteConfigManager.shouldShowGstMigrationBanner()    // false
```

---

### 5. ChargingTransactionDetails.kt

**Location**: `feature/payment/src/main/java/com/powerly/payment/charging/ChargingTransactionDetails.kt`

**Purpose**: UI component to display transaction pricing breakdown

**Composables**:
```kotlin
// Full transaction details with breakdown
@Composable
fun ChargingTransactionDetails(transaction: ChargingTransaction)

// Compact summary for lists
@Composable
fun ChargingTransactionSummary(transaction: ChargingTransaction)
```

**UI Display**:
```
┌─────────────────────────────┐
│  Payment Breakdown          │
├─────────────────────────────┤
│  Energy Consumed    15.0 kWh│
│  Rate          ₹10.00 per kWh│
├─────────────────────────────┤
│  Charging Cost      ₹150.00 │
│  Platform Fee         ₹7.50 │
├─────────────────────────────┤
│  Total Paid         ₹157.50 │
│                             │
│  All prices are inclusive.  │
│  No hidden fees.            │
└─────────────────────────────┘
```

---

## Implementation Details

### Creating a Charging Transaction

```kotlin
// 1. Calculate pricing
val breakdown = PricingCalculator.calculateFullBreakdown(
    energyKwh = sessionEnergy,
    ratePerKwh = charger.ratePerKwh,
    feePercent = RemoteConfigManager.getPlatformFeePercentage()
)

// 2. Validate amount
if (!RemoteConfigManager.isAmountValid(breakdown.totalAmount)) {
    val error = RemoteConfigManager.getAmountValidationError(breakdown.totalAmount)
    showError(error)
    return
}

// 3. Create transaction
val transaction = ChargingTransaction(
    id = UUID.randomUUID().toString(),
    userId = currentUser.id,
    chargerId = charger.id,
    energyConsumed = breakdown.energyKwh,
    ratePerKwh = breakdown.ratePerKwh,
    chargingCost = breakdown.chargingCost,
    platformFee = breakdown.platformFee,
    totalAmount = breakdown.totalAmount,
    timestamp = System.currentTimeMillis(),
    razorpayOrderId = null,
    razorpayPaymentId = null,
    paymentMethod = null,
    status = PaymentStatus.PENDING,
    chargerName = charger.name,
    chargerLocation = charger.address
)

// 4. Save to Firestore
firestore.collection("charging_transactions")
    .document(transaction.id)
    .set(ChargingTransactionDocument.fromChargingTransaction(transaction))

// 5. Process payment via Razorpay
val razorpayOrder = createRazorpayOrder(transaction.totalAmount)
transaction.razorpayOrderId = razorpayOrder.id

// 6. After payment success, update transaction
transaction.status = PaymentStatus.SUCCESS
transaction.razorpayPaymentId = paymentId
transaction.paymentMethod = "UPI"

// 7. Generate receipt
val receiptData = ReceiptGenerator.generateReceiptData(transaction)
val receiptUrl = uploadReceiptToStorage(receiptData)
transaction.receiptUrl = receiptUrl

// 8. Update Firestore
firestore.collection("charging_transactions")
    .document(transaction.id)
    .update(mapOf(
        "status" to "SUCCESS",
        "razorpayPaymentId" to paymentId,
        "paymentMethod" to "UPI",
        "receiptUrl" to receiptUrl
    ))
```

---

### Displaying Transaction in UI

```kotlin
@Composable
fun TransactionScreen(transactionId: String) {
    val transaction by viewModel.getTransaction(transactionId).collectAsState()

    transaction?.let {
        ChargingTransactionDetails(
            transaction = it,
            showHeader = true
        )
    }
}
```

---

### Generating and Downloading Receipt

```kotlin
fun downloadReceipt(transaction: ChargingTransaction) {
    // Generate HTML receipt
    val html = ReceiptGenerator.generateReceiptHtml(transaction)

    // Convert to PDF (using library like iTextPDF or Android PdfDocument)
    val pdf = convertHtmlToPdf(html)

    // Save to downloads
    saveToDownloads(pdf, "SharaSpot_Receipt_${transaction.id}.pdf")

    // Or upload to Firebase Storage
    val storageRef = Firebase.storage.reference
        .child("receipts/${transaction.userId}/${transaction.id}.pdf")

    storageRef.putBytes(pdf)
        .addOnSuccessListener { taskSnapshot ->
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                transaction.receiptUrl = uri.toString()
                updateTransactionInFirestore(transaction)
            }
        }
}
```

---

## Usage Examples

### Example 1: Calculate Pricing

```kotlin
// User charges 15 kWh at ₹10 per kWh
val breakdown = PricingCalculator.calculateFullBreakdown(
    energyKwh = 15.0,
    ratePerKwh = 10.0
)

println(breakdown.getFormattedBreakdown())
// Output:
// energy: 15.00 kWh
// rate: ₹10.00
// chargingCost: ₹150.00
// platformFee: ₹7.50
// total: ₹157.50
```

---

### Example 2: Check if Platform Fee is Enabled

```kotlin
val platformFee = if (RemoteConfigManager.isPlatformFeeEnabled()) {
    PricingCalculator.calculatePlatformFee(
        chargingCost,
        RemoteConfigManager.getPlatformFeePercentage()
    )
} else {
    0.0  // Promotional period - no fee
}
```

---

### Example 3: Validate Amount

```kotlin
val totalAmount = 157.50

if (RemoteConfigManager.isAmountValid(totalAmount)) {
    // Proceed with payment
    processPayment(totalAmount)
} else {
    val error = RemoteConfigManager.getAmountValidationError(totalAmount)
    showError(error)
}
```

---

### Example 4: Display Transaction List

```kotlin
@Composable
fun TransactionList(transactions: List<ChargingTransaction>) {
    LazyColumn {
        items(transactions) { transaction ->
            Card(
                modifier = Modifier.padding(8.dp),
                onClick = { navigateToDetails(transaction.id) }
            ) {
                ChargingTransactionSummary(transaction = transaction)
            }
        }
    }
}
```

---

## Migration Path

### When SharaSpot Gets GST Registration

**Step 1: Announce (30 days before)**
```kotlin
// Firebase Remote Config
RemoteConfigManager.shouldShowGstMigrationBanner() // Set to true
RemoteConfigManager.getGstMigrationDate() // Set to "2025-12-31"
```

**Step 2: Update PricingCalculator**
```kotlin
// Add GST calculation method
fun calculateWithGst(
    energyKwh: Double,
    ratePerKwh: Double,
    gstRate: Double = 0.18
): PricingBreakdownWithGst {
    val chargingCost = calculateChargingCost(energyKwh, ratePerKwh)
    val platformFee = calculatePlatformFee(chargingCost)
    val subtotal = chargingCost + platformFee
    val gstAmount = subtotal * gstRate
    val totalAmount = subtotal + gstAmount

    return PricingBreakdownWithGst(
        energyKwh = energyKwh,
        ratePerKwh = ratePerKwh,
        chargingCost = chargingCost,
        platformFee = platformFee,
        subtotal = subtotal,
        gstRate = gstRate,
        gstAmount = gstAmount,
        totalAmount = totalAmount
    )
}
```

**Step 3: Enable GST**
```kotlin
// Firebase Remote Config (on migration date)
RemoteConfigManager.isGstEnabled() // Set to true
```

**Step 4: Update UI**
```kotlin
@Composable
fun PricingDisplay(breakdown: PricingBreakdown) {
    if (RemoteConfigManager.isGstEnabled()) {
        // Show with GST breakdown
        ShowPricingWithGst(breakdown)
    } else {
        // Show simple pricing
        ShowSimplePricing(breakdown)
    }
}
```

**Step 5: Update Receipt Generator**
```kotlin
fun generateInvoice(transaction: ChargingTransaction): String {
    // Generate tax invoice instead of receipt
    // Include GSTIN number
    // Include invoice numbering
}
```

---

## Testing

### Unit Tests

```kotlin
class PricingCalculatorTest {

    @Test
    fun `calculate charging cost`() {
        val cost = PricingCalculator.calculateChargingCost(10.0, 12.0)
        assertEquals(120.0, cost)
    }

    @Test
    fun `calculate platform fee`() {
        val fee = PricingCalculator.calculatePlatformFee(120.0, 0.05)
        assertEquals(6.0, fee)
    }

    @Test
    fun `calculate total`() {
        val total = PricingCalculator.calculateTotal(120.0, 6.0)
        assertEquals(126.0, total)
    }

    @Test
    fun `full breakdown calculation`() {
        val breakdown = PricingCalculator.calculateFullBreakdown(10.0, 12.0)

        assertEquals(10.0, breakdown.energyKwh)
        assertEquals(12.0, breakdown.ratePerKwh)
        assertEquals(120.0, breakdown.chargingCost)
        assertEquals(6.0, breakdown.platformFee)
        assertEquals(126.0, breakdown.totalAmount)
    }

    @Test
    fun `format INR`() {
        val formatted = PricingCalculator.formatINR(1234.56)
        assertEquals("₹1,234.56", formatted)
    }
}
```

### Integration Tests

```kotlin
class ChargingTransactionTest {

    @Test
    fun `create and save transaction`() = runBlocking {
        // Create transaction
        val transaction = ChargingTransaction(
            id = "test_txn_001",
            userId = "user_001",
            chargerId = "charger_001",
            energyConsumed = 15.0,
            ratePerKwh = 10.0,
            chargingCost = 150.0,
            platformFee = 7.5,
            totalAmount = 157.5,
            timestamp = System.currentTimeMillis(),
            razorpayOrderId = null,
            razorpayPaymentId = null,
            paymentMethod = null,
            status = PaymentStatus.PENDING
        )

        // Save to Firestore
        val doc = ChargingTransactionDocument.fromChargingTransaction(transaction)
        firestore.collection("charging_transactions")
            .document(transaction.id)
            .set(doc)
            .await()

        // Retrieve and verify
        val retrieved = firestore.collection("charging_transactions")
            .document(transaction.id)
            .get()
            .await()
            .toObject(ChargingTransactionDocument::class.java)

        assertNotNull(retrieved)
        assertEquals(transaction.id, retrieved?.id)
        assertEquals(transaction.totalAmount, retrieved?.totalAmount)
    }
}
```

---

## Deployment

### Step 1: Firebase Remote Config Setup

1. Go to Firebase Console → Remote Config
2. Add parameters as per `FIREBASE_REMOTE_CONFIG.md`
3. Set values:
   - `GST_ENABLED = false`
   - `PLATFORM_FEE_PERCENTAGE = 5.0`
   - `ENABLE_PLATFORM_FEE = true`
   - `MIN_CHARGING_AMOUNT = 10.0`
   - `MAX_CHARGING_AMOUNT = 10000.0`
4. Publish changes

### Step 2: Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /charging_transactions/{transactionId} {
      allow read: if request.auth != null &&
                   (request.auth.uid == resource.data.userId ||
                    request.auth.token.admin == true);

      allow create: if request.auth != null &&
                     request.auth.uid == request.resource.data.userId;

      allow update: if request.auth.token.admin == true;

      allow delete: if request.auth.token.admin == true;
    }
  }
}
```

### Step 3: Firestore Indexes

```bash
# Create composite indexes
firestore indexes create --collection-group=charging_transactions --field=userId --field=timestamp
firestore indexes create --collection-group=charging_transactions --field=chargerId --field=timestamp
firestore indexes create --collection-group=charging_transactions --field=status --field=timestamp
```

### Step 4: Build and Deploy

```bash
# Build app
./gradlew assembleRelease

# Test on staging
./gradlew installStagingDebug

# Deploy to production
./gradlew bundleRelease
# Upload to Play Store
```

---

## Documentation

All documentation is available in `/docs`:

- **FIRESTORE_SCHEMA.md** - Firestore collections and documents
- **FIREBASE_REMOTE_CONFIG.md** - Remote Config setup and usage
- **PRICING_TRANSPARENCY.md** - User-facing pricing transparency guide
- **TERMS_AND_CONDITIONS.md** - Legal terms and conditions
- **NO_GST_PRICING_IMPLEMENTATION.md** - This file

---

## Support and Maintenance

### Monitoring

Monitor these metrics:
- Transaction success rate
- Average transaction amount
- Platform fee revenue
- Payment failures
- Receipt generation success

### Analytics Events

Track these events:
```kotlin
analytics.logEvent("transaction_created", bundle {
    putString("transaction_id", transaction.id)
    putDouble("amount", transaction.totalAmount)
    putBoolean("has_platform_fee", transaction.platformFee > 0)
})

analytics.logEvent("receipt_generated", bundle {
    putString("transaction_id", transaction.id)
})

analytics.logEvent("receipt_downloaded", bundle {
    putString("transaction_id", transaction.id)
})
```

---

## FAQs

**Q: Can we charge GST without registration?**
A: No, it's illegal to charge GST without GSTIN registration.

**Q: What happens if turnover exceeds ₹40 lakhs?**
A: We must register for GST within 30 days and start charging GST.

**Q: Can users claim expenses with payment receipts?**
A: Yes, payment receipts are valid for expense claims. Consult CA for specifics.

**Q: How to handle charger owners with GST?**
A: They handle their own GST. We only facilitate payments.

**Q: Is platform fee mandatory?**
A: It can be toggled via Remote Config for promotions.

---

**Last Updated**: November 5, 2025
**Version**: 1.0.0
**Status**: ✅ Ready for Production
