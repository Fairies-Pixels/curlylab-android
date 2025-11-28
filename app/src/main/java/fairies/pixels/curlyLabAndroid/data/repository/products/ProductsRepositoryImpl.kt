package fairies.pixels.curlyLabAndroid.data.repository.products

import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.FavoriteRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.ReviewRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.FavoriteResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ProductResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ReviewResponse
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductsRepository {

    override suspend fun getProducts(): List<ProductResponse> {
        return apiService.getAllProducts()
    }

    override suspend fun getProductById(productId: UUID): ProductResponse {
        return apiService.getProduct(productId.toString())
    }

    override suspend fun getUserFavorites(userId: UUID): List<FavoriteResponse>? {
        return apiService.getUserFavorites(userId.toString())
    }

    override suspend fun isFavorite(userId: UUID, productId: UUID): Boolean {
        return apiService.isProductFavorite(
            productId = productId.toString(),
            userId = userId.toString()
        )
    }

    override suspend fun addToFavorites(userId: UUID, productId: UUID) {
        val request = FavoriteRequest(productId = productId)
        apiService.addToFavorites(userId.toString(), request)
    }

    override suspend fun removeFromFavorites(userId: UUID, productId: UUID) {
        apiService.removeFromFavorites(userId.toString(), productId.toString())
    }

    override suspend fun getReviews(productId: UUID): List<ReviewResponse> {
        return apiService.getProductReviews(productId.toString())
    }

    override suspend fun addReview(userId: UUID, productId: UUID, mark: Int, review: String): Response<Unit> {
        val request = ReviewRequest(
            userId = userId,
            mark = mark,
            review = review
        )
        return apiService.createReview(productId.toString(), request)
    }

    override suspend fun updateReview(userId: UUID, productId: UUID, reviewId: UUID, mark: Int, review: String) {
        val request = ReviewRequest(
            userId = userId,
            mark = mark,
            review = review
        )
        apiService.updateReview(productId.toString(), reviewId.toString(), request)
    }

    override suspend fun deleteReview(productId: UUID, reviewId: UUID) {
        apiService.deleteReview(productId.toString(), reviewId.toString())
    }
}