package com.SharaSpot.core.data.repoImpl

import com.SharaSpot.core.data.repositories.LoginSocialRepository
import com.SharaSpot.core.model.api.ApiStatus
import com.SharaSpot.core.model.user.SocialLoginBody
import com.SharaSpot.core.network.RemoteDataSource
import com.SharaSpot.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException

@Single
class LoginSocialRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : LoginSocialRepository {

    override suspend fun googleLogin(request: SocialLoginBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.socialGoogleLogin(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun huaweiLogin(request: SocialLoginBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.socialHuaweiLogin(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }
}
