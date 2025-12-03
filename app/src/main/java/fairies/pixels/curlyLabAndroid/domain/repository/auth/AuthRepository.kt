package fairies.pixels.curlyLabAndroid.domain.repository.auth

import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse

interface AuthRepository {
    suspend fun register(email: String, password: String, username: String): Result<AuthResponse>
    suspend fun login(email: String, password: String): Result<AuthResponse>
    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun logout()
    suspend fun deleteAccount(userId: String)
    suspend fun getAccessToken(): String?
    suspend fun getUserId(): String?
}