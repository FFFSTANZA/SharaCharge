package com.SharaSpot.core.data.repoImpl

import android.util.Log
import com.SharaSpot.core.data.model.ReviewOptionsStatus
import com.SharaSpot.core.data.repositories.FeedbackRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.ReviewBody
import com.SharaSpot.core.network.RemoteDataSource
import com.SharaSpot.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException

@Single
class FeedbackRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : FeedbackRepository {

    companion object {
        private const val TAG = "FeedbackRepositoryImpl"
    }

    override val reviewOptions = flow {
        try {
            val response = remoteDataSource.reviewOptions()
            emit(
                if (response.hasData) ReviewOptionsStatus.Success(response.getData!!)
                else ReviewOptionsStatus.Error(response.getMessage())
            )
        } catch (e: HttpException) {
            emit(ReviewOptionsStatus.Error(e.asErrorMessage))
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}", e)
            emit(ReviewOptionsStatus.Error(e.asErrorMessage))
        }
    }

    override suspend fun reviewAdd(
        orderId: String,
        rating: Double,
        msg: String
    ) = withContext(ioDispatcher) {
        try {
            val body = ReviewBody(rating = rating, msg = msg)
            val response = remoteDataSource.reviewAdd(
                orderId = orderId,
                body = body
            )
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}", e)
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun reviewSkip(orderId: String) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.reviewSkip(orderId)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}", e)
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun reviewsList() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.reviewPending(limit = 1)
            if (response.hasData) ApiStatus.Success(response.getData?.getOrNull(0))
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }
}