package fairies.pixels.curlyLabAndroid.presentation.composition.screen.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.composition.AnalysisResult
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@HiltViewModel
class CompositionViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _inputText = mutableStateOf("")
    val inputText: State<String> = _inputText

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result

    fun onInputTextChange(newText: String) {
        _inputText.value = newText
    }

    fun analyze(context: Context, imageUri: Uri?) {
        _result.value = "Анализируем..."
        viewModelScope.launch {
            try {
                val filePart = imageUri?.let { uri ->
                    val resolver = context.contentResolver
                    val inputStream = resolver.openInputStream(uri)

                    val mime = resolver.getType(uri) ?: "image/*"
                    val ext = when (mime) {
                        "image/png" -> ".png"
                        "image/webp" -> ".webp"
                        else -> ".jpg"
                    }

                    val tempFile = File.createTempFile("upload_", ext, context.cacheDir)
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val requestFile = tempFile
                        .asRequestBody(mime.toMediaTypeOrNull())

                    MultipartBody.Part.createFormData(
                        "file",
                        tempFile.name,
                        requestFile
                    )
                }

                val textPart = _inputText.value.takeIf { it.isNotBlank() }
                    ?.toRequestBody("text/plain".toMediaTypeOrNull())

                val res: AnalysisResult = api.analyzeComposition(
                    file = filePart,
                    text = textPart
                )

                _result.value = if (res.issues.isNullOrEmpty()) {
                    "Всё в порядке!"
                } else {
                    val list = res.issues.joinToString("\n\n") { issue ->
                        " ${issue.ingredient}\nКатегория: ${issue.category}\nПричина: ${issue.reason}"
                    }
                    "Обнаружены проблемы:\n\n$list"
                }

            } catch (e: Exception) {
                _result.value = "Ошибка: ${e.message}"
            }
        }
    }
}
