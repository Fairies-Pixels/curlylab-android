package fairies.pixels.curlyLabAndroid.domain.usecase.auth

import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<AuthResponse> {
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException(AuthErrors.INVALID_EMAIL))
        }

        if (password.isEmpty()) {
            return Result.failure(IllegalArgumentException(AuthErrors.PASSWORD_EMPTY))
        }

        return authRepository.login(email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}