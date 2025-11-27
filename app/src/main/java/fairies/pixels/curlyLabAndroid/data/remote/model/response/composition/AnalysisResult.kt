package fairies.pixels.curlyLabAndroid.data.remote.model.response.composition

import kotlinx.serialization.json.JsonElement

data class AnalysisIssue(
    val ingredient: String,
    val category: String,
    val reason: String
)

data class AnalysisResult(
    val result: JsonElement,
    val issues: List<AnalysisIssue>? = null
)