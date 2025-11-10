package com.SharaSpot.core.data.repoImpl

import com.SharaSpot.core.data.model.BalanceRefillStatus
import com.SharaSpot.core.data.repositories.PaymentRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.AddCardBody
import com.powerly.core.model.payment.BalanceRefillBody
import com.powerly.core.model.payment.CreateRazorpayOrderRequest
import com.powerly.core.model.payment.PaymentStatus
import com.powerly.core.model.payment.PaymentTransaction
import com.powerly.core.model.payment.VerifyRazorpayPaymentRequest
import com.SharaSpot.core.network.RemoteDataSource
import com.SharaSpot.core.network.asErrorMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.UUID


@Single
class PaymentRepositoryImpl (
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    companion object {
        private const val COLLECTION_PAYMENT_TRANSACTIONS = "payment_transactions"
    }

    override suspend fun cardList() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.cardList()
            if (response.hasData) ApiStatus.Success(response.getData)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun addCard(token: String) = withContext(ioDispatcher) {
        try {
            val request = AddCardBody(token)
            val response = remoteDataSource.cardAdd(request)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun setDefaultCard(id: String) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.cardDefault(id)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun deleteCard(id: String) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.cardDelete(id)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun refillBalance(offerId: Int, paymentMethodId: String) =
        withContext(ioDispatcher) {
            try {
                val body = BalanceRefillBody(offerId, paymentMethodId)
                val response = remoteDataSource.refillBalance(body)
                if (response.hasData) {
                    val result = response.getData
                    val balance = result.newBalance
                    val message = response.getMessage()
                    if (result.hasRedirectUrl)
                        BalanceRefillStatus.Authenticate(result.redirectUrl, message)
                    else
                        BalanceRefillStatus.Success(balance, message)
                } else BalanceRefillStatus.Error(response.getMessage())
            } catch (e: HttpException) {
                BalanceRefillStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                BalanceRefillStatus.Error(e.asErrorMessage)
            }
        }


    override suspend fun getBalanceItems(countryId: Int?) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getBalanceItems(countryId)
            if (response.isSuccess) ApiStatus.Success(response.getData)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun walletList() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.walletList()
            if (response.hasData) ApiStatus.Success(response.getData)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun walletPayout() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.walletPayout()
            if (response.isSuccess) ApiStatus.Success(response.getMessage())
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    // Razorpay Integration Implementation

    override suspend fun createRazorpayOrder(amount: Double, currency: String) =
        withContext(ioDispatcher) {
            try {
                // Create Razorpay order on backend
                val request = CreateRazorpayOrderRequest(
                    amount = amount,
                    currency = currency,
                    receipt = "rcpt_${System.currentTimeMillis()}"
                )
                val response = remoteDataSource.createRazorpayOrder(request)

                if (response.hasData) {
                    val orderData = response.getData
                    ApiStatus.Success(orderData.orderId)
                } else {
                    ApiStatus.Error(response.getMessage())
                }
            } catch (e: HttpException) {
                ApiStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun verifyPayment(orderId: String, paymentId: String, signature: String) =
        withContext(ioDispatcher) {
            try {
                // Verify payment signature on backend (SECURITY: Must be done server-side)
                val request = VerifyRazorpayPaymentRequest(
                    orderId = orderId,
                    paymentId = paymentId,
                    signature = signature
                )
                val response = remoteDataSource.verifyRazorpayPayment(request)

                if (response.hasData) {
                    val verificationResult = response.getData
                    if (verificationResult.verified) {
                        ApiStatus.Success(true)
                    } else {
                        ApiStatus.Error(verificationResult.message ?: "Payment verification failed")
                    }
                } else {
                    ApiStatus.Error(response.getMessage())
                }
            } catch (e: HttpException) {
                ApiStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun getPaymentStatus(orderId: String) =
        withContext(ioDispatcher) {
            try {
                // Get payment status from backend (queries Razorpay API)
                val response = remoteDataSource.getRazorpayPaymentStatus(orderId)

                if (response.hasData) {
                    val statusData = response.getData
                    ApiStatus.Success(statusData.status)
                } else {
                    ApiStatus.Error(response.getMessage())
                }
            } catch (e: HttpException) {
                ApiStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun savePaymentTransaction(transaction: PaymentTransaction) =
        withContext(ioDispatcher) {
            try {
                // Save transaction to Firebase Firestore
                // Collection: "payment_transactions"
                // Note: NO sensitive payment data should be stored (only references)
                val transactionData = hashMapOf(
                    "id" to transaction.id,
                    "orderId" to transaction.orderId,
                    "razorpayPaymentId" to transaction.razorpayPaymentId,
                    "amount" to transaction.amount,
                    "currency" to transaction.currency,
                    "status" to transaction.status.name,
                    "timestamp" to transaction.timestamp,
                    "paymentMethod" to transaction.paymentMethod
                )

                firestore.collection(COLLECTION_PAYMENT_TRANSACTIONS)
                    .document(transaction.id)
                    .set(transactionData)
                    .await()

                ApiStatus.Success(true)
            } catch (e: Exception) {
                ApiStatus.Error(e.message ?: "Failed to save transaction")
            }
        }

    override suspend fun getPaymentTransactions(userId: String) =
        withContext(ioDispatcher) {
            try {
                // Retrieve transactions from Firebase Firestore
                // Note: This implementation retrieves all transactions
                // In production, you should filter by userId if stored
                val snapshot = firestore.collection(COLLECTION_PAYMENT_TRANSACTIONS)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val transactions = snapshot.documents.mapNotNull { document ->
                    try {
                        PaymentTransaction(
                            id = document.getString("id") ?: return@mapNotNull null,
                            orderId = document.getString("orderId") ?: return@mapNotNull null,
                            razorpayPaymentId = document.getString("razorpayPaymentId"),
                            amount = document.getDouble("amount") ?: 0.0,
                            currency = document.getString("currency") ?: "INR",
                            status = PaymentStatus.valueOf(
                                document.getString("status") ?: "PENDING"
                            ),
                            timestamp = document.getLong("timestamp") ?: 0L,
                            paymentMethod = document.getString("paymentMethod")
                        )
                    } catch (e: Exception) {
                        null // Skip malformed documents
                    }
                }

                ApiStatus.Success(transactions)
            } catch (e: Exception) {
                ApiStatus.Error(e.message ?: "Failed to retrieve transactions")
            }
        }
}