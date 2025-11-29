package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import java.util.UUID
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(userId: UUID, productId: UUID) {
        val isFavorite = repository.isFavorite(userId, productId)
        if (isFavorite) {
            repository.removeFromFavorites(userId, productId)
        } else {
            repository.addToFavorites(userId, productId)
        }
    }
}