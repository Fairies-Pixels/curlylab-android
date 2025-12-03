package fairies.pixels.curlyLabAndroid.data.remote.model.response.profile

import kotlinx.serialization.Serializable

@Serializable
data class HairTypeResponse(
    val porosity: String?,
    val isColored: Boolean?,
    val thickness: String?
)