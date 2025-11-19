package fairies.pixels.curlyLabAndroid.data.remote.model.request.products

import fairies.pixels.curlyLabAndroid.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FavoriteRequest(
    @Serializable(with = UUIDSerializer::class)
    val productId: UUID
)