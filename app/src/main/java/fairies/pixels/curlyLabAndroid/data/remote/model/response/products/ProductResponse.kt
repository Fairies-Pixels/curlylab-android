package fairies.pixels.curlyLabAndroid.data.remote.model.response.products

import fairies.pixels.curlyLabAndroid.domain.model.products.Product
import fairies.pixels.curlyLabAndroid.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val description: String,
    val tags: List<String>,
    val imageUrl: String?
)

fun ProductResponse.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    tags = tags,
    imageUrl = imageUrl
)