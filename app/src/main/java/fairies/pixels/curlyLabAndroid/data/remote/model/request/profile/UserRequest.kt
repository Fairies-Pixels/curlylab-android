package fairies.pixels.curlyLabAndroid.data.remote.model.request.profile

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val username: String
)