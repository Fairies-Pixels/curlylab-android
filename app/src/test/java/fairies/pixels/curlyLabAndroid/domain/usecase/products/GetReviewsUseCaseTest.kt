package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ReviewResponse
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.OffsetDateTime
import java.util.UUID

@RunWith(Parameterized::class)
class GetReviewsCombinationsUseCaseTest(
    private val mark: Int,
    private val reviewText: String,
    private val expectedUserName: String
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1, "Ужасно", "Анна"),
                arrayOf(2, "Плохо", "Мария"),
                arrayOf(3, "Нормально", "user"),
                arrayOf(4, "Хорошо", "Анна"),
                arrayOf(5, "Отлично", "Мария"),
                arrayOf(5, "", "user"),
                arrayOf(5, "a".repeat(1000), "Анна")
            )
        }
    }

    private lateinit var productsRepository: ProductsRepository
    private lateinit var usersRepository: UsersRepository
    private lateinit var getReviewsUseCase: GetReviewsUseCase

    private val testProductId = UUID.randomUUID()
    private val testUserId = UUID.randomUUID()

    @Before
    fun setUp() {
        productsRepository = mockk()
        usersRepository = mockk()
        getReviewsUseCase = GetReviewsUseCase(productsRepository, usersRepository)
    }

    @Test
    fun `успешная обработка различных комбинаций оценок и отзывов`() = runBlocking {
        val reviewId = UUID.randomUUID()
        val testReviews = listOf(
            ReviewResponse(
                reviewId = reviewId,
                userId = testUserId,
                productId = testProductId,
                date = OffsetDateTime.now().toString(),
                mark = mark,
                review = reviewText
            )
        )

        coEvery { productsRepository.getReviews(testProductId) } returns testReviews
        coEvery { usersRepository.getUser(testUserId.toString()) } returns mockk {
            coEvery { username } returns expectedUserName
        }

        val result = getReviewsUseCase(testProductId)

        assertEquals(1, result.size)
        assertEquals(mark, result[0].rating)
        assertEquals(reviewText, result[0].comment)
        assertEquals(expectedUserName, result[0].userName)
    }
}

class GetReviewsUseCaseTest {

    private lateinit var productsRepository: ProductsRepository
    private lateinit var usersRepository: UsersRepository
    private lateinit var getReviewsUseCase: GetReviewsUseCase

    private val testProductId = UUID.randomUUID()
    private val testUserId1 = UUID.randomUUID()
    private val testUserId2 = UUID.randomUUID()

    @Before
    fun setUp() {
        productsRepository = mockk()
        usersRepository = mockk()
        getReviewsUseCase = GetReviewsUseCase(productsRepository, usersRepository)
    }

    @Test
    fun `успешное получение отзывов и имен пользователей`() = runBlocking {
        val testReviews = listOf(
            ReviewResponse(
                reviewId = UUID.randomUUID(),
                userId = testUserId1,
                productId = testProductId,
                date = OffsetDateTime.now().toString(),
                mark = 5,
                review = "Отлично!"
            ),
            ReviewResponse(
                reviewId = UUID.randomUUID(),
                userId = testUserId2,
                productId = testProductId,
                date = OffsetDateTime.now().toString(),
                mark = 4,
                review = "Хорошо"
            )
        )

        coEvery { productsRepository.getReviews(testProductId) } returns testReviews
        coEvery { usersRepository.getUser(testUserId1.toString()) } returns mockk {
            coEvery { username } returns "Анна"
        }
        coEvery { usersRepository.getUser(testUserId2.toString()) } returns mockk {
            coEvery { username } returns "Мария"
        }

        val result = getReviewsUseCase(testProductId)

        assertEquals(2, result.size)
        assertEquals("Анна", result[0].userName)
        assertEquals(5, result[0].rating)
        assertEquals("Мария", result[1].userName)
        assertEquals(4, result[1].rating)
    }

    @Test
    fun `использование значения по умолчанию при ошибке получения пользователя`() = runBlocking {
        val testReviews = listOf(
            ReviewResponse(
                reviewId = UUID.randomUUID(),
                userId = testUserId1,
                productId = testProductId,
                date = OffsetDateTime.now().toString(),
                mark = 5,
                review = "Отлично!"
            )
        )

        coEvery { productsRepository.getReviews(testProductId) } returns testReviews
        coEvery { usersRepository.getUser(any()) } throws Exception("Ошибка")

        val result = getReviewsUseCase(testProductId)

        assertEquals(1, result.size)
        assertEquals("user", result[0].userName)
    }

    @Test
    fun `возврат пустого списка, если отзывов нет`() = runBlocking {
        coEvery { productsRepository.getReviews(testProductId) } returns emptyList()

        val result = getReviewsUseCase(testProductId)

        assertTrue(result.isEmpty())
    }
}