package fairies.pixels.curlyLabAndroid.data.repository.products

import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.ReviewRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.FavoriteResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ProductResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ReviewResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import retrofit2.Response
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: ProductsRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = ProductsRepositoryImpl(apiService)
    }

    @Test
    fun `получение всех продуктов возвращает список`() = runTest {
        val products = listOf(mockk<ProductResponse>())
        coEvery { apiService.getAllProducts() } returns products

        val result = repository.getProducts()

        assertEquals(products, result)
        coVerify(exactly = 1) { apiService.getAllProducts() }
    }

    @Test
    fun `получение продукта по id вызывает сервис со строковым id`() = runTest {
        val id = UUID.randomUUID()
        val response = mockk<ProductResponse>()

        coEvery { apiService.getProduct(id.toString()) } returns response

        val result = repository.getProductById(id)

        assertEquals(response, result)
        coVerify { apiService.getProduct(id.toString()) }
    }

    @Test
    fun `получение избранных продуктов пользователя возвращает список`() = runTest {
        val userId = UUID.randomUUID()
        val favorites = listOf(mockk<FavoriteResponse>())

        coEvery { apiService.getUserFavorites(userId.toString()) } returns favorites

        val result = repository.getUserFavorites(userId)

        assertEquals(favorites, result)
        coVerify { apiService.getUserFavorites(userId.toString()) }
    }

    @Test
    fun `проверка, добавлен ли товар в избраное, возвращает boolean`() = runTest {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        coEvery {
            apiService.isProductFavorite(productId.toString(), userId.toString())
        } returns true

        val result = repository.isFavorite(userId, productId)

        assertTrue(result)
        coVerify {
            apiService.isProductFavorite(productId.toString(), userId.toString())
        }
    }

    @Test
    fun `добавление в избранное вызывает сервис с правильным запросом`() = runTest {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        coEvery { apiService.addToFavorites(any(), any()) } returns Response.success(Unit)

        repository.addToFavorites(userId, productId)

        coVerify {
            apiService.addToFavorites(
                userId.toString(),
                match { it.productId == productId }
            )
        }
    }

    @Test
    fun `удаление из избранного вызывает сервис`() = runTest {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        coEvery { apiService.removeFromFavorites(any(), any()) } returns Response.success(Unit)

        repository.removeFromFavorites(userId, productId)

        coVerify {
            apiService.removeFromFavorites(
                userId.toString(),
                productId.toString()
            )
        }
    }

    @Test
    fun `удаление отзыва вызывает сервис с правильными параметрами`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()

        coEvery { apiService.deleteReview(any(), any()) } returns Response.success(Unit)

        repository.deleteReview(productId, reviewId)

        coVerify(exactly = 1) {
            apiService.deleteReview(
                productId.toString(),
                reviewId.toString()
            )
        }
    }

    @Test
    fun `получение отзывов возвращает список`() = runTest {
        val productId = UUID.randomUUID()
        val reviews = listOf(mockk<ReviewResponse>())

        coEvery { apiService.getProductReviews(productId.toString()) } returns reviews

        val result = repository.getReviews(productId)

        assertEquals(reviews, result)
        coVerify { apiService.getProductReviews(productId.toString()) }
    }
}

@RunWith(Parameterized::class)
class ProductsRepositoryAddReviewTest(
    private val mark: Int,
    private val reviewText: String
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1, "Ужасно"),
                arrayOf(2, "Плохо"),
                arrayOf(3, "Нормально"),
                arrayOf(4, "Хорошо"),
                arrayOf(5, "Отлично"),
                arrayOf(5, ""),
                arrayOf(5, "a"),
                arrayOf(5, "a".repeat(500)),
                arrayOf(5, "a".repeat(1000))
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `добавление отзыва с разными параметрами`() = runTest {
        val apiService = mockk<ApiService>()
        val repository = ProductsRepositoryImpl(apiService)
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val response = Response.success(Unit)
        val requestSlot = slot<ReviewRequest>()

        coEvery {
            apiService.createReview(any(), capture(requestSlot))
        } returns response

        val result = repository.addReview(userId, productId, mark, reviewText)

        assertEquals(response, result)
        assertEquals(mark, requestSlot.captured.mark)
        assertEquals(reviewText, requestSlot.captured.review)
    }
}

@RunWith(Parameterized::class)
class ProductsRepositoryUpdateReviewTest(
    private val mark: Int,
    private val reviewText: String
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1, "Ужасно"),
                arrayOf(2, "Плохо"),
                arrayOf(3, "Нормально"),
                arrayOf(4, "Хорошо"),
                arrayOf(5, "Отлично"),
                arrayOf(3, "a".repeat(1000))
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `обновление отзыва с разными параметрами`() = runTest {
        val apiService = mockk<ApiService>()
        val repository = ProductsRepositoryImpl(apiService)
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val requestSlot = slot<ReviewRequest>()

        coEvery {
            apiService.updateReview(productId.toString(), reviewId.toString(), capture(requestSlot))
        } returns mockk()

        repository.updateReview(userId, productId, reviewId, mark, reviewText)

        assertEquals(mark, requestSlot.captured.mark)
        assertEquals(reviewText, requestSlot.captured.review)
        assertEquals(userId, requestSlot.captured.userId)
    }
}
