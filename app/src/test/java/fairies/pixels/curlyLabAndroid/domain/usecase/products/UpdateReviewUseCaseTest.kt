package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.UUID

@RunWith(Parameterized::class)
class UpdateReviewCombinationUseCaseTest(
    private val mark: Int, private val review: String, private val shouldSucceed: Boolean
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1, "Ужасно", true),
                arrayOf(2, "Плохо", true),
                arrayOf(3, "Нормально", true),
                arrayOf(4, "Хорошо", true),
                arrayOf(5, "Отлично", true),
                arrayOf(3, "a".repeat(1000), true)
            )
        }
    }

    private lateinit var repository: ProductsRepository
    private lateinit var updateReviewUseCase: UpdateReviewUseCase

    private val testUserId = UUID.randomUUID()
    private val testProductId = UUID.randomUUID()
    private val testReviewId = UUID.randomUUID()

    @Before
    fun setUp() {
        repository = mockk()
        updateReviewUseCase = UpdateReviewUseCase(repository)
    }

    @Test
    fun `успешная обработка различных оценок и отзывов`() = runBlocking {
        coEvery {
            repository.updateReview(testUserId, testProductId, testReviewId, mark, review)
        } returns Unit

        val result = updateReviewUseCase(testUserId, testProductId, testReviewId, mark, review)

        assertTrue(result.isSuccessful)
        coVerify {
            repository.updateReview(testUserId, testProductId, testReviewId, mark, review)
        }
    }
}


class UpdateReviewUseCaseTest {

    private lateinit var repository: ProductsRepository
    private lateinit var updateReviewUseCase: UpdateReviewUseCase

    private val testUserId = UUID.randomUUID()
    private val testProductId = UUID.randomUUID()
    private val testReviewId = UUID.randomUUID()

    @Before
    fun setUp() {
        repository = mockk()
        updateReviewUseCase = UpdateReviewUseCase(repository)
    }

    @Test
    fun `успешное обновление отзыва`() = runBlocking {
        coEvery {
            repository.updateReview(testUserId, testProductId, testReviewId, 4, "Хороший продукт")
        } returns Unit

        val result =
            updateReviewUseCase(testUserId, testProductId, testReviewId, 4, "Хороший продукт")

        assertTrue(result.isSuccessful)
        coVerify {
            repository.updateReview(testUserId, testProductId, testReviewId, 4, "Хороший продукт")
        }
    }

    @Test
    fun `возврат ошибки при исключении`() = runBlocking {
        coEvery {
            repository.updateReview(any(), any(), any(), any(), any())
        } throws Exception("Ошибка")

        val result = updateReviewUseCase(testUserId, testProductId, testReviewId, 3, "Тест")

        assertFalse(result.isSuccessful)
        assertEquals(500, result.code())
    }
}