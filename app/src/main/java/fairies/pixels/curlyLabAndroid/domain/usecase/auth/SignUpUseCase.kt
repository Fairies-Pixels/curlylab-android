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
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        if (username.length < 2) {
            return Result.failure(IllegalArgumentException("Username must be at least 2 characters"))
        }

        return authRepository.register(email, password, username)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}