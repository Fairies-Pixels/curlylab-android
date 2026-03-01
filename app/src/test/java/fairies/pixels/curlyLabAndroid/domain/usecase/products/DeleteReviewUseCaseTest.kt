package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.UUID

class DeleteReviewUseCaseTest {

    private lateinit var repository: ProductsRepository
    private lateinit var deleteReviewUseCase: DeleteReviewUseCase

    private val testProductId = UUID.randomUUID()
    private val testReviewId = UUID.randomUUID()

    @Before
    fun setUp() {
        repository = mockk()
        deleteReviewUseCase = DeleteReviewUseCase(repository)
    }

    @Test
    fun `успешное удаление отзыва`() = runBlocking {
        coEvery { repository.deleteReview(testProductId, testReviewId) } returns Unit

        deleteReviewUseCase(testProductId, testReviewId)

        coVerify(exactly = 1) {
            repository.deleteReview(testProductId, testReviewId)
        }
    }

    @Test(expected = Exception::class)
    fun `исключение при ошибке удаления`() = runBlocking {
        coEvery {
            repository.deleteReview(testProductId, testReviewId)
        } throws Exception("Ошибка удаления")

        deleteReviewUseCase(testProductId, testReviewId)
    }
}