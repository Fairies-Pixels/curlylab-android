package fairies.pixels.curlyLabAndroid.data.remote.model.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class GoogleRequest(
    val idToken: String
)