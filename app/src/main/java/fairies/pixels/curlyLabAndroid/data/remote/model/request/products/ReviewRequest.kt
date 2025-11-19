package fairies.pixels.curlyLabAndroid.data.remote.model.request.products

import fairies.pixels.curlyLabAndroid.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ReviewRequest(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val mark: Int,
    val review: String
)