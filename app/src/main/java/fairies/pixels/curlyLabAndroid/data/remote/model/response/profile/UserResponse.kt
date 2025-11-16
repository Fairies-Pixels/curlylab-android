package fairies.pixels.curlyLabAndroid.data.remote.model.response.profile

import fairies.pixels.curlyLabAndroid.utils.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val imageUrl: String?
)