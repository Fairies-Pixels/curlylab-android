package fairies.pixels.curlyLabAndroid.data.remote.model.response.products

import fairies.pixels.curlyLabAndroid.domain.model.products.Review
import fairies.pixels.curlyLabAndroid.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class ReviewResponse(
    @Serializable(with = UUIDSerializer::class)
    val reviewId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val productId: UUID,
    val date: String,
    val mark: Int,
    val review: String
)

fun ReviewResponse.toDomain(userName: String): Review = Review(
    id = reviewId,
    productId = productId,
    userId = userId,
    userName = userName,
    rating = mark,
    comment = review,
    createdAt = date
)