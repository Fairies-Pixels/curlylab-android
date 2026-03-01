package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class AddReviewUseCaseTest {

    private lateinit var repository: ProductsRepository
    private lateinit var addReviewUseCase: AddReviewUseCase
    private val testUserId = UUID.randomUUID()
    private val testProductId = UUID.randomUUID()
    private val testMark = 5
    private val testReview = "Отличный продукт!"

    @Before
    fun setUp() {
        repository = mockk()
        addReviewUseCase = AddReviewUseCase(repository)
    }

    @Test
    fun `успешное добавление отзыва`() = runBlocking {
        val expectedResponse = Response.success(Unit)
        coEvery {
            repository.addReview(testUserId, testProductId, testMark, testReview)
        } returns expectedResponse

        val result = addReviewUseCase(testUserId, testProductId, testMark, testReview)

        assert(result.isSuccessful)
        coVerify(exactly = 1) {
            repository.addReview(testUserId, testProductId, testMark, testReview)
        }
    }

    @Test
    fun `возврат ошибки при добавлении отзыва`() = runBlocking {
        val expectedResponse = Response.error<Unit>(400, mockk(relaxed = true))
        coEvery {
            repository.addReview(testUserId, testProductId, testMark, testReview)
        } returns expectedResponse

        val result = addReviewUseCase(testUserId, testProductId, testMark, testReview)

        assert(!result.isSuccessful)
        assert(result.code() == 400)
    }
}