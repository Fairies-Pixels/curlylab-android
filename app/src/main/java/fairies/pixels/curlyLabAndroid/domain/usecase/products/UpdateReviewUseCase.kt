package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.ReviewRequest
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import java.util.UUID
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(userId: UUID, productId: UUID, reviewId: UUID, mark: Int, reviewText: String) {
        productsRepository.updateReview(userId, productId, reviewId, mark, reviewText)
    }
}