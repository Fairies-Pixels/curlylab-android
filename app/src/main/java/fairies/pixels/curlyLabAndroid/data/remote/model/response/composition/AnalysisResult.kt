package fairies.pixels.curlyLabAndroid.data.remote.model.response.composition

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisResponse(
    val status: String?,
    val result: AnalysisResult?
)
@Serializable
data class AnalysisResult(
    val ok: Boolean?,
    val raw_text_excerpt: String?,
    val issues_count: Int?,
    val issues: List<AnalysisIssue>?,
    val result: String?
)
@Serializable
data class AnalysisIssue(
    val ingredient: String?,
    val category: String?,
    val reason: String?
)