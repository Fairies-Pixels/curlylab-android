package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.ReviewRequest
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import java.util.UUID
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: UUID, reviewId: UUID, request: ReviewRequest) {
        repository.updateReview(productId, reviewId, request)
    }
}