package fairies.pixels.curlyLabAndroid.data.repository.auth

import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.LoginRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.RegisterRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import fairies.pixels.curlyLabAndroid.utils.decoders.JwtDecoder
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: ApiService,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<AuthResponse> {
        return try {
            val response = authApiService.register(RegisterRequest(email, password, username))
            if (response.isSuccessful) {
                val authResponse =
                    response.body() ?: return Result.failure(Exception("Empty response body"))

                val userId = JwtDecoder.decodeUserId(authResponse.access)
                    ?: return Result.failure(Exception("Failed to decode user ID from token"))

                authDataStore.saveAuthData(
                    isLoggedIn = true,
                    accessToken = authResponse.access,
                    refreshToken = authResponse.refresh,
                    userId = userId,
                    email = email
                )
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Registration failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val authResponse =
                    response.body() ?: return Result.failure(Exception("Empty response body"))

                val userId = JwtDecoder.decodeUserId(authResponse.access)
                    ?: return Result.failure(Exception("Failed to decode user ID from token"))

                authDataStore.saveAuthData(
                    isLoggedIn = true,
                    accessToken = authResponse.access,
                    refreshToken = authResponse.refresh,
                    userId = userId,
                    email = email
                )
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }


    override suspend fun isUserLoggedIn(): Boolean {
        return authDataStore.isLoggedIn.first()
    }

    override suspend fun logout() {
        authDataStore.clearAuthData()
    }

    override suspend fun getAccessToken(): String? {
        return authDataStore.getAccessToken()
    }

    override suspend fun getUserId(): String? {
        return authDataStore.getUserId()
    }
}