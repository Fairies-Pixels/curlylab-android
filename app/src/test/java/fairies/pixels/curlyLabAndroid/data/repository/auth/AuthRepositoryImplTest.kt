package fairies.pixels.curlyLabAndroid.data.repository.auth

import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.GoogleRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.LoginRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.LogoutRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.RegisterRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var authDataStore: AuthDataStore
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        authDataStore = mockk(relaxed = true)
        authRepository = AuthRepositoryImpl(apiService, authDataStore)
    }

    @Test
    fun `успешная регистрация с помощью register`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val accessToken = "header.eyJzdWIiOiJ1c2VyMTIzIn0.signature"
        val authResponse = AuthResponse(accessToken, "refresh", username, email)

        coEvery { apiService.register(any<RegisterRequest>()) } returns Response.success(
            authResponse
        )

        val result = authRepository.register(email, password, username)

        assertTrue(result.isSuccess)
        assertEquals(authResponse, result.getOrNull())
        coVerify { authDataStore.saveAuthData(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `register возвращает ошибку при пустом теле ответа`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"

        coEvery { apiService.register(any<RegisterRequest>()) } returns Response.success(null)

        val result = authRepository.register(email, password, username)

        assertTrue(result.isFailure)
        assertEquals("Пустой ответ от сервера", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register возвращает ошибку при неуспешном ответе`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val errorBody = "{\"error\":\"user exists\"}".toResponseBody(null)

        coEvery { apiService.register(any<RegisterRequest>()) } returns Response.error(
            400, errorBody
        )

        val result = authRepository.register(email, password, username)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("400") == true)
    }

    @Test
    fun `register возвращает ошибку при исключении`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"

        coEvery { apiService.register(any<RegisterRequest>()) } throws Exception("Network error")

        val result = authRepository.register(email, password, username)

        assertTrue(result.isFailure)
        assertEquals("Ошибка сети: Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `успешный вход с помощью login`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val accessToken = "header.eyJzdWIiOiJ1c2VyMTIzIn0.signature"
        val authResponse = AuthResponse(accessToken, "refresh", "testuser", email)

        coEvery { apiService.login(any<LoginRequest>()) } returns Response.success(authResponse)

        val result = authRepository.login(email, password)

        assertTrue(result.isSuccess)
        assertEquals(authResponse, result.getOrNull())
        coVerify { authDataStore.saveAuthData(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `login возвращает ошибку при пустом теле ответа`() = runTest {
        val email = "test@example.com"
        val password = "password123"

        coEvery { apiService.login(any<LoginRequest>()) } returns Response.success(null)

        val result = authRepository.login(email, password)

        assertTrue(result.isFailure)
        assertEquals("Пустой ответ от сервера", result.exceptionOrNull()?.message)
    }

    @Test
    fun `loginWithGoogle возвращает успех при успешном входе`() = runTest {
        val idToken = "google-token"
        val accessToken = "header.eyJzdWIiOiJ1c2VyMTIzIn0.signature"
        val authResponse = AuthResponse(accessToken, "refresh", "testuser", "test@example.com")

        coEvery { apiService.googleLogin(any<GoogleRequest>()) } returns Response.success(
            authResponse
        )

        val result = authRepository.loginWithGoogle(idToken)

        assertTrue(result.isSuccess)
        assertEquals(authResponse, result.getOrNull())
        coVerify { authDataStore.saveAuthData(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `isUserLoggedIn возвращает статус из AuthDataStore`() = runTest {
        coEvery { authDataStore.isLoggedIn } returns flowOf(true)

        val result = authRepository.isUserLoggedIn()

        assertTrue(result)
    }

    @Test
    fun `logout очищает данные и вызывает logout сервиса`() = runTest {
        val refreshToken = "refresh123"
        coEvery { authDataStore.getRefreshTokenBlocking() } returns refreshToken
        coEvery { apiService.logout(any<LogoutRequest>()) } returns Response.success(Unit)

        authRepository.logout()

        coVerify { apiService.logout(LogoutRequest(refreshToken)) }
        coVerify { authDataStore.clearAuthData() }
    }

    @Test
    fun `logout очищает данные даже при ошибке сервиса`() = runTest {
        coEvery { authDataStore.getRefreshTokenBlocking() } returns "refresh123"
        coEvery { apiService.logout(any<LogoutRequest>()) } throws Exception("Network error")

        authRepository.logout()

        coVerify { authDataStore.clearAuthData() }
    }

    @Test
    fun `getAccessToken возвращает токен из AuthDataStore`() = runTest {
        val token = "access123"
        coEvery { authDataStore.getAccessToken() } returns token

        val result = authRepository.getAccessToken()

        assertEquals(token, result)
    }

    @Test
    fun `getUserId возвращает userId из AuthDataStore`() = runTest {
        val userId = "user123"
        coEvery { authDataStore.getUserId() } returns userId

        val result = authRepository.getUserId()

        assertEquals(userId, result)
    }

    @Test
    fun `deleteAccount успешно удаляет аккаунт`() = runTest {
        val userId = "user123"
        val refreshToken = "refresh123"

        coEvery { authDataStore.getRefreshTokenBlocking() } returns refreshToken
        coEvery { apiService.logout(any<LogoutRequest>()) } returns Response.success(Unit)
        coEvery { apiService.deleteUser(userId) } returns Unit

        authRepository.deleteAccount(userId)

        coVerify { apiService.logout(LogoutRequest(refreshToken)) }
        coVerify { apiService.deleteUser(userId) }
        coVerify { authDataStore.clearAuthData() }
    }

    @Test
    fun `deleteAccount выбрасывает исключение при ошибке`() = runTest {
        val userId = "user123"
        val refreshToken = "refresh123"
        val errorMessage = "Network error"

        coEvery { authDataStore.getRefreshTokenBlocking() } returns refreshToken
        coEvery { apiService.logout(any<LogoutRequest>()) } returns Response.success(Unit)
        coEvery { apiService.deleteUser(userId) } throws Exception(errorMessage)

        try {
            authRepository.deleteAccount(userId)
            throw AssertionError("Ожидаемое исключение не было выброшено")
        } catch (e: Exception) {
            assertEquals("Ошибка при удалении аккаунта: $errorMessage", e.message)
        }

        coVerify { apiService.logout(LogoutRequest(refreshToken)) }
        coVerify { apiService.deleteUser(userId) }
        coVerify { authDataStore.clearAuthData() }
    }

    @Test
    fun `deleteAccount вызывает logout, даже если refresh токена нет`() = runTest {
        val userId = "user123"

        coEvery { authDataStore.getRefreshTokenBlocking() } returns null
        coEvery { apiService.deleteUser(userId) } returns Unit

        authRepository.deleteAccount(userId)

        coVerify(exactly = 0) { apiService.logout(any<LogoutRequest>()) }
        coVerify { apiService.deleteUser(userId) }
        coVerify { authDataStore.clearAuthData() }
    }
}