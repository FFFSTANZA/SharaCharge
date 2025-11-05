package com.SharaSpot.payment.balance

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.SharaSpot.core.analytics.EVENTS
import com.SharaSpot.core.analytics.EventsManager
import com.SharaSpot.core.data.model.BalanceRefillStatus
import com.SharaSpot.core.data.repositories.PaymentRepository
import com.SharaSpot.core.data.repositories.UserRepository
import com.SharaSpot.core.model.api.ApiStatus
import com.SharaSpot.core.model.payment.BalanceItem
import com.SharaSpot.core.model.payment.PaymentRedirect
import com.SharaSpot.core.model.payment.PaymentCard
import com.SharaSpot.core.model.util.Message
import com.SharaSpot.lib.managers.CountryManager
import com.SharaSpot.lib.managers.StorageManager
import com.SharaSpot.payment.PaymentManager
import com.SharaSpot.resources.R
import com.SharaSpot.ui.dialogs.alert.initAlertDialogState
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@KoinViewModel
class BalanceViewModel (
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val eventsManager: EventsManager,
    private val paymentManager: PaymentManager,
    private val countryManager: CountryManager,
    private val storageManager: StorageManager,
) : ViewModel() {
    val paymentFailureDialog = initAlertDialogState()
    val balanceItem = mutableStateOf(BalanceItem())
    val userBalance: Double get() = storageManager.userDetails?.balance ?: 0.0
    val userCurrency: String get() = storageManager.userDetails?.currency.orEmpty()

    fun setBalanceItem(item: BalanceItem) {
        this.balanceItem.value = item
    }

    val balanceItems: Flow<ApiStatus<List<BalanceItem>>> = flow {
        emit(ApiStatus.Loading)
        val countryId = countryManager.detectCountry()?.id
        val result = paymentRepository.getBalanceItems(countryId)
        emit(result)
    }

    suspend fun refillBalance(paymentCard: PaymentCard): Boolean {
        log(EVENTS.BALANCE_REFILL)
        val it = paymentRepository.refillBalance(
            offerId = balanceItem.value.id,
            paymentMethodId = paymentCard.id
        )
        when (it) {
            is BalanceRefillStatus.Error -> {
                paymentFailureDialog.show(it.msg.msg)
                return false
            }

            is BalanceRefillStatus.Authenticate -> {
                // Payment authentication removed - would need to integrate new payment provider
                paymentFailureDialog.show("Payment authentication not available")
                return false
            }

            is BalanceRefillStatus.Success -> {
                return true
            }

            else -> {
                return false
            }
        }
    }

    suspend fun updateUserDetails() {
        val result = userRepository.getUserDetails()
        if (result is ApiStatus.Success) {
            storageManager.userDetails = result.data
            delay(500)
        }
    }


    fun log(event: String) {
        eventsManager.log(event)
    }

    companion object {
        private const val TAG = "BalanceViewModel"
    }
}