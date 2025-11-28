package fairies.pixels.curlyLabAndroid.presentation.profile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val hairTypeRepository: HairTypesRepository,
    private val authDataStore: AuthDataStore,
    application: Application
) : AndroidViewModel(application) {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _logoutState = MutableStateFlow<Result<Unit>?>(null)
    private val _deleteState = MutableStateFlow<Result<Unit>?>(null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _hairType = MutableStateFlow<HairTypeResponse?>(null)
    val hairType: StateFlow<HairTypeResponse?> = _hairType.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authDataStore.getUserId()
                _userId.value = userId

                if (userId != null) {
                    loadHairType(userId)
                    loadProfile(userId)
                } else {
                    _error.value = "Пользователь не авторизован"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных профиля: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    usersRepository.getUser(userId)
                }
                _userName.value = user.username
                _imageUrl.value = user.imageUrl
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных профиля: ${e.message}"
            }
        }
    }

    private fun loadHairType(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val hairType = hairTypeRepository.getHairType(userId)
                _hairType.value = hairType
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки типа волос: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                val userId = authDataStore.getUserId()
                if (userId != null) {
                    withContext(Dispatchers.IO) {
                        usersRepository.deleteUser(userId)
                    }
                    _deleteState.value = Result.success(Unit)
                    //logout()
                } else {
                    _deleteState.value = Result.failure(Exception("Пользователь не найден"))
                }
            } catch (e: Exception) {
                _deleteState.value = Result.failure(e)
            }
        }
    }
}