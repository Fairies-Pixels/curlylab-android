package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import java.util.UUID
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: UUID, reviewId: UUID) {
        repository.deleteReview(productId, reviewId)
    }
}