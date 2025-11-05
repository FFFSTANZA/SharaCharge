# Firebase Remote Config - Tamil Nadu Pricing Configuration

This document outlines the pricing configuration for the SharaCharge app in Tamil Nadu, India, using Firebase Remote Config.

## Currency Configuration

- **Default Currency**: INR (Indian Rupees)
- **Currency Symbol**: ₹
- **Currency Code**: INR

## Pricing Tiers for Tamil Nadu

### 1. Charging Rates

Configure the following parameters in Firebase Remote Config:

```json
{
  "charging_rate_per_kwh_min": 8.00,
  "charging_rate_per_kwh_max": 12.00,
  "charging_rate_currency": "INR",
  "charging_rate_unit": "kWh"
}
```

**Details**:
- **Minimum Rate**: ₹8 per kWh
- **Maximum Rate**: ₹12 per kWh
- **Dynamic Pricing**: Rates can vary based on:
  - Time of day (peak/off-peak hours)
  - Station location
  - Power source capacity
  - Demand levels

### 2. Platform Fees

```json
{
  "platform_fee_percentage": 5.0,
  "platform_fee_type": "percentage",
  "platform_fee_currency": "INR"
}
```

**Details**:
- **Platform Fee**: 5% of total charging cost
- **Type**: Percentage-based
- **Applied To**: Total charging session cost (excluding GST)

### 3. GST (Goods and Services Tax)

```json
{
  "gst_percentage": 18.0,
  "gst_applicable": true,
  "gst_included_in_price": false
}
```

**Details**:
- **GST Rate**: 18% (as per Indian tax regulations)
- **Applied To**: Total amount (charging cost + platform fee)
- **Display**: GST is shown separately in billing breakdown

### 4. Balance Recharge Tiers

Configure popular balance recharge amounts for Indian market:

```json
{
  "balance_tiers": [
    {
      "id": 1,
      "amount": 100,
      "bonus": 0,
      "popular": false,
      "currency": "INR"
    },
    {
      "id": 2,
      "amount": 500,
      "bonus": 25,
      "popular": true,
      "currency": "INR"
    },
    {
      "id": 3,
      "amount": 1000,
      "bonus": 100,
      "popular": true,
      "currency": "INR"
    },
    {
      "id": 4,
      "amount": 2000,
      "bonus": 250,
      "popular": false,
      "currency": "INR"
    },
    {
      "id": 5,
      "amount": 5000,
      "bonus": 750,
      "popular": false,
      "currency": "INR"
    }
  ]
}
```

### 5. Sample Pricing Calculation

**Example Charging Session**:
- Energy Consumed: 50 kWh
- Rate: ₹10 per kWh
- Charging Cost: ₹500
- Platform Fee (5%): ₹25
- Subtotal: ₹525
- GST (18%): ₹94.50
- **Total Amount**: ₹619.50

## Remote Config Parameters Summary

| Parameter | Type | Default Value | Description |
|-----------|------|---------------|-------------|
| `charging_rate_per_kwh_min` | Double | 8.00 | Minimum charging rate per kWh |
| `charging_rate_per_kwh_max` | Double | 12.00 | Maximum charging rate per kWh |
| `charging_rate_currency` | String | "INR" | Currency code for charging |
| `platform_fee_percentage` | Double | 5.0 | Platform fee percentage |
| `gst_percentage` | Double | 18.0 | GST percentage |
| `gst_applicable` | Boolean | true | Whether GST is applicable |
| `balance_tiers` | JSON | See above | Available balance recharge amounts |

## Time-Based Pricing (Optional)

You can configure different rates for peak and off-peak hours:

```json
{
  "peak_hours_rate": 12.00,
  "off_peak_hours_rate": 8.00,
  "peak_hours_start": "09:00",
  "peak_hours_end": "21:00",
  "time_based_pricing_enabled": true
}
```

## Location-Based Pricing (Optional)

Configure different rates for different regions in Tamil Nadu:

```json
{
  "location_pricing": {
    "chennai": {
      "rate_per_kwh": 12.00,
      "platform_fee": 5.0
    },
    "coimbatore": {
      "rate_per_kwh": 10.00,
      "platform_fee": 5.0
    },
    "madurai": {
      "rate_per_kwh": 9.00,
      "platform_fee": 5.0
    },
    "default": {
      "rate_per_kwh": 10.00,
      "platform_fee": 5.0
    }
  }
}
```

## Implementation Notes

1. **Currency Formatting**: All prices are displayed using the `CurrencyFormatter` utility class located at:
   - `core/model/src/main/java/com/powerly/core/model/util/CurrencyFormatter.kt`

2. **Razorpay Integration**: Payments are processed through Razorpay in INR:
   - All amounts are converted to paise (multiply by 100) before sending to Razorpay
   - Use `CurrencyFormatter.rupeesToPaise()` for conversion

3. **Number Formatting**: Indian numbering system is used:
   - Format: ₹1,23,456.78 (not ₹123,456.78)
   - Implemented using `Locale("en", "IN")`

4. **Decimal Precision**: All currency values maintain 2 decimal places

## Testing Configuration

For testing purposes, you can use lower amounts:

```json
{
  "test_mode": true,
  "test_charging_rate": 1.00,
  "test_platform_fee": 0.50,
  "test_balance_tiers": [
    {
      "id": 1,
      "amount": 10,
      "bonus": 1,
      "popular": true,
      "currency": "INR"
    }
  ]
}
```

## Updating Remote Config

1. Login to Firebase Console
2. Navigate to Remote Config section
3. Update the parameters as documented above
4. Publish changes
5. Changes will be reflected in the app after the next config fetch

## Support

For questions about pricing configuration, contact the backend team or refer to the Razorpay integration documentation at:
- `feature/payment/RAZORPAY_INTEGRATION.md`
