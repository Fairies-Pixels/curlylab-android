package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.model.products.Favorite
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import java.util.UUID
import javax.inject.Inject

class GetUserFavoritesUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(userId: UUID): List<Favorite> {
        return repository.getUserFavorites(userId)?.map { favoriteResponse ->
            Favorite(favoriteResponse.userId, favoriteResponse.productId)
        } ?: emptyList()
    }
}