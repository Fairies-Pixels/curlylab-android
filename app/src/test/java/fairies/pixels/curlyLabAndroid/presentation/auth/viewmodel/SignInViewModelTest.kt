package fairies.pixels.curlyLabAndroid.presentation.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.AuthErrors
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.GoogleSignInUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.SignInUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.ValidatePasswordUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.ValidationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var signInUseCase: SignInUseCase
    private lateinit var googleSignInUseCase: GoogleSignInUseCase
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private lateinit var viewModel: SignInViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        signInUseCase = mockk()
        googleSignInUseCase = mockk()
        validatePasswordUseCase = mockk()

        every { validatePasswordUseCase.validatePasswordStrength(any()) } returns ValidationResult(
            true
        )

        viewModel = SignInViewModel(signInUseCase, googleSignInUseCase, validatePasswordUseCase)
    }

    @Test
    fun `обновление email изменяет значение и очищает ошибку`() {
        viewModel.updateEmail("test@example.com")

        assertEquals("test@example.com", viewModel.email.value)
        assertNull(viewModel.errorMessage.value)

        viewModel.updateEmail("new@example.com")
        assertEquals("new@example.com", viewModel.email.value)
    }

    @Test
    fun `обновление пароля изменяет значение и очищает ошибку`() {
        val validationResult = ValidationResult(true)
        every { validatePasswordUseCase.validatePasswordStrength("password123") } returns validationResult

        viewModel.updatePassword("password123")

        assertEquals("password123", viewModel.password.value)
        assertNull(viewModel.errorMessage.value)
        verify { validatePasswordUseCase.validatePasswordStrength("password123") }
    }

    @Test
    fun `обновление пустого пароля не вызывает валидацию`() {
        viewModel.updatePassword("")

        assertEquals("", viewModel.password.value)
        verify(exactly = 0) { validatePasswordUseCase.validatePasswordStrength(any()) }
    }

    @Test
    fun `обновление пароля с ошибкой валидации устанавливает ошибку пароля`() {
        val validationResult = ValidationResult(false, "Ошибка пароля")
        every { validatePasswordUseCase.validatePasswordStrength("weak") } returns validationResult

        viewModel.updatePassword("weak")

        assertEquals("weak", viewModel.password.value)
        assertEquals("Ошибка пароля", viewModel.passwordError.value)
    }

    @Test
    fun `signIn с пустыми полями возвращает ошибку`() {
        viewModel.updateEmail("")
        viewModel.updatePassword("")

        var successCalled = false
        viewModel.signIn { successCalled = true }

        assertEquals(AuthErrors.FIELDS_REQUIRED, viewModel.errorMessage.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signIn с невалидным паролем возвращает ошибку`() {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("12345")

        val validationResult = ValidationResult(false, AuthErrors.PASSWORD_TOO_SHORT)
        every { validatePasswordUseCase.validatePasswordStrength("12345") } returns validationResult

        var successCalled = false
        viewModel.signIn { successCalled = true }

        assertEquals(AuthErrors.PASSWORD_TOO_SHORT, viewModel.errorMessage.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signIn с успешным входом вызывает onSuccess`() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")

        val validationResult = ValidationResult(true)
        every { validatePasswordUseCase.validatePasswordStrength("password123") } returns validationResult

        coEvery { signInUseCase("test@example.com", "password123") } returns Result.success(
            AuthResponse("access", "refresh", "user", "email")
        )

        var successCalled = false
        viewModel.signIn { successCalled = true }

        assertNull(viewModel.errorMessage.value)
        assertFalse(viewModel.isEmailLoading.value)
        assertTrue(successCalled)
        coVerify { signInUseCase("test@example.com", "password123") }
    }

    @Test
    fun `signIn с ошибкой входа устанавливает сообщение об ошибке`() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")

        val validationResult = ValidationResult(true)
        every { validatePasswordUseCase.validatePasswordStrength("password123") } returns validationResult

        coEvery { signInUseCase("test@example.com", "password123") } returns Result.failure(
            Exception("Invalid credentials")
        )

        var successCalled = false
        viewModel.signIn { successCalled = true }

        assertEquals("Invalid credentials", viewModel.errorMessage.value)
        assertFalse(viewModel.isEmailLoading.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signInWithGoogle с валидным токеном вызывает onSuccess`() = runTest {
        val token = "google-token"

        coEvery { googleSignInUseCase(token) } returns Result.success(
            AuthResponse("access", "refresh", "user", "email")
        )

        var successCalled = false
        viewModel.signInWithGoogle(token) { successCalled = true }

        assertNull(viewModel.errorMessage.value)
        assertFalse(viewModel.isGoogleLoading.value)
        assertTrue(successCalled)
        coVerify { googleSignInUseCase(token) }
    }

    @Test
    fun `signInWithGoogle с ошибкой устанавливает сообщение об ошибке`() = runTest {
        val token = "google-token"

        coEvery { googleSignInUseCase(token) } returns Result.failure(Exception("Invalid token"))

        var successCalled = false
        viewModel.signInWithGoogle(token) { successCalled = true }

        assertEquals("Invalid token", viewModel.errorMessage.value)
        assertFalse(viewModel.isGoogleLoading.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signInWithGoogle с исключением устанавливает сообщение об ошибке`() = runTest {
        val token = "google-token"

        coEvery { googleSignInUseCase(token) } throws Exception("Network error")

        var successCalled = false
        viewModel.signInWithGoogle(token) { successCalled = true }

        assertTrue(viewModel.errorMessage.value!!.contains("Network error"))
        assertFalse(viewModel.isGoogleLoading.value)
        assertFalse(successCalled)
    }

    @Test
    fun `clearError очищает все ошибки`() {
        val validationResult = ValidationResult(false, "Ошибка пароля")
        every { validatePasswordUseCase.validatePasswordStrength("weak") } returns validationResult

        viewModel.updatePassword("weak")
        viewModel.signIn { }

        assertNotNull(viewModel.passwordError.value)
        assertNotNull(viewModel.errorMessage.value)

        viewModel.clearError()

        assertNull(viewModel.passwordError.value)
        assertNull(viewModel.errorMessage.value)
    }
}