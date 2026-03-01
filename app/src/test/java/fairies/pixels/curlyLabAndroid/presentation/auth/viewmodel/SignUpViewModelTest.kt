package fairies.pixels.curlyLabAndroid.presentation.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.AuthErrors
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.SignUpUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.ValidatePasswordUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.ValidationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
class SignUpViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private lateinit var viewModel: SignUpViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        signUpUseCase = mockk()
        validatePasswordUseCase = mockk()
        viewModel = SignUpViewModel(signUpUseCase, validatePasswordUseCase)
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
    fun `обновление username изменяет значение и очищает ошибку`() {
        viewModel.updateUsername("testuser")

        assertEquals("testuser", viewModel.username.value)
        assertNull(viewModel.errorMessage.value)

        viewModel.updateUsername("newuser")
        assertEquals("newuser", viewModel.username.value)
    }

    @Test
    fun `обновление password изменяет значение и очищает ошибку`() {
        viewModel.updatePassword("password123")

        assertEquals("password123", viewModel.password.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `обновление confirmPassword изменяет значение и очищает ошибку`() {
        viewModel.updateConfirmPassword("password123")

        assertEquals("password123", viewModel.confirmPassword.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `signUp с длинным username возвращает ошибку`() {
        viewModel.updateUsername("a".repeat(21))
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        var successCalled = false
        viewModel.signUp { successCalled = true }

        assertEquals(AuthErrors.USERNAME_TOO_LONG, viewModel.errorMessage.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signUp с коротким username возвращает ошибку`() {
        viewModel.updateUsername("a")
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        var successCalled = false
        viewModel.signUp { successCalled = true }

        assertEquals(AuthErrors.USERNAME_TOO_SHORT, viewModel.errorMessage.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signUp с невалидным паролем возвращает ошибку`() {
        viewModel.updateUsername("testuser")
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password456")

        val validationResult = ValidationResult(false, AuthErrors.PASSWORDS_DONT_MATCH)
        every { validatePasswordUseCase(any(), any()) } returns validationResult

        var successCalled = false
        viewModel.signUp { successCalled = true }

        assertEquals(AuthErrors.PASSWORDS_DONT_MATCH, viewModel.errorMessage.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signUp с пустыми полями возвращает ошибку`() {
        viewModel.updateUsername("testuser")
        viewModel.updateEmail("")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        val validationResult = ValidationResult(true)
        every { validatePasswordUseCase(any(), any()) } returns validationResult

        var successCalled = false
        viewModel.signUp { successCalled = true }

        assertEquals(AuthErrors.FIELDS_REQUIRED, viewModel.errorMessage.value)
        assertFalse(successCalled)
    }

    @Test
    fun `signUp с успешной регистрацией вызывает onSuccess`() = runTest {
        viewModel.updateUsername("testuser")
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        val validationResult = ValidationResult(true)
        every { validatePasswordUseCase(any(), any()) } returns validationResult

        coEvery { signUpUseCase(any(), any(), any()) } returns Result.success(
            AuthResponse("access", "refresh", "testuser", "test@example.com")
        )

        var successCalled = false
        viewModel.signUp { successCalled = true }

        assertNull(viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(successCalled)
        coVerify { signUpUseCase("test@example.com", "password123", "testuser") }
    }

    @Test
    fun `signUp с ошибкой регистрации устанавливает сообщение об ошибке`() = runTest {
        viewModel.updateUsername("testuser")
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        val validationResult = ValidationResult(true)
        every { validatePasswordUseCase(any(), any()) } returns validationResult

        coEvery {
            signUpUseCase(
                any(),
                any(),
                any()
            )
        } returns Result.failure(Exception("User exists"))

        var successCalled = false
        viewModel.signUp { successCalled = true }

        assertEquals("User exists", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(successCalled)
    }

    @Test
    fun `clearError очищает сообщение об ошибке`() {
        viewModel.signUp { }

        assertNotNull(viewModel.errorMessage.value)

        viewModel.clearError()

        assertNull(viewModel.errorMessage.value)
    }
}