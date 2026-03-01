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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var signInUseCase: SignInUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        signInUseCase = SignInUseCase(authRepository)
    }

    @Test
    fun `успешный вход с валидными данными`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val expectedResponse = AuthResponse("access", "refresh", "user", email)

        coEvery { authRepository.login(email, password) } returns Result.success(expectedResponse)

        val result = signInUseCase(email, password)

        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
        coVerify { authRepository.login(email, password) }
    }

    @Test
    fun `ошибка при невалидном email`() = runTest {
        val invalidEmails = listOf(
            "invalid",
            "test@",
            "@example.com",
            "test.example.com",
            "test@.com"
        )

        invalidEmails.forEach { email ->
            val result = signInUseCase(email, "password123")
            assertTrue(result.isFailure)
            assertEquals(AuthErrors.INVALID_EMAIL, result.exceptionOrNull()?.message)
            coVerify(exactly = 0) { authRepository.login(any(), any()) }
        }
    }

    @Test
    fun `ошибка при пустом пароле`() = runTest {
        val email = "test@example.com"
        val password = ""

        val result = signInUseCase(email, password)

        assertTrue(result.isFailure)
        assertEquals(AuthErrors.PASSWORD_EMPTY, result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `ошибка при неудачном входе`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Invalid credentials"

        coEvery { authRepository.login(email, password) } returns Result.failure(
            Exception(
                errorMessage
            )
        )

        val result = signInUseCase(email, password)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}