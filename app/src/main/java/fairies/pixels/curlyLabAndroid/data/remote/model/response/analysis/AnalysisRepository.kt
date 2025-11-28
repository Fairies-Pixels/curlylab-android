package fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis

interface AnalysisRepository {
    suspend fun analyzePhoto(imageBytes: ByteArray): String
}

