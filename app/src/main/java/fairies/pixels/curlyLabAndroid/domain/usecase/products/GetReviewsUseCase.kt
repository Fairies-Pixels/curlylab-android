package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.toDomain
import fairies.pixels.curlyLabAndroid.domain.model.products.Review
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import jakarta.inject.Inject
import java.util.UUID

class GetReviewsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(productId: UUID): List<Review> {
        val reviews = productsRepository.getReviews(productId)

        return reviews.map { reviewResponse ->
            val userName = getUserName(reviewResponse.userId)
            reviewResponse.toDomain(userName)
        }
    }

    private suspend fun getUserName(userId: UUID): String {
        return try {
            val user = usersRepository.getUser(userId.toString())
            user.username
        } catch (e: Exception) {
            "user"
        }
    }
}