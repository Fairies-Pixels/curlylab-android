package fairies.pixels.curlyLabAndroid.domain.usecase.auth

class ValidatePasswordUseCase {
    operator fun invoke(password: String, confirmPassword: String): ValidationResult {
        return when {
            password.length < 6 -> ValidationResult(
                successful = false,
                errorMessage = "Пароль должен содержать минимум 6 символов"
            )

            password.length > 20 -> ValidationResult(
                successful = false,
                errorMessage = "Пароль должен содержать не более 20 символов"
            )

            containsNonLatinLetters(password) -> ValidationResult(
                successful = false,
                errorMessage = "Пароль может содержать любые символы, но буквы только латинские"
            )

            password != confirmPassword -> ValidationResult(
                successful = false,
                errorMessage = "Пароли не совпадают"
            )

            else -> ValidationResult(successful = true)
        }
    }

    fun validatePasswordStrength(password: String): ValidationResult {
        return when {
            password.length < 6 -> ValidationResult(
                successful = false,
                errorMessage = "Пароль должен содержать минимум 6 символов"
            )

            password.length > 20 -> ValidationResult(
                successful = false,
                errorMessage = "Пароль должен содержать не более 20 символов"
            )

            containsNonLatinLetters(password) -> ValidationResult(
                successful = false,
                errorMessage = "Пароль может содержать любые символы, но буквы только латинские"
            )

            else -> ValidationResult(successful = true)
        }
    }

    private fun containsNonLatinLetters(text: String): Boolean {
        return text.any { char ->
            Character.isLetter(char) && !isLatinLetter(char)
        }
    }

    private fun isLatinLetter(char: Char): Boolean {
        return char in 'a'..'z' || char in 'A'..'Z'
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)