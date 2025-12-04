package fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes


@HiltViewModel
class HairAnalysisViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val hairTypesRepository: HairTypesRepository,
    private val repository: AnalysisRepository
) : ViewModel() {

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun analyze(imageBytes: ByteArray) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _result.value = null

            try {
                val porosity = repository.analyzePhoto(imageBytes)
                _result.value = porosity
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка анализа"
            } finally {
                _isLoading.value = false
            }
        }
    }
    private val _saved = MutableStateFlow<Boolean?>(null)
    val saved: StateFlow<Boolean?> = _saved.asStateFlow()

    fun saveResult() {
        viewModelScope.launch {
            try {
                val userId = authDataStore.getUserId() ?: return@launch

                val porosityResult = result.value?.uppercase() ?: return@launch
                val porosityCode = when(porosityResult) {
                    "ВЫСОКАЯ ПОРИСТОСТЬ" -> PorosityTypes.POROUS.dbCode
                    "СРЕДНЯЯ ПОРИСТОСТЬ" -> PorosityTypes.SEMI_POROUS.dbCode
                    "НИЗКАЯ ПОРИСТОСТЬ" -> PorosityTypes.NON_POROUS.dbCode
                    else -> null
                }

                hairTypesRepository.updateHairType(
                    userId,
                    HairTypeRequest(
                        userId = userId,
                        porosity = porosityCode
                    )
                )

                _saved.value = true

            } catch (e: Exception) {
                _saved.value = false
            }
        }
    }

}