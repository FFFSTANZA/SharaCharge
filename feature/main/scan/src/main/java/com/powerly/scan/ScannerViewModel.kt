package com.SharaSpot.scan

import androidx.lifecycle.ViewModel
import com.SharaSpot.core.data.repositories.PowerSourceRepository
import com.powerly.core.model.api.ApiStatus
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ScannerViewModel (
    private val powerSourceRepository: PowerSourceRepository,
) : ViewModel() {
    fun powerSourceDetails(identifier: String) = flow {
        emit(ApiStatus.Loading)
        val it = powerSourceRepository.getPowerSourceByIdentifier(identifier)
        emit(it)
    }
}