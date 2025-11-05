# Firebase Remote Config - SharaSpot

## Overview

Firebase Remote Config allows dynamic configuration of app behavior without requiring app updates. This document describes the configuration parameters for SharaSpot's pricing and feature flags.

---

## Configuration Parameters

### 1. GST_ENABLED

**Type**: Boolean
**Default Value**: `false`
**Description**: Controls whether GST calculation is enabled in the app

**Usage**:
```kotlin
val gstEnabled = FirebaseRemoteConfig.getInstance().getBoolean("GST_ENABLED")

if (gstEnabled) {
    // Show GST breakdown
    // Use tax invoice format
    // Calculate GST on transactions
} else {
    // Show simple pricing
    // Use payment receipt format
    // No GST calculation
}
```

**When to Change**:
- Set to `true` when SharaSpot obtains GST registration
- Announce change 30 days in advance to users
- Update app to handle GST calculations before enabling

**Impact**:
- Changes pricing display throughout app
- Switches from receipts to tax invoices
- Adds GST line items to transactions
- Updates Firestore schema usage

---

### 2. PLATFORM_FEE_PERCENTAGE

**Type**: Double
**Default Value**: `5.0`
**Description**: Platform fee percentage (SharaSpot commission)

**Range**: 0.0 - 100.0

**Usage**:
```kotlin
val feePercent = FirebaseRemoteConfig.getInstance().getDouble("PLATFORM_FEE_PERCENTAGE") / 100.0

val platformFee = PricingCalculator.calculatePlatformFee(
    amount = chargingCost,
    feePercent = feePercent
)
```

**Best Practices**:
- Announce changes 30 days in advance
- Keep fee reasonable (industry standard: 3-10%)
- Consider promotional periods (0-2%)
- Document reason for changes

**Examples**:
- `5.0` = 5% commission (current)
- `0.0` = No platform fee (promotional)
- `7.5` = 7.5% commission (future adjustment)

---

### 3. GST_RATE

**Type**: Double
**Default Value**: `18.0`
**Description**: GST rate percentage (when GST_ENABLED = true)

**Range**: 0.0 - 100.0

**Usage**:
```kotlin
if (gstEnabled) {
    val gstRate = FirebaseRemoteConfig.getInstance().getDouble("GST_RATE") / 100.0
    val gstAmount = subtotal * gstRate
    val total = subtotal + gstAmount
}
```

**Note**:
- Only applicable when GST_ENABLED = true
- Standard GST rate for services in India: 18%
- May vary based on government regulations
- Update when GST rates change

---

### 4. MIN_CHARGING_AMOUNT

**Type**: Double
**Default Value**: `10.0`
**Description**: Minimum charging amount in rupees

**Usage**:
```kotlin
val minAmount = FirebaseRemoteConfig.getInstance().getDouble("MIN_CHARGING_AMOUNT")

if (chargingCost < minAmount) {
    showError("Minimum charging amount is ₹$minAmount")
}
```

**Purpose**:
- Prevent micro-transactions
- Cover payment processing costs
- Ensure profitable operations

**Typical Values**:
- `10.0` - Minimum viable transaction
- `20.0` - Conservative minimum
- `5.0` - Promotional minimum

---

### 5. MAX_CHARGING_AMOUNT

**Type**: Double
**Default Value**: `10000.0`
**Description**: Maximum charging amount in rupees

**Usage**:
```kotlin
val maxAmount = FirebaseRemoteConfig.getInstance().getDouble("MAX_CHARGING_AMOUNT")

if (chargingCost > maxAmount) {
    showError("Maximum charging amount is ₹$maxAmount")
}
```

**Purpose**:
- Fraud prevention
- Risk management
- Payment gateway limits

**Typical Values**:
- `10000.0` - Standard maximum
- `25000.0` - Premium user limit
- `50000.0` - Fleet/commercial limit

---

### 6. ENABLE_PLATFORM_FEE

**Type**: Boolean
**Default Value**: `true`
**Description**: Whether to charge platform fee

**Usage**:
```kotlin
val enableFee = FirebaseRemoteConfig.getInstance().getBoolean("ENABLE_PLATFORM_FEE")

val platformFee = if (enableFee) {
    PricingCalculator.calculatePlatformFee(chargingCost)
} else {
    0.0
}
```

**Use Cases**:
- Promotional periods (disable fees)
- First-time user bonus (no fees)
- Partner charger networks (waived fees)
- Beta testing period

---

### 7. RAZORPAY_KEY_ID

**Type**: String
**Default Value**: `"rzp_test_xxxxxx"`
**Description**: Razorpay API key ID

**Usage**:
```kotlin
val keyId = FirebaseRemoteConfig.getInstance().getString("RAZORPAY_KEY_ID")
razorpay.setKeyId(keyId)
```

**Security Note**:
- Public key, safe to expose in app
- Use test key for development
- Use live key for production

**Values**:
- Test: `rzp_test_xxxxxxxxxxxxxx`
- Live: `rzp_live_xxxxxxxxxxxxxx`

---

### 8. SHOW_GST_MIGRATION_BANNER

**Type**: Boolean
**Default Value**: `false`
**Description**: Show banner announcing upcoming GST registration

**Usage**:
```kotlin
val showBanner = FirebaseRemoteConfig.getInstance().getBoolean("SHOW_GST_MIGRATION_BANNER")

if (showBanner) {
    // Show in-app banner:
    // "Notice: SharaSpot will be GST registered from [DATE].
    //  Prices will include 18% GST from then onwards."
}
```

**Timeline**:
- 30 days before GST registration: Set to `true`
- Show banner on home screen
- Link to detailed announcement
- After migration: Set to `false`

---

### 9. GST_MIGRATION_DATE

**Type**: String
**Default Value**: `""`
**Description**: Date when GST will be enabled (ISO 8601 format)

**Usage**:
```kotlin
val migrationDate = FirebaseRemoteConfig.getInstance().getString("GST_MIGRATION_DATE")

if (migrationDate.isNotEmpty()) {
    val targetDate = parseISODate(migrationDate)
    val daysRemaining = calculateDaysUntil(targetDate)

    showNotice("GST will be applicable in $daysRemaining days")
}
```

**Format**: `"2025-12-31"`

**Purpose**:
- Inform users of upcoming changes
- Countdown timer in app
- Email reminders

---

### 10. RECEIPT_FOOTER_TEXT

**Type**: String
**Default Value**: See below
**Description**: Footer text shown on receipts

**Default Value**:
```
"This is a payment receipt and not a tax invoice. SharaSpot is not registered under GST. All prices shown are inclusive and final."
```

**Usage**:
```kotlin
val footerText = FirebaseRemoteConfig.getInstance().getString("RECEIPT_FOOTER_TEXT")
// Add to receipt generation
```

**When GST Enabled**:
```
"This is a tax invoice. SharaSpot is registered under GST (GSTIN: 29ABCDE1234F1Z5)."
```

---

## Setup Instructions

### Step 1: Firebase Console Setup

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your SharaSpot project
3. Navigate to: **Remote Config**
4. Click **"Add parameter"** for each configuration

### Step 2: Add Parameters

Add all parameters listed above with their default values.

**Example Configuration**:

| Parameter Name | Type | Default Value |
|----------------|------|---------------|
| GST_ENABLED | Boolean | false |
| PLATFORM_FEE_PERCENTAGE | Number | 5.0 |
| GST_RATE | Number | 18.0 |
| MIN_CHARGING_AMOUNT | Number | 10.0 |
| MAX_CHARGING_AMOUNT | Number | 10000.0 |
| ENABLE_PLATFORM_FEE | Boolean | true |
| RAZORPAY_KEY_ID | String | rzp_test_xxx |
| SHOW_GST_MIGRATION_BANNER | Boolean | false |
| GST_MIGRATION_DATE | String | (empty) |
| RECEIPT_FOOTER_TEXT | String | (see above) |

### Step 3: Conditions (Optional)

Create conditions for different user segments:

**Example: Beta Users (No Platform Fee)**
```
Condition: app_version >= 2.0.0 AND user_type == beta
Parameter: ENABLE_PLATFORM_FEE = false
```

**Example: Regional Pricing**
```
Condition: user_location == "Tamil Nadu"
Parameter: MIN_CHARGING_AMOUNT = 10.0

Condition: user_location == "Karnataka"
Parameter: MIN_CHARGING_AMOUNT = 15.0
```

### Step 4: Publish Changes

1. Review all parameters
2. Click **"Publish changes"**
3. Changes go live immediately

---

## Implementation in Code

### RemoteConfigManager.kt

```kotlin
package com.powerly.lib.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.ktx.Firebase

object RemoteConfigManager {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    // Default values
    private val defaults = mapOf(
        "GST_ENABLED" to false,
        "PLATFORM_FEE_PERCENTAGE" to 5.0,
        "GST_RATE" to 18.0,
        "MIN_CHARGING_AMOUNT" to 10.0,
        "MAX_CHARGING_AMOUNT" to 10000.0,
        "ENABLE_PLATFORM_FEE" to true,
        "RAZORPAY_KEY_ID" to "rzp_test_xxxxxx",
        "SHOW_GST_MIGRATION_BANNER" to false,
        "GST_MIGRATION_DATE" to "",
        "RECEIPT_FOOTER_TEXT" to "This is a payment receipt and not a tax invoice. SharaSpot is not registered under GST."
    )

    fun initialize() {
        // Set config settings
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values
        remoteConfig.setDefaultsAsync(defaults)

        // Fetch and activate
        fetchAndActivate()
    }

    fun fetchAndActivate() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Remote Config fetched and activated")
                } else {
                    println("Remote Config fetch failed")
                }
            }
    }

    // Getters
    fun isGstEnabled(): Boolean = remoteConfig.getBoolean("GST_ENABLED")

    fun getPlatformFeePercentage(): Double = remoteConfig.getDouble("PLATFORM_FEE_PERCENTAGE") / 100.0

    fun getGstRate(): Double = remoteConfig.getDouble("GST_RATE") / 100.0

    fun getMinChargingAmount(): Double = remoteConfig.getDouble("MIN_CHARGING_AMOUNT")

    fun getMaxChargingAmount(): Double = remoteConfig.getDouble("MAX_CHARGING_AMOUNT")

    fun isPlatformFeeEnabled(): Boolean = remoteConfig.getBoolean("ENABLE_PLATFORM_FEE")

    fun getRazorpayKeyId(): String = remoteConfig.getString("RAZORPAY_KEY_ID")

    fun shouldShowGstMigrationBanner(): Boolean = remoteConfig.getBoolean("SHOW_GST_MIGRATION_BANNER")

    fun getGstMigrationDate(): String = remoteConfig.getString("GST_MIGRATION_DATE")

    fun getReceiptFooterText(): String = remoteConfig.getString("RECEIPT_FOOTER_TEXT")
}
```

### Usage in Application

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Remote Config
        RemoteConfigManager.initialize()
    }
}
```

### Usage in ViewModel

```kotlin
class ChargingViewModel : ViewModel() {

    fun calculatePrice(energyKwh: Double, ratePerKwh: Double): PricingBreakdown {
        val chargingCost = PricingCalculator.calculateChargingCost(energyKwh, ratePerKwh)

        val platformFee = if (RemoteConfigManager.isPlatformFeeEnabled()) {
            PricingCalculator.calculatePlatformFee(
                chargingCost,
                RemoteConfigManager.getPlatformFeePercentage()
            )
        } else {
            0.0
        }

        val subtotal = chargingCost + platformFee

        val totalAmount = if (RemoteConfigManager.isGstEnabled()) {
            val gstAmount = subtotal * RemoteConfigManager.getGstRate()
            subtotal + gstAmount
        } else {
            subtotal
        }

        return PricingBreakdown(
            energyKwh = energyKwh,
            ratePerKwh = ratePerKwh,
            chargingCost = chargingCost,
            platformFee = platformFee,
            totalAmount = totalAmount
        )
    }
}
```

---

## Testing

### Test Different Configurations

```kotlin
@Test
fun testPricingWithoutGst() {
    // Mock Remote Config
    mockRemoteConfig.setBoolean("GST_ENABLED", false)
    mockRemoteConfig.setDouble("PLATFORM_FEE_PERCENTAGE", 5.0)

    val pricing = calculatePrice(10.0, 12.0)

    assertEquals(120.0, pricing.chargingCost)
    assertEquals(6.0, pricing.platformFee)
    assertEquals(126.0, pricing.totalAmount)
}

@Test
fun testPricingWithGst() {
    // Mock Remote Config
    mockRemoteConfig.setBoolean("GST_ENABLED", true)
    mockRemoteConfig.setDouble("PLATFORM_FEE_PERCENTAGE", 5.0)
    mockRemoteConfig.setDouble("GST_RATE", 18.0)

    val pricing = calculatePrice(10.0, 12.0)

    assertEquals(120.0, pricing.chargingCost)
    assertEquals(6.0, pricing.platformFee)
    assertEquals(148.68, pricing.totalAmount) // 126 + 22.68 GST
}
```

---

## Migration Checklist

When enabling GST:

- [ ] Update Remote Config: `GST_ENABLED = true`
- [ ] Update Remote Config: `GST_RATE = 18.0`
- [ ] Update Remote Config: `RECEIPT_FOOTER_TEXT` with new text
- [ ] Show migration banner 30 days before: `SHOW_GST_MIGRATION_BANNER = true`
- [ ] Set migration date: `GST_MIGRATION_DATE = "2025-12-31"`
- [ ] Test all pricing calculations with GST
- [ ] Update Firestore schema to include GST fields
- [ ] Test receipt/invoice generation
- [ ] Update Terms and Conditions
- [ ] Send email notification to all users
- [ ] Monitor for issues after migration
- [ ] Disable migration banner after completion

---

**Last Updated**: November 5, 2025
