package com.SharaSpot.core.data.repoImpl

import com.SharaSpot.core.data.model.BalanceRefillStatus
import com.SharaSpot.core.data.repositories.PaymentRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.AddCardBody
import com.powerly.core.model.payment.BalanceRefillBody
import com.powerly.core.model.payment.PaymentStatus
import com.powerly.core.model.payment.PaymentTransaction
import com.SharaSpot.core.network.RemoteDataSource
import com.SharaSpot.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
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
                // TODO: Call backend API to create Razorpay order
                // val response = remoteDataSource.createRazorpayOrder(amount, currency)
                // For now, return a mock order ID
                val orderId = "order_${UUID.randomUUID().toString().replace("-", "")}"
                ApiStatus.Success(orderId)
            } catch (e: HttpException) {
                ApiStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun verifyPayment(orderId: String, paymentId: String, signature: String) =
        withContext(ioDispatcher) {
            try {
                // TODO: Call backend API to verify payment signature
                // val response = remoteDataSource.verifyRazorpayPayment(orderId, paymentId, signature)
                // For now, return success (signature verification should be done on backend)
                ApiStatus.Success(true)
            } catch (e: HttpException) {
                ApiStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun getPaymentStatus(orderId: String) =
        withContext(ioDispatcher) {
            try {
                // TODO: Call backend API to get payment status from Razorpay
                // val response = remoteDataSource.getRazorpayPaymentStatus(orderId)
                // For now, return pending status
                ApiStatus.Success(PaymentStatus.PENDING)
            } catch (e: HttpException) {
                ApiStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun savePaymentTransaction(transaction: PaymentTransaction) =
        withContext(ioDispatcher) {
            try {
                // TODO: Save transaction to Firebase Firestore
                // Collection: "payment_transactions"
                // Fields: orderId, razorpayPaymentId, amount, status, timestamp
                // Note: NO sensitive payment data should be stored

                // Example Firestore save:
                // val firestore = FirebaseFirestore.getInstance()
                // firestore.collection("payment_transactions")
                //     .document(transaction.id)
                //     .set(mapOf(
                //         "orderId" to transaction.orderId,
                //         "razorpayPaymentId" to transaction.razorpayPaymentId,
                //         "amount" to transaction.amount,
                //         "currency" to transaction.currency,
                //         "status" to transaction.status.name,
                //         "timestamp" to transaction.timestamp,
                //         "paymentMethod" to transaction.paymentMethod
                //     ))
                //     .await()

                ApiStatus.Success(true)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun getPaymentTransactions(userId: String) =
        withContext(ioDispatcher) {
            try {
                // TODO: Retrieve transactions from Firebase Firestore
                // Example Firestore query:
                // val firestore = FirebaseFirestore.getInstance()
                // val snapshot = firestore.collection("payment_transactions")
                //     .whereEqualTo("userId", userId)
                //     .orderBy("timestamp", Query.Direction.DESCENDING)
                //     .get()
                //     .await()

                // For now, return empty list
                ApiStatus.Success(emptyList())
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }
}