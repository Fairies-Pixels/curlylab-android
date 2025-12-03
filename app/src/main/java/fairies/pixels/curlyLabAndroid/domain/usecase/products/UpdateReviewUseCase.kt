package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(
        userId: UUID,
        productId: UUID,
        reviewId: UUID,
        mark: Int,
        review: String
    ): Response<Unit> {
        return try {
            repository.updateReview(userId, productId, reviewId, mark, review)
            Response.success(Unit)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, e.message ?: "Unknown error"))
        }
    }
}