package fairies.pixels.curlyLabAndroid.domain.usecase.auth

import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String
    ): Result<AuthResponse> {
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException(AuthErrors.INVALID_EMAIL))
        }

        if (password.length < 6) {
            return Result.failure(IllegalArgumentException(AuthErrors.PASSWORD_TOO_SHORT))
        }

        if (username.length < 2) {
            return Result.failure(IllegalArgumentException(AuthErrors.USERNAME_TOO_SHORT))
        }

        if (username.length > 20) {
            return Result.failure(IllegalArgumentException(AuthErrors.USERNAME_TOO_LONG))
        }

        return authRepository.register(email, password, username)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}