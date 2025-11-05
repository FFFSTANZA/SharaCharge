package com.SharaSpot.core.data.repositories

import com.SharaSpot.core.data.model.BalanceRefillStatus
import com.SharaSpot.core.model.api.ApiStatus
import com.SharaSpot.core.model.payment.BalanceItem
import com.SharaSpot.core.model.payment.PaymentCard
import com.SharaSpot.core.model.payment.PaymentStatus
import com.SharaSpot.core.model.payment.PaymentTransaction
import com.SharaSpot.core.model.payment.Wallet
import com.SharaSpot.core.model.util.Message

interface PaymentRepository {

    /**
     * Retrieves a list of cards.
     *
     * @return  [ApiStatus] results containing the list of cards.
     */
    suspend fun cardList(): ApiStatus<List<PaymentCard>>

    /**
     * Adds a card.
     *
     * @param token The card token.
     * @return  [ApiStatus] results indicating the success or failure of adding the card.
     */
    suspend fun addCard(token: String): ApiStatus<Boolean>

    /**
     * Sets a card as the default.
     *
     * @param id The ID of the card to set as default.
     * @return  [ApiStatus] results indicating the success or failure of setting the default card.
     */
    suspend fun setDefaultCard(id: String): ApiStatus<Boolean>

    /**
     * Deletes a card.
     *
     * @param id The ID of the card to delete.
     * @return  [ApiStatus] results indicating the success or failure of deleting the card.
     */
    suspend fun deleteCard(id: String): ApiStatus<Boolean>

    /**
     * Refills the balance.
     *
     * @param offerId ID of the balance offer selected by the user.
     * @param paymentMethodId method ID used for the transaction.
     * @return  [com.SharaSpot.core.data.model.BalanceRefillStatus] results indicating the status of the refill.
     */
    suspend fun refillBalance(offerId: Int, paymentMethodId: String): BalanceRefillStatus

    /**
     * Retrieves balance items.
     *
     * @param countryId The ID of the country.
     * @return  [ApiStatus] results containing the balance items.
     */
    suspend fun getBalanceItems(countryId: Int?): ApiStatus<List<BalanceItem>>

    /**
     * Retrieves a list of wallets.
     * @return  [ApiStatus] results containing wallet data.
     */
    suspend fun walletList(): ApiStatus<List<Wallet>>

    /**
     * Initiates a wallet payout.
     * @return  [ApiStatus] results indicating payout status.
     */
    suspend fun walletPayout(): ApiStatus<Message>

    // Razorpay Integration Methods

    /**
     * Creates a Razorpay order on the backend
     *
     * @param amount The amount for the order
     * @param currency The currency (default: INR)
     * @return [ApiStatus] results containing the order ID
     */
    suspend fun createRazorpayOrder(amount: Double, currency: String = "INR"): ApiStatus<String>

    /**
     * Verifies the payment signature with backend
     *
     * @param orderId The Razorpay order ID
     * @param paymentId The Razorpay payment ID
     * @param signature The payment signature
     * @return [ApiStatus] results indicating verification success
     */
    suspend fun verifyPayment(orderId: String, paymentId: String, signature: String): ApiStatus<Boolean>

    /**
     * Gets the payment status for a transaction
     *
     * @param orderId The order ID
     * @return [ApiStatus] results containing the payment status
     */
    suspend fun getPaymentStatus(orderId: String): ApiStatus<PaymentStatus>

    /**
     * Saves payment transaction reference in Firebase Firestore
     * ONLY stores: orderId, razorpayPaymentId, amount, status, timestamp
     * All sensitive payment data is stored by Razorpay
     *
     * @param transaction The payment transaction to save
     * @return [ApiStatus] results indicating success or failure
     */
    suspend fun savePaymentTransaction(transaction: PaymentTransaction): ApiStatus<Boolean>

    /**
     * Gets payment transaction history from Firebase Firestore
     *
     * @param userId The user ID
     * @return [ApiStatus] results containing list of transactions
     */
    suspend fun getPaymentTransactions(userId: String): ApiStatus<List<PaymentTransaction>>
}


