package fairies.pixels.curlyLabAndroid.data.remote.model.request.profile

import kotlinx.serialization.Serializable

@Serializable
data class HairTypeRequest(
    val userId: String,
    val porosity: String? = null,
    val isColored: Boolean? = null,
    val thickness: String? = null
)