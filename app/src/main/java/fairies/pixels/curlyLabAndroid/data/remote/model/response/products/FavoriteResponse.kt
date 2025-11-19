package fairies.pixels.curlyLabAndroid.data.remote.model.response.products

import fairies.pixels.curlyLabAndroid.domain.model.products.Favorite
import fairies.pixels.curlyLabAndroid.domain.model.products.Product
import fairies.pixels.curlyLabAndroid.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FavoriteResponse(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val productId: UUID
)

fun FavoriteResponse.toDomain(product: Product? = null): Favorite = Favorite(
    userId = userId,
    productId = productId,
    product = product
)