package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.FavoriteResponse
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.UUID

class GetUserFavoritesUseCaseTest {

    private lateinit var repository: ProductsRepository
    private lateinit var getUserFavoritesUseCase: GetUserFavoritesUseCase

    private val testUserId = UUID.randomUUID()
    private val testProductId1 = UUID.randomUUID()
    private val testProductId2 = UUID.randomUUID()

    @Before
    fun setUp() {
        repository = mockk()
        getUserFavoritesUseCase = GetUserFavoritesUseCase(repository)
    }

    @Test
    fun `успешное получение избранных товаров`() = runBlocking {
        val testFavorites = listOf(
            FavoriteResponse(testUserId, testProductId1),
            FavoriteResponse(testUserId, testProductId2)
        )

        coEvery { repository.getUserFavorites(testUserId) } returns testFavorites

        val result = getUserFavoritesUseCase(testUserId)

        assert(result.size == 2)
        assert(result[0].userId == testUserId)
        assert(result[0].productId == testProductId1)
        assert(result[1].productId == testProductId2)
    }

    @Test
    fun `возврат пустого списка, если избранное не найдено`() = runBlocking {
        coEvery { repository.getUserFavorites(testUserId) } returns null

        val result = getUserFavoritesUseCase(testUserId)

        assert(result.isEmpty())
    }

    @Test
    fun `возврат пустого списка, если избранное пустое`() = runBlocking {
        coEvery { repository.getUserFavorites(testUserId) } returns emptyList()

        val result = getUserFavoritesUseCase(testUserId)

        assert(result.isEmpty())
    }
}