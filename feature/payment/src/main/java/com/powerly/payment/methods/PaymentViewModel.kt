package com.SharaSpot.payment.methods

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SharaSpot.core.data.repositories.PaymentRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.PaymentCard
import com.powerly.core.model.util.asErrorMessage
import com.SharaSpot.payment.PaymentManager
import com.SharaSpot.ui.dialogs.loading.initScreenState
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


@KoinViewModel
class PaymentViewModel (
    private val paymentRepository: PaymentRepository,
    private val paymentManager: PaymentManager
) : ViewModel() {

    val screenState = initScreenState()
    val paymentMethods = mutableStateListOf<PaymentCard>()
    val defaultPaymentMethod = mutableStateOf<PaymentCard?>(null)

    suspend fun loadPaymentMethods(forceUpdate: Boolean = false) {
        if (paymentMethods.isEmpty() || forceUpdate) {
            val it = paymentRepository.cardList()
            when (it) {
                is ApiStatus.Success -> {
                    paymentMethods.clear()
                    paymentMethods.addAll(it.data)
                    defaultPaymentMethod.value =
                        paymentMethods.firstOrNull { c -> c.default }
                            ?: paymentMethods.firstOrNull()
                }

                else -> {}
            }
        }
    }

    suspend fun setDefaultCard(card: PaymentCard): Boolean {
        if (card.default) return true
        val result = paymentRepository.setDefaultCard(card.id)
        when (result) {
            is ApiStatus.Error -> screenState.showMessage(result.msg)
            is ApiStatus.Success -> {
                loadPaymentMethods(forceUpdate = true)
                defaultPaymentMethod.value = card
                return true
            }

            else -> {}
        }
        return false
    }

    // Placeholder method - payment integration removed
    fun addCard(
        paymentDetails: Map<String, Any>,
        onDismiss: () -> Unit
    ) {
        screenState.loading = false
        screenState.showMessage("Payment integration not available") {
            onDismiss()
        }
    }

    suspend fun deleteCard(cardId: String): Boolean {
        val result = paymentRepository.deleteCard(cardId)
        when (result) {
            is ApiStatus.Error -> screenState.showMessage(result.msg)
            is ApiStatus.Success -> {
                loadPaymentMethods(forceUpdate = true)
                return true
            }

            else -> {}
        }
        return false
    }

    // Placeholder - card scanning removed
    private val _scannedCard = MutableSharedFlow<String>(replay = 1)
    val scannedCard: Flow<String> = _scannedCard.asSharedFlow()

    fun clearScannedCard() {
        _scannedCard.resetReplayCache()
    }

    fun scanCard() {
        // Card scanning functionality removed
    }

    companion object {
        private const val TAG = "PaymentViewModel"
    }
}