package fairies.pixels.curlyLabAndroid.data.repository.analysis

import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AnalysisRepository {

    override suspend fun analyzePhoto(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {

        val body = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", "photo.jpg", body)

        // Отправляем фото на API
        val response: Response<ResponseBody> = apiService.analyzeHair(part)

        // Читаем тело ответа
        val raw = response.body()?.byteStream()?.bufferedReader()?.readText() ?: ""
        println("RAW_RESPONSE = $raw")

        if (!response.isSuccessful) {
            throw Exception("Ошибка анализа: ${response.errorBody()?.string()}")
        }

        return@withContext try {
            val json = JSONObject(raw)
            val resultObj = json.optJSONObject("result")
            val porosity = resultObj?.optString("porosity")?.uppercase()

            println("PARSED POROSITY = $porosity")

            // Возвращаем удобочитаемый результат для UI или raw текст, если не удалось распарсить
            when (porosity) {
                "HIGH" -> "Высокая пористость"
                "MEDIUM" -> "Средняя пористость"
                "LOW" -> "Низкая пористость"
                else -> raw.ifEmpty { "Результат недоступен" }
            }

        } catch (e: Exception) {
            println("JSON PARSE ERROR: ${e.message}")
            raw.ifEmpty { "Результат недоступен" }
        }

    }
}