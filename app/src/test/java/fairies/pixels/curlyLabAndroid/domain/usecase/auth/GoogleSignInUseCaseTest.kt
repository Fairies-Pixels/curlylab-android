package fairies.pixels.curlyLabAndroid.domain.usecase.auth

import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GoogleSignInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var googleSignInUseCase: GoogleSignInUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        googleSignInUseCase = GoogleSignInUseCase(authRepository)
    }

    @Test
    fun `успешный вход с валидным токеном`() = runTest {
        val token = "valid-google-token"
        val expectedResponse = AuthResponse("access", "refresh", "user", "email")

        coEvery { authRepository.loginWithGoogle(token) } returns Result.success(expectedResponse)

        val result = googleSignInUseCase(token)

        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
        coVerify { authRepository.loginWithGoogle(token) }
    }

    @Test
    fun `ошибка при пустом токене`() = runTest {
        val token = ""

        val result = googleSignInUseCase(token)

        assertTrue(result.isFailure)
        assertEquals(AuthErrors.GOOGLE_TOKEN_EMPTY, result.exceptionOrNull()?.message)
    }

    @Test
    fun `ошибка при пробельном токене`() = runTest {
        val token = "   "

        val result = googleSignInUseCase(token)

        assertTrue(result.isFailure)
        assertEquals(AuthErrors.GOOGLE_TOKEN_EMPTY, result.exceptionOrNull()?.message)
    }

    @Test
    fun `ошибка при неудачном входе`() = runTest {
        val token = "valid-token"
        val errorMessage = "Invalid token"

        coEvery { authRepository.loginWithGoogle(token) } returns Result.failure(
            Exception(
                errorMessage
            )
        )

        val result = googleSignInUseCase(token)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}