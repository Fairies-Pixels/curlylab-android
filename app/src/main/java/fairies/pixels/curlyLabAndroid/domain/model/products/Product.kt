package fairies.pixels.curlyLabAndroid.domain.model.products

import java.util.UUID

data class Product(
    val id: UUID,
    val name: String,
    val description: String,
    val tags: List<String>,
    val imageUrl: String?
)