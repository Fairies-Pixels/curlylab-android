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
class SignUpUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        signUpUseCase = SignUpUseCase(authRepository)
    }

    @Test
    fun `успешная регистрация с валидными данными`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val expectedResponse = AuthResponse("access", "refresh", username, email)

        coEvery { authRepository.register(email, password, username) } returns Result.success(
            expectedResponse
        )

        val result = signUpUseCase(email, password, username)

        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
        coVerify { authRepository.register(email, password, username) }
    }

    @Test
    fun `ошибка при невалидном email`() = runTest {
        val invalidEmails = listOf(
            "invalid",
            "test@",
            "@example.com",
            "test.example.com"
        )

        invalidEmails.forEach { email ->
            val result = signUpUseCase(email, "password123", "testuser")
            assertTrue(result.isFailure)
            assertEquals(AuthErrors.INVALID_EMAIL, result.exceptionOrNull()?.message)
            coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
        }
    }

    @Test
    fun `ошибка при коротком пароле`() = runTest {
        val email = "test@example.com"
        val password = "12345"
        val username = "testuser"

        val result = signUpUseCase(email, password, username)

        assertTrue(result.isFailure)
        assertEquals(AuthErrors.PASSWORD_TOO_SHORT, result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
    }

    @Test
    fun `ошибка при коротком имени`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "a"

        val result = signUpUseCase(email, password, username)

        assertTrue(result.isFailure)
        assertEquals(AuthErrors.USERNAME_TOO_SHORT, result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
    }

    @Test
    fun `ошибка при длинном имени`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "a".repeat(21)

        val result = signUpUseCase(email, password, username)

        assertTrue(result.isFailure)
        assertEquals(AuthErrors.USERNAME_TOO_LONG, result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
    }

    @Test
    fun `ошибка при неудачной регистрации`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val username = "testuser"
        val errorMessage = "User already exists"

        coEvery { authRepository.register(email, password, username) } returns Result.failure(
            Exception(errorMessage)
        )

        val result = signUpUseCase(email, password, username)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}