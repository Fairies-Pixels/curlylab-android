package fairies.pixels.curlyLabAndroid.presentation.profile.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val hairTypeRepository: HairTypesRepository,
    private val authRepository: AuthRepository,
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
    val logoutState: StateFlow<Result<Unit>?> = _logoutState.asStateFlow()

    private val _deleteState = MutableStateFlow<Result<Unit>?>(null)
    val deleteState: StateFlow<Result<Unit>?> = _deleteState.asStateFlow()


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

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.logout()
                _logoutState.value = Result.success(Unit)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка выхода: ${e.message}"
                _logoutState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = _userId.value
                if (userId != null) {
                    authRepository.deleteAccount(userId)
                    _deleteState.value = Result.success(Unit)
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Ошибка удаления аккаунта: ${e.message}"
                _deleteState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetStates() {
        _logoutState.value = null
        _deleteState.value = null
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = _userId.value ?: return@launch

                val context = getApplication<Application>()
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

                val file = uriToFile(uri)

                val requestBody = file.asRequestBody(mimeType.toMediaType())
                val part = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = file.name,
                    body = requestBody
                )

                val imageUrl = usersRepository.uploadUserAvatar(userId, file, part)
                _imageUrl.value = imageUrl

            } catch (e: Exception) {
                _error.value = "Ошибка загрузки фото: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAvatar() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = _userId.value ?: return@launch
                usersRepository.deleteUserAvatar(userId)

                _imageUrl.value = null
                _error.value = null

            } catch (e: Exception) {
                _error.value = "Ошибка удаления аватара: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val context = getApplication<Application>()
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        return file
    }
}