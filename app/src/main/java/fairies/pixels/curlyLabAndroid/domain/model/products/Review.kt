package fairies.pixels.curlyLabAndroid.domain.model.products

import java.time.OffsetDateTime
import java.util.UUID

data class Review(
    val id: UUID,
    val productId: UUID,
    val userId: UUID,
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)