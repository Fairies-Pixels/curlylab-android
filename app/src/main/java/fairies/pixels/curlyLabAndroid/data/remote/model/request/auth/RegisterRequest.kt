package fairies.pixels.curlyLabAndroid.data.remote.model.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)