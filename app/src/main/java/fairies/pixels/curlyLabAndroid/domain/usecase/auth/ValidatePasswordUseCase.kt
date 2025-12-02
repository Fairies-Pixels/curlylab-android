package fairies.pixels.curlyLabAndroid.domain.usecase.auth

class ValidatePasswordUseCase {
    operator fun invoke(password: String, confirmPassword: String): ValidationResult {
        return when {
            password.length < 6 -> ValidationResult(
                successful = false,
                errorMessage = AuthErrors.PASSWORD_TOO_SHORT
            )

            password.length > 20 -> ValidationResult(
                successful = false,
                errorMessage = AuthErrors.PASSWORD_TOO_LONG
            )

            containsNonLatinLetters(password) -> ValidationResult(
                successful = false,
                errorMessage = AuthErrors.PASSWORD_NON_LATIN
            )

            password != confirmPassword -> ValidationResult(
                successful = false,
                errorMessage = AuthErrors.PASSWORDS_DONT_MATCH
            )

            else -> ValidationResult(successful = true)
        }
    }

    fun validatePasswordStrength(password: String): ValidationResult {
        return when {
            password.length < 6 -> ValidationResult(
                successful = false,
                errorMessage = AuthErrors.PASSWORD_TOO_SHORT
            )

            password.length > 20 -> ValidationResult(
                successful = false,
                errorMessage = AuthErrors.PASSWORD_TOO_LONG
            )

            containsNonLatinLetters(password) -> ValidationResult(
                successful = false,
                errorMessage = AuthErrors.PASSWORD_NON_LATIN
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