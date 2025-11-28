package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.oauth.GoogleAuthUiClient
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            val loggedIn = authRepository.isUserLoggedIn()
            _isLoggedIn.value = loggedIn
            _isLoading.value = false
        }
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

    fun logout() {
        viewModelScope.launch {
            googleAuthUiClient.signOut()
            authRepository.logout()
            _isLoggedIn.value = false
        }
    }
}