package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: ProductsRepository
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    private val testUserId = UUID.randomUUID()
    private val testProductId = UUID.randomUUID()

    @Before
    fun setUp() {
        repository = mockk()
        toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `добавление в избранное, если товара не было там`() = runBlocking {
        coEvery { repository.isFavorite(testUserId, testProductId) } returns false
        coEvery { repository.addToFavorites(testUserId, testProductId) } returns Unit

        toggleFavoriteUseCase(testUserId, testProductId)

        coVerify(exactly = 1) {
            repository.isFavorite(testUserId, testProductId)
            repository.addToFavorites(testUserId, testProductId)
        }
        coVerify(exactly = 0) {
            repository.removeFromFavorites(any(), any())
        }
    }

    @Test
    fun `удаление из избранного, если товар уже есть в избранном`() = runBlocking {
        coEvery { repository.isFavorite(testUserId, testProductId) } returns true
        coEvery { repository.removeFromFavorites(testUserId, testProductId) } returns Unit

        toggleFavoriteUseCase(testUserId, testProductId)

        coVerify(exactly = 1) {
            repository.isFavorite(testUserId, testProductId)
            repository.removeFromFavorites(testUserId, testProductId)
        }
        coVerify(exactly = 0) {
            repository.addToFavorites(any(), any())
        }
    }
}