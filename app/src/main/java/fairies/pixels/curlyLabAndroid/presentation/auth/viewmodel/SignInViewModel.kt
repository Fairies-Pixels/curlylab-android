package fairies.pixels.curlyLabAndroid.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.AuthErrors
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.GoogleSignInUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.SignInUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.ValidatePasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isEmailLoading = MutableStateFlow(false)
    val isEmailLoading: StateFlow<Boolean> = _isEmailLoading.asStateFlow()

    private val _isGoogleLoading = MutableStateFlow(false)
    val isGoogleLoading: StateFlow<Boolean> = _isGoogleLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    fun updateEmail(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun updatePassword(value: String) {
        _password.value = value
        _errorMessage.value = null

        if (value.isNotEmpty()) {
            val validation = validatePasswordUseCase.validatePasswordStrength(value)
            _passwordError.value = validation.errorMessage
        } else {
            _passwordError.value = null
        }
    }

    fun signIn(onSuccess: () -> Unit) {
        if (email.value.isEmpty() || password.value.isEmpty()) {
            _errorMessage.value = AuthErrors.FIELDS_REQUIRED
            return
        }

        val passwordValidation = validatePasswordUseCase.validatePasswordStrength(password.value)
        if (!passwordValidation.successful) {
            _errorMessage.value = passwordValidation.errorMessage
            return
        }

        _isEmailLoading.value = true
        viewModelScope.launch {
            val result = signInUseCase(email.value, password.value)
            _isEmailLoading.value = false

            if (result.isSuccess) {
                _errorMessage.value = null
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: AuthErrors.LOGIN_FAILED
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        _isGoogleLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = googleSignInUseCase(idToken)
                _isGoogleLoading.value = false

                if (result.isSuccess) {
                    _errorMessage.value = null
                    onSuccess()
                } else {
                    _errorMessage.value =
                        result.exceptionOrNull()?.message ?: AuthErrors.GOOGLE_LOGIN_FAILED
                }
            } catch (e: Exception) {
                _isGoogleLoading.value = false
                _errorMessage.value = "${AuthErrors.NETWORK_ERROR}: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
        _passwordError.value = null
    }
}