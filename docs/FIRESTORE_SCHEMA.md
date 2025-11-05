# Firebase Firestore Schema - SharaSpot

## Overview
This document describes the Firestore collections and document structure for SharaSpot's charging transaction and payment system.

**Important**: SharaSpot operates without GST registration. All pricing is inclusive and transparent.

---

## Collections

### 1. `charging_transactions`

Stores all EV charging transactions with simplified pricing (no GST).

#### Document Structure

```json
{
  "id": "txn_abc123def456",
  "userId": "user_789xyz",
  "chargerId": "charger_001",
  "energyConsumed": 15.5,
  "ratePerKwh": 10.0,
  "chargingCost": 155.0,
  "platformFee": 7.75,
  "totalAmount": 162.75,
  "razorpayOrderId": "order_xyz123",
  "razorpayPaymentId": "pay_abc456",
  "paymentMethod": "UPI",
  "status": "SUCCESS",
  "timestamp": 1699200000000,
  "receiptUrl": "https://storage.googleapis.com/receipts/txn_abc123def456.pdf",
  "chargerName": "Shell Station - Anna Salai",
  "chargerLocation": "Anna Salai, Chennai, Tamil Nadu"
}
```

#### Field Descriptions

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | String | Yes | Unique transaction ID (primary key) |
| `userId` | String | Yes | User who performed the charging |
| `chargerId` | String | Yes | Charger station ID |
| `energyConsumed` | Double | Yes | Energy consumed in kWh |
| `ratePerKwh` | Double | Yes | Charging rate in ₹ per kWh |
| `chargingCost` | Double | Yes | Base charging cost (energy × rate) |
| `platformFee` | Double | Yes | SharaSpot commission (can be 0) |
| `totalAmount` | Double | Yes | Final amount paid (chargingCost + platformFee) |
| `razorpayOrderId` | String | No | Razorpay order ID for payment tracking |
| `razorpayPaymentId` | String | No | Razorpay payment ID after successful payment |
| `paymentMethod` | String | No | Payment method: UPI, CARD, NET_BANKING, WALLET |
| `status` | String | Yes | Payment status: PENDING, SUCCESS, FAILED, CANCELLED |
| `timestamp` | Long | Yes | Unix timestamp in milliseconds |
| `receiptUrl` | String | No | Cloud storage URL for PDF receipt |
| `chargerName` | String | No | Display name of the charging station |
| `chargerLocation` | String | No | Location/address of charging station |

#### Indexes

**Recommended Indexes:**
- `userId` (ascending) + `timestamp` (descending) - for user transaction history
- `chargerId` (ascending) + `timestamp` (descending) - for charger usage analytics
- `status` (ascending) + `timestamp` (descending) - for filtering by status
- `userId` (ascending) + `status` (ascending) + `timestamp` (descending) - for user's successful transactions

#### Security Rules (Example)

```javascript
match /charging_transactions/{transactionId} {
  allow read: if request.auth != null &&
               (request.auth.uid == resource.data.userId ||
                request.auth.token.admin == true);

  allow create: if request.auth != null &&
                 request.auth.uid == request.resource.data.userId;

  allow update: if request.auth.token.admin == true;

  allow delete: if request.auth.token.admin == true;
}
```

---

### 2. `receipts` (Optional)

Stores receipt metadata and content separately from transactions.

#### Document Structure

```json
{
  "transactionId": "txn_abc123def456",
  "userId": "user_789xyz",
  "receiptText": "PAYMENT RECEIPT\n(Not a Tax Invoice)\n...",
  "receiptHtml": "<!DOCTYPE html>...",
  "timestamp": 1699200000000,
  "storageUrl": "gs://bucket/receipts/txn_abc123def456.pdf"
}
```

---

## Data Migration Plan

### From GST-inclusive to GST-free Pricing

If migrating from a previous GST-registered structure:

1. **Identify old fields:**
   - `gstAmount`
   - `gstRate`
   - `cgst`
   - `sgst`
   - `igst`

2. **Migration script:**
   - Calculate new `totalAmount` = `chargingCost + platformFee`
   - Remove GST fields
   - Update all queries and UI components
   - Maintain old data for historical records

3. **Backward compatibility:**
   - Keep old documents unchanged
   - New documents use simplified structure
   - UI handles both formats gracefully

---

## Firebase Remote Config

### Feature Flags

```json
{
  "GST_ENABLED": false,
  "PLATFORM_FEE_PERCENTAGE": 5.0,
  "MIN_CHARGING_AMOUNT": 10.0,
  "MAX_CHARGING_AMOUNT": 10000.0
}
```

### Configuration Descriptions

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `GST_ENABLED` | Boolean | false | Enable GST calculation (future use) |
| `PLATFORM_FEE_PERCENTAGE` | Double | 5.0 | Platform fee as percentage (0-100) |
| `MIN_CHARGING_AMOUNT` | Double | 10.0 | Minimum charging amount in ₹ |
| `MAX_CHARGING_AMOUNT` | Double | 10000.0 | Maximum charging amount in ₹ |

---

## Future GST Support

When SharaSpot obtains GST registration:

### Updated Transaction Structure

```json
{
  "id": "txn_abc123def456",
  "userId": "user_789xyz",
  "chargerId": "charger_001",
  "energyConsumed": 15.5,
  "ratePerKwh": 10.0,
  "chargingCost": 155.0,
  "platformFee": 7.75,
  "subtotal": 162.75,
  "gstRate": 18,
  "gstAmount": 29.29,
  "totalAmount": 192.04,
  "razorpayOrderId": "order_xyz123",
  "razorpayPaymentId": "pay_abc456",
  "paymentMethod": "UPI",
  "status": "SUCCESS",
  "timestamp": 1699200000000,
  "receiptUrl": "https://storage.googleapis.com/invoices/inv_abc123def456.pdf",
  "chargerName": "Shell Station - Anna Salai",
  "chargerLocation": "Anna Salai, Chennai, Tamil Nadu",
  "gstNumber": "29ABCDE1234F1Z5",
  "invoiceNumber": "INV-2024-00123"
}
```

### Migration Path

1. Set `GST_ENABLED = true` in Firebase Remote Config
2. Update `PricingCalculator` to include GST calculation methods
3. Update `ReceiptGenerator` to generate tax invoices instead of receipts
4. Update UI to show GST breakdown
5. Add GST number and invoice numbering system
6. Update Firestore security rules and indexes

---

## Best Practices

### 1. Transaction Creation
```kotlin
val transaction = ChargingTransaction(
    id = UUID.randomUUID().toString(),
    userId = currentUserId,
    chargerId = selectedCharger.id,
    energyConsumed = sessionEnergy,
    ratePerKwh = charger.ratePerKwh,
    chargingCost = PricingCalculator.calculateChargingCost(sessionEnergy, charger.ratePerKwh),
    platformFee = PricingCalculator.calculatePlatformFee(chargingCost),
    totalAmount = PricingCalculator.calculateTotal(chargingCost, platformFee),
    timestamp = System.currentTimeMillis(),
    razorpayOrderId = null,
    razorpayPaymentId = null,
    paymentMethod = null,
    status = PaymentStatus.PENDING,
    chargerName = charger.name,
    chargerLocation = charger.address
)
```

### 2. Querying Transactions
```kotlin
// Get user's recent transactions
firestore.collection("charging_transactions")
    .whereEqualTo("userId", userId)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .limit(20)
    .get()

// Get successful transactions only
firestore.collection("charging_transactions")
    .whereEqualTo("userId", userId)
    .whereEqualTo("status", "SUCCESS")
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .get()
```

### 3. Updating Transaction Status
```kotlin
firestore.collection("charging_transactions")
    .document(transactionId)
    .update(mapOf(
        "status" to "SUCCESS",
        "razorpayPaymentId" to paymentId,
        "paymentMethod" to "UPI",
        "receiptUrl" to receiptUrl
    ))
```

---

## Notes

- **No GST Fields**: Current schema does not include GST-related fields as SharaSpot is not GST registered
- **Receipt vs Invoice**: We generate payment receipts, not tax invoices
- **Compliance**: Suitable for businesses under ₹40 lakhs annual turnover threshold
- **Transparency**: All amounts are inclusive and clearly displayed to users
- **Scalability**: Schema designed for easy GST migration when business grows

---

**Last Updated**: 2025-11-05
**Schema Version**: 1.0.0 (No GST)
