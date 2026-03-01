package fairies.pixels.curlyLabAndroid.domain.usecase.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidatePasswordUseCaseTest {

    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase

    @Before
    fun setUp() {
        validatePasswordUseCase = ValidatePasswordUseCase()
    }

    @Test
    fun `успешная валидация при совпадающих паролях 6-20 символов латиницы`() {
        val result = validatePasswordUseCase("password123", "password123")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `успешная валидация с цифрами и спецсимволами`() {
        val result = validatePasswordUseCase("Pass123!@#", "Pass123!@#")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `ошибка при коротком пароле`() {
        val result = validatePasswordUseCase("12345", "12345")

        assertFalse(result.successful)
        assertEquals(AuthErrors.PASSWORD_TOO_SHORT, result.errorMessage)
    }

    @Test
    fun `ошибка при длинном пароле`() {
        val password = "a".repeat(21)
        val result = validatePasswordUseCase(password, password)

        assertFalse(result.successful)
        assertEquals(AuthErrors.PASSWORD_TOO_LONG, result.errorMessage)
    }

    @Test
    fun `ошибка при нелатинских буквах`() {
        val nonLatinPasswords = listOf(
            "привет123",
            "пароль!",
            "密码12345",
            "パスドワード"
        )

        nonLatinPasswords.forEach { password ->
            val result = validatePasswordUseCase(password, password)
            assertFalse(result.successful)
            assertEquals(AuthErrors.PASSWORD_NON_LATIN, result.errorMessage)
        }
    }

    @Test
    fun `ошибка при несовпадающих паролях`() {
        val result = validatePasswordUseCase("password123", "password456")

        assertFalse(result.successful)
        assertEquals(AuthErrors.PASSWORDS_DONT_MATCH, result.errorMessage)
    }

    @Test
    fun `успешная валидация пароля при корректном пароле`() {
        val result = validatePasswordUseCase.validatePasswordStrength("Valid123")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `успешная валидация силы пароля с цифрами и символами`() {
        val result = validatePasswordUseCase.validatePasswordStrength("Pass!@#123")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `валидация силы пароля с нелатинскими буквами возвращает ошибку`() {
        val result = validatePasswordUseCase.validatePasswordStrength("привет123")

        assertFalse(result.successful)
        assertEquals(AuthErrors.PASSWORD_NON_LATIN, result.errorMessage)
    }

    @Test
    fun `граничные значения длины пароля`() {
        val password6 = "a".repeat(6)
        val result6 = validatePasswordUseCase.validatePasswordStrength(password6)
        assertTrue(result6.successful)

        val password20 = "a".repeat(20)
        val result20 = validatePasswordUseCase.validatePasswordStrength(password20)
        assertTrue(result20.successful)

        val password5 = "a".repeat(5)
        val result5 = validatePasswordUseCase.validatePasswordStrength(password5)
        assertFalse(result5.successful)

        val password21 = "a".repeat(21)
        val result21 = validatePasswordUseCase.validatePasswordStrength(password21)
        assertFalse(result21.successful)
    }
}
