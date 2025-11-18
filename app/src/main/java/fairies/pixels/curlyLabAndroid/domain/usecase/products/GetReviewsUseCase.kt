package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.toDomain
import fairies.pixels.curlyLabAndroid.domain.model.products.Review
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import jakarta.inject.Inject
import java.util.UUID

class GetReviewsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: UUID, userName: String): List<Review> {
        return repository.getReviews(productId).map { it.toDomain(userName) }
    }
}