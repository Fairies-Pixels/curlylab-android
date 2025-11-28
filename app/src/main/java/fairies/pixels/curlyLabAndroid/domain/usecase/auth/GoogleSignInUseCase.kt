package fairies.pixels.curlyLabAndroid.domain.usecase.auth

import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<AuthResponse> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Google token is empty"))
        }

        return authRepository.loginWithGoogle(idToken)
    }
}