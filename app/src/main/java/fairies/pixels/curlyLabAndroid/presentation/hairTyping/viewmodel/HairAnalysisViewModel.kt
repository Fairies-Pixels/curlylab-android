package fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HairAnalysisViewModel @Inject constructor(
    private val repository: AnalysisRepository
) : ViewModel() {

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Анализ фото волос через ByteArray
     */
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
}