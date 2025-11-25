package fairies.pixels.curlyLabAndroid.data.remote.model.composition

data class AnalysisIssue(
    val ingredient: String,
    val category: String,
    val reason: String
)

data class AnalysisResult(
    val result: String,
    val issues: List<AnalysisIssue>? = null
)