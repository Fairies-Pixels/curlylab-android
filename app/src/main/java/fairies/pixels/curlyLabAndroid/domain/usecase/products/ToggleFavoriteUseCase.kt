package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.FavoriteRequest
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import java.util.UUID
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(userId: UUID, productId: UUID) {
        val isFavorite = repository.isFavorite(userId, productId)
        val request = FavoriteRequest(productId = productId)

        if (isFavorite) {
            repository.removeFromFavorites(request)
        } else {
            repository.addToFavorites(request)
        }
    }
}