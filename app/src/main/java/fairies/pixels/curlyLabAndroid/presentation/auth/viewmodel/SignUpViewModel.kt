package fairies.pixels.curlyLabAndroid.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.AuthErrors
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.SignUpUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.ValidatePasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun updateEmail(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun updateUsername(value: String) {
        _username.value = value
        _errorMessage.value = null
    }

    fun updatePassword(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
        _errorMessage.value = null
    }

    fun signUp(onSuccess: () -> Unit) {
        if (username.value.length > 20) {
            _errorMessage.value = AuthErrors.USERNAME_TOO_LONG
            return
        }

        if (username.value.length < 2) {
            _errorMessage.value = AuthErrors.USERNAME_TOO_SHORT
            return
        }

        val passwordValidation = validatePasswordUseCase(password.value, confirmPassword.value)
        if (!passwordValidation.successful) {
            _errorMessage.value = passwordValidation.errorMessage
            return
        }

        if (email.value.isEmpty() || username.value.isEmpty()) {
            _errorMessage.value = AuthErrors.FIELDS_REQUIRED
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = signUpUseCase(email.value, password.value, username.value)
            _isLoading.value = false

            if (result.isSuccess) {
                _errorMessage.value = null
                onSuccess()
            } else {
                _errorMessage.value =
                    result.exceptionOrNull()?.message ?: AuthErrors.REGISTRATION_FAILED
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}