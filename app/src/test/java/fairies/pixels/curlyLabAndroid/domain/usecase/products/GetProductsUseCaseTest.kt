package fairies.pixels.curlyLabAndroid.domain.usecase.products

import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ProductResponse
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.UUID

class GetProductsUseCaseTest {

    private lateinit var repository: ProductsRepository
    private lateinit var getProductsUseCase: GetProductsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getProductsUseCase = GetProductsUseCase(repository)
    }

    @Test
    fun `успешное получение списка продуктов`() = runBlocking {
        val testProducts = listOf(
            ProductResponse(
                id = UUID.randomUUID(),
                name = "Шампунь",
                description = "Увлажняющий шампунь",
                tags = listOf("Высокая", "Да"),
                imageUrl = "url1"
            ),
            ProductResponse(
                id = UUID.randomUUID(),
                name = "Кондиционер",
                description = "Питательный кондиционер",
                tags = listOf("Средняя", "Нет"),
                imageUrl = "url2"
            )
        )

        coEvery { repository.getProducts() } returns testProducts

        val result = getProductsUseCase()

        assert(result.size == 2)
        assert(result[0].name == "Шампунь")
        assert(result[1].name == "Кондиционер")
        assert(result[0].tags.contains("Высокая"))
        assert(result[1].tags.contains("Средняя"))
    }

    @Test
    fun `возврат пустого списока, если продуктов нет`() = runBlocking {
        coEvery { repository.getProducts() } returns emptyList()

        val result = getProductsUseCase()

        assert(result.isEmpty())
    }
}