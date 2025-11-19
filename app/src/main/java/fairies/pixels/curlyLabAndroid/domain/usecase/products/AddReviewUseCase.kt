package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: UUID, mark: Int, review: String): Response<Unit> {
        return repository.addReview(productId, mark, review)
    }
}