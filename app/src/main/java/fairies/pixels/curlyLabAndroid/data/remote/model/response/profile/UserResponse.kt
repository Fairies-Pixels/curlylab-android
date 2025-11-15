package fairies.pixels.curlyLabAndroid.data.remote.model.response.profile

import kotlinx.serialization.Serializable
import java.time.Instant
import fairies.pixels.curlyLabAndroid.utils.serializers.InstantSerializer

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    val imageUrl: String?
)