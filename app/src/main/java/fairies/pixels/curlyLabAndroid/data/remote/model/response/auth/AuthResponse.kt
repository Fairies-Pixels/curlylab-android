package fairies.pixels.curlyLabAndroid.data.remote.model.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val access: String,
    val refresh: String,
    val userId: String? = null,
    val email: String? = null,
    val username: String? = null
)