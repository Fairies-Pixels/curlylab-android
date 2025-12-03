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

        val response: Response<ResponseBody> = apiService.analyzeHair(part)

        if (response.isSuccessful) {

            val raw = response.body()?.string() ?: ""

            return@withContext try {
                val json = JSONObject(raw)
                val result = json.optJSONObject("result")

                val porosity = result?.optString("porosity") ?: ""
                when (porosity.uppercase()) {
                    "HIGH" -> "Высокая пористость"
                    "MEDIUM" -> "Средняя пористость"
                    "LOW" -> "Низкая пористость"
                    else -> porosity
                }

            } catch (e: Exception) {
                raw
            }

        } else {
            val error = response.errorBody()?.string()
            throw Exception("Ошибка анализа: $error")
        }

    }
}