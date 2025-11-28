package fairies.pixels.curlyLabAndroid.domain.repository.products

import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.FavoriteResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ProductResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ReviewResponse
import retrofit2.Response
import java.util.UUID

interface ProductsRepository {
    suspend fun getProducts(): List<ProductResponse>
    suspend fun getProductById(productId: UUID): ProductResponse
    suspend fun getUserFavorites(userId: UUID): List<FavoriteResponse>?
    suspend fun isFavorite(userId: UUID, productId: UUID): Boolean
    suspend fun addToFavorites(userId: UUID, productId: UUID)
    suspend fun removeFromFavorites(userId: UUID, productId: UUID)
    suspend fun getReviews(productId: UUID): List<ReviewResponse>
    suspend fun addReview(userId: UUID, productId: UUID, mark: Int, review: String): Response<Unit>
    suspend fun updateReview(
        userId: UUID,
        productId: UUID,
        reviewId: UUID,
        mark: Int,
        review: String
    )

    suspend fun deleteReview(productId: UUID, reviewId: UUID)
}