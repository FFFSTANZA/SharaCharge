package com.powerly.lib.receipt

import com.powerly.core.model.payment.ChargingTransaction
import com.powerly.lib.pricing.PricingCalculator
import java.text.SimpleDateFormat
import java.util.*

/**
 * ReceiptGenerator for SharaSpot
 *
 * Generates simple payment receipts (NOT tax invoices).
 * SharaSpot is not registered under GST, so we cannot issue tax invoices.
 *
 * Note: This is a text-based receipt generator. For PDF generation,
 * integrate with a PDF library like iText or PDFBox.
 */
object ReceiptGenerator {

    private const val RECEIPT_WIDTH = 40
    private const val HEADER_SEPARATOR = "----------------------------------------"
    private const val FOOTER_SEPARATOR = "----------------------------------------"

    /**
     * Generate receipt text for a charging transaction
     */
    fun generateReceiptText(transaction: ChargingTransaction): String {
        return buildString {
            // Header
            appendLine(centerText("PAYMENT RECEIPT"))
            appendLine(centerText("(Not a Tax Invoice)"))
            appendLine(HEADER_SEPARATOR)
            appendLine()

            // SharaSpot branding
            appendLine(centerText("SharaSpot"))
            appendLine(centerText("EV Charging Network"))
            appendLine()

            // Transaction details
            appendLine("Transaction ID: ${transaction.id}")
            appendLine("Date: ${formatDateTime(transaction.timestamp)}")
            appendLine()

            // Charger details
            if (transaction.chargerName != null) {
                appendLine("Charger: ${transaction.chargerName}")
            }
            if (transaction.chargerLocation != null) {
                appendLine("Location: ${transaction.chargerLocation}")
            }
            if (transaction.chargerName != null || transaction.chargerLocation != null) {
                appendLine()
            }

            appendLine(HEADER_SEPARATOR)
            appendLine()

            // Pricing breakdown
            appendLine("Energy Consumed: ${transaction.getFormattedEnergy()}")
            appendLine("Rate: ${transaction.getFormattedRate()}")
            appendLine("Charging Cost: ${transaction.getFormattedChargingCost()}")

            if (transaction.hasPlatformFee()) {
                appendLine("Platform Fee: ${transaction.getFormattedPlatformFee()}")
            }

            appendLine()
            appendLine(centerText("Total Paid: ${transaction.getFormattedTotal()}"))
            appendLine()

            appendLine(HEADER_SEPARATOR)
            appendLine()

            // Payment details
            if (transaction.paymentMethod != null) {
                appendLine("Payment Method: ${formatPaymentMethod(transaction.paymentMethod)}")
            }
            if (transaction.razorpayPaymentId != null) {
                appendLine("Payment ID: ${transaction.razorpayPaymentId}")
            }
            appendLine()

            appendLine(HEADER_SEPARATOR)
            appendLine(centerText("Thank you for using SharaSpot!"))
            appendLine()

            // Legal disclaimer
            appendLine(wrapText(
                "Note: This is a payment receipt " +
                "and not a tax invoice. SharaSpot " +
                "is not registered under GST."
            ))
            appendLine()
            appendLine(FOOTER_SEPARATOR)
        }
    }

    /**
     * Generate receipt HTML for PDF conversion
     */
    fun generateReceiptHtml(transaction: ChargingTransaction): String {
        val dateTime = formatDateTime(transaction.timestamp)

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    max-width: 600px;
                    margin: 40px auto;
                    padding: 20px;
                    background-color: #f5f5f5;
                }
                .receipt {
                    background-color: white;
                    padding: 40px;
                    border-radius: 8px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                }
                .header {
                    text-align: center;
                    margin-bottom: 30px;
                    padding-bottom: 20px;
                    border-bottom: 2px solid #4CAF50;
                }
                .header h1 {
                    color: #4CAF50;
                    margin: 0;
                    font-size: 32px;
                }
                .header .subtitle {
                    color: #666;
                    font-size: 14px;
                    margin-top: 5px;
                }
                .header .not-invoice {
                    color: #f44336;
                    font-size: 12px;
                    font-weight: bold;
                    margin-top: 10px;
                }
                .section {
                    margin: 20px 0;
                }
                .section-title {
                    color: #333;
                    font-size: 14px;
                    font-weight: bold;
                    margin-bottom: 10px;
                    text-transform: uppercase;
                    border-bottom: 1px solid #ddd;
                    padding-bottom: 5px;
                }
                .detail-row {
                    display: flex;
                    justify-content: space-between;
                    padding: 8px 0;
                    border-bottom: 1px solid #f0f0f0;
                }
                .detail-label {
                    color: #666;
                }
                .detail-value {
                    color: #333;
                    font-weight: 500;
                }
                .total-row {
                    display: flex;
                    justify-content: space-between;
                    padding: 15px 0;
                    margin-top: 15px;
                    border-top: 2px solid #4CAF50;
                    font-size: 18px;
                    font-weight: bold;
                }
                .total-label {
                    color: #4CAF50;
                }
                .total-value {
                    color: #4CAF50;
                }
                .footer {
                    margin-top: 30px;
                    padding-top: 20px;
                    border-top: 2px solid #ddd;
                    text-align: center;
                }
                .footer .thank-you {
                    color: #4CAF50;
                    font-size: 18px;
                    font-weight: bold;
                    margin-bottom: 15px;
                }
                .footer .disclaimer {
                    color: #999;
                    font-size: 11px;
                    line-height: 1.6;
                    max-width: 400px;
                    margin: 0 auto;
                }
                .watermark {
                    position: fixed;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%) rotate(-45deg);
                    font-size: 80px;
                    color: rgba(76, 175, 80, 0.05);
                    font-weight: bold;
                    z-index: -1;
                    pointer-events: none;
                }
            </style>
        </head>
        <body>
            <div class="watermark">SharaSpot Receipt</div>
            <div class="receipt">
                <div class="header">
                    <h1>SharaSpot</h1>
                    <div class="subtitle">EV Charging Network</div>
                    <div class="not-invoice">(Not a Tax Invoice)</div>
                </div>

                <div class="section">
                    <div class="detail-row">
                        <span class="detail-label">Transaction ID:</span>
                        <span class="detail-value">${transaction.id}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Date:</span>
                        <span class="detail-value">$dateTime</span>
                    </div>
                    ${if (transaction.chargerName != null) """
                    <div class="detail-row">
                        <span class="detail-label">Charger:</span>
                        <span class="detail-value">${transaction.chargerName}</span>
                    </div>
                    """ else ""}
                    ${if (transaction.chargerLocation != null) """
                    <div class="detail-row">
                        <span class="detail-label">Location:</span>
                        <span class="detail-value">${transaction.chargerLocation}</span>
                    </div>
                    """ else ""}
                </div>

                <div class="section">
                    <div class="section-title">Charging Details</div>
                    <div class="detail-row">
                        <span class="detail-label">Energy Consumed:</span>
                        <span class="detail-value">${transaction.getFormattedEnergy()}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Rate:</span>
                        <span class="detail-value">${transaction.getFormattedRate()}</span>
                    </div>
                </div>

                <div class="section">
                    <div class="section-title">Payment Breakdown</div>
                    <div class="detail-row">
                        <span class="detail-label">Charging Cost:</span>
                        <span class="detail-value">${transaction.getFormattedChargingCost()}</span>
                    </div>
                    ${if (transaction.hasPlatformFee()) """
                    <div class="detail-row">
                        <span class="detail-label">Platform Fee:</span>
                        <span class="detail-value">${transaction.getFormattedPlatformFee()}</span>
                    </div>
                    """ else ""}
                    <div class="total-row">
                        <span class="total-label">Total Paid:</span>
                        <span class="total-value">${transaction.getFormattedTotal()}</span>
                    </div>
                </div>

                ${if (transaction.paymentMethod != null || transaction.razorpayPaymentId != null) """
                <div class="section">
                    <div class="section-title">Payment Information</div>
                    ${if (transaction.paymentMethod != null) """
                    <div class="detail-row">
                        <span class="detail-label">Payment Method:</span>
                        <span class="detail-value">${formatPaymentMethod(transaction.paymentMethod)}</span>
                    </div>
                    """ else ""}
                    ${if (transaction.razorpayPaymentId != null) """
                    <div class="detail-row">
                        <span class="detail-label">Payment ID:</span>
                        <span class="detail-value">${transaction.razorpayPaymentId}</span>
                    </div>
                    """ else ""}
                </div>
                """ else ""}

                <div class="footer">
                    <div class="thank-you">Thank you for using SharaSpot!</div>
                    <div class="disclaimer">
                        This is a payment receipt and not a tax invoice.
                        SharaSpot is not registered under GST.
                        All prices shown are inclusive and final.
                    </div>
                </div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    /**
     * Generate receipt data for Firebase storage
     */
    fun generateReceiptData(transaction: ChargingTransaction): ReceiptData {
        return ReceiptData(
            transactionId = transaction.id,
            userId = transaction.userId,
            receiptText = generateReceiptText(transaction),
            receiptHtml = generateReceiptHtml(transaction),
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Center text within the receipt width
     */
    private fun centerText(text: String): String {
        val padding = (RECEIPT_WIDTH - text.length) / 2
        return " ".repeat(padding.coerceAtLeast(0)) + text
    }

    /**
     * Wrap text to fit within receipt width
     */
    private fun wrapText(text: String, width: Int = RECEIPT_WIDTH): String {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            if ((currentLine + word).length <= width) {
                currentLine += if (currentLine.isEmpty()) word else " $word"
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                }
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines.joinToString("\n")
    }

    /**
     * Format timestamp to readable date and time
     */
    private fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy, hh:mm a", Locale.ENGLISH)
        return sdf.format(Date(timestamp))
    }

    /**
     * Format payment method for display
     */
    private fun formatPaymentMethod(method: String): String {
        return when (method.uppercase()) {
            "UPI" -> "UPI"
            "CARD" -> "Credit/Debit Card"
            "NET_BANKING" -> "Net Banking"
            "WALLET" -> "Digital Wallet"
            else -> method
        }
    }
}

/**
 * Data class for receipt information
 */
data class ReceiptData(
    val transactionId: String,
    val userId: String,
    val receiptText: String,
    val receiptHtml: String,
    val timestamp: Long
)
