package fairies.pixels.curlyLabAndroid.presentation.products.viewmodel

import app.cash.turbine.test
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.domain.model.products.Product
import fairies.pixels.curlyLabAndroid.domain.model.products.Review
import fairies.pixels.curlyLabAndroid.domain.usecase.products.AddReviewUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.DeleteReviewUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetProductsUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetReviewsUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetUserFavoritesUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.ToggleFavoriteUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.UpdateReviewUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import retrofit2.Response
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getProductsUseCase: GetProductsUseCase = mockk()
    private val getUserFavoritesUseCase: GetUserFavoritesUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val getReviewsUseCase: GetReviewsUseCase = mockk()
    private val addReviewUseCase: AddReviewUseCase = mockk()
    private val updateReviewUseCase: UpdateReviewUseCase = mockk()
    private val deleteReviewUseCase: DeleteReviewUseCase = mockk()
    private val authDataStore: AuthDataStore = mockk()

    private lateinit var viewModel: ProductsViewModel

    private val testUserId = UUID.randomUUID()

    @Before
    fun setUp() = runTest {
        coEvery { authDataStore.getUserId() } returns testUserId.toString()
        coEvery { getProductsUseCase() } returns emptyList()
        coEvery { getUserFavoritesUseCase(any()) } returns emptyList()

        viewModel = ProductsViewModel(
            getProductsUseCase,
            getUserFavoritesUseCase,
            toggleFavoriteUseCase,
            getReviewsUseCase,
            addReviewUseCase,
            updateReviewUseCase,
            deleteReviewUseCase,
            authDataStore
        )

        advanceUntilIdle()
    }

    @Test
    fun `загрузка продуктов при инициализации`() = runTest {
        coVerify { getProductsUseCase() }
        assertEquals(emptyList<Product>(), viewModel.products.value)
    }

    @Test
    fun `переключение избранного добавляет продукт, если его не было там`() = runTest {
        val productId = UUID.randomUUID()

        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel.toggleFavorite(productId)
        advanceUntilIdle()

        assertTrue(
            viewModel.favorites.value.any { it.productId == productId }
        )
    }

    @Test
    fun `переключение избранного удаляет продукт из избранного, если он находился там`() = runTest {
        val productId = UUID.randomUUID()

        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel.toggleFavorite(productId)
        advanceUntilIdle()

        viewModel.toggleFavorite(productId)
        advanceUntilIdle()

        assertTrue(viewModel.favorites.value.isEmpty())
    }

    @Test
    fun `фильтрация продуктов возвращает только продукты с подходящими тегами`() = runTest {
        val productId = UUID.randomUUID()

        val product = Product(
            id = productId,
            name = "Test",
            description = "",
            tags = listOf("Низкая", "Натуральная"),
            imageUrl = ""
        )

        coEvery { getProductsUseCase() } returns listOf(product)

        viewModel.refreshData()
        advanceUntilIdle()
        viewModel.filteredProducts.test {

            awaitItem()

            viewModel.toggleTag("Пористость", "Низкая")

            val filtered = awaitItem()

            assertEquals(1, filtered.size)
            assertEquals(productId, filtered.first().id)

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `отправка отзыва успешно очищает ошибку`() = runTest {
        val productId = UUID.randomUUID()

        coEvery {
            addReviewUseCase(any(), any(), any(), any())
        } returns Response.success(Unit)

        coEvery { getReviewsUseCase(any()) } returns emptyList()

        viewModel.submitReview(productId, 5, "Отлично")
        advanceUntilIdle()

        assertNull(viewModel.error.value)
    }

    @Test
    fun `отправка отзыва с ошибкой устанавливает сообщение об ошибке`() = runTest {
        val productId = UUID.randomUUID()

        coEvery {
            addReviewUseCase(any(), any(), any(), any())
        } returns Response.error(
            400,
            "".toResponseBody(null)
        )

        viewModel.submitReview(productId, 5, "Плохо")
        advanceUntilIdle()

        assertTrue(viewModel.error.value!!.contains("400"))
    }

    @Test
    fun `удаление отзыва убирает его из локального списка`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val review = Review(
            id = reviewId,
            productId = productId,
            userId = testUserId,
            userName = "Миша",
            rating = 5,
            comment = "Тестовый отзыв",
            createdAt = System.currentTimeMillis().toString()
        )

        coEvery { getReviewsUseCase(productId) } returns listOf(review)
        coEvery { deleteReviewUseCase(productId, reviewId) } returns Unit

        viewModel.loadReviews(productId)
        advanceUntilIdle()

        assertEquals(1, viewModel.reviews.value.size)

        viewModel.deleteReview(productId, reviewId)
        advanceUntilIdle()

        assertTrue(viewModel.reviews.value.none { it.id == reviewId })
    }

    @Test
    fun `получение id, когда пользователь авторизован`() = runTest {
        val result = viewModel.getCurrentUserId()
        assertEquals(testUserId, result)
    }

    @Test
    fun `выброс исключения, когда пользователь не авторизован`() = runTest {
        coEvery { authDataStore.getUserId() } returns null

        val viewModelWithoutUser = ProductsViewModel(
            getProductsUseCase,
            getUserFavoritesUseCase,
            toggleFavoriteUseCase,
            getReviewsUseCase,
            addReviewUseCase,
            updateReviewUseCase,
            deleteReviewUseCase,
            authDataStore
        )
        advanceUntilIdle()

        try {
            viewModelWithoutUser.getCurrentUserId()
            throw AssertionError("Ожидаемое исключение не было выброшено")
        } catch (e: IllegalStateException) {
            assertEquals("Пользователь не авторизован", e.message)
        }
    }

    @Test
    fun `получение имени пользователя из authDataStore`() = runTest {
        val expectedUsername = "testuser"
        coEvery { authDataStore.getUsername() } returns expectedUsername

        val result = viewModel.getCurrentUsername()

        assertEquals(expectedUsername, result)
    }

    @Test
    fun `возвращение значения user по умолчанию, когда имя не найдено`() = runTest {
        coEvery { authDataStore.getUsername() } returns null

        val result = viewModel.getCurrentUsername()

        assertEquals("user", result)
    }

    @Test
    fun `переключение тега пористости`() = runTest {
        assertNull(viewModel.porosityTag.value)

        viewModel.toggleTag("Пористость", "Низкая")
        assertEquals("Низкая", viewModel.porosityTag.value)

        viewModel.toggleTag("Пористость", "Низкая")
        assertNull(viewModel.porosityTag.value)
    }

    @Test
    fun `переключение тега окрашенности`() = runTest {
        assertNull(viewModel.coloringTag.value)

        viewModel.toggleTag("Окрашенность", "Натуральная")
        assertEquals("Натуральная", viewModel.coloringTag.value)

        viewModel.toggleTag("Окрашенность", "Натуральная")
        assertNull(viewModel.coloringTag.value)
    }

    @Test
    fun `переключение тега толщины`() = runTest {
        assertNull(viewModel.thicknessTag.value)

        viewModel.toggleTag("Толщина", "Тонкая")
        assertEquals("Тонкая", viewModel.thicknessTag.value)

        viewModel.toggleTag("Толщина", "Тонкая")
        assertNull(viewModel.thicknessTag.value)
    }

    @Test
    fun `переключение несуществующего тега ничего не меняет`() = runTest {
        assertNull(viewModel.porosityTag.value)
        assertNull(viewModel.coloringTag.value)
        assertNull(viewModel.thicknessTag.value)

        viewModel.toggleTag("Неизвестная", "Значение")

        assertNull(viewModel.porosityTag.value)
        assertNull(viewModel.coloringTag.value)
        assertNull(viewModel.thicknessTag.value)
    }

    @Test
    fun `изменение выбранной вкладки`() = runTest {
        assertEquals(ProductsTab.ALL_PRODUCTS, viewModel.selectedTab.value)

        viewModel.selectTab(ProductsTab.FAVORITE_PRODUCTS)
        assertEquals(ProductsTab.FAVORITE_PRODUCTS, viewModel.selectedTab.value)

        viewModel.selectTab(ProductsTab.ALL_PRODUCTS)
        assertEquals(ProductsTab.ALL_PRODUCTS, viewModel.selectedTab.value)
    }

    @Test
    fun `сброс всех фильтров`() = runTest {
        viewModel.toggleTag("Пористость", "Низкая")
        viewModel.toggleTag("Окрашенность", "Натуральная")
        viewModel.toggleTag("Толщина", "Тонкая")

        assertEquals("Низкая", viewModel.porosityTag.value)
        assertEquals("Натуральная", viewModel.coloringTag.value)
        assertEquals("Тонкая", viewModel.thicknessTag.value)

        viewModel.clearAllFilters()

        assertNull(viewModel.porosityTag.value)
        assertNull(viewModel.coloringTag.value)
        assertNull(viewModel.thicknessTag.value)
    }

    @Test
    fun `очистка сообщения об ошибке`() = runTest {
        val errorMessage = "Тестовая ошибка"
        coEvery { getProductsUseCase() } throws Exception(errorMessage)

        viewModel.refreshData()
        advanceUntilIdle()

        assertEquals("Ошибка загрузки товаров: $errorMessage", viewModel.error.value)

        viewModel.clearError()
        assertNull(viewModel.error.value)
    }

    @Test
    fun `перезагрузка продуктов, когда пользователь авторизован`() = runTest {
        val productId = UUID.randomUUID()
        val product = Product(
            id = productId,
            name = "Test",
            description = "",
            tags = emptyList(),
            imageUrl = ""
        )

        coEvery { getProductsUseCase() } returns listOf(product)
        coEvery { getUserFavoritesUseCase(any()) } returns emptyList()

        viewModel.refreshData()
        advanceUntilIdle()

        assertEquals(1, viewModel.products.value.size)
        assertEquals(productId, viewModel.products.value.first().id)
        coVerify { getProductsUseCase() }
        coVerify { getUserFavoritesUseCase(testUserId) }
    }

    @Test
    fun `успешное обновление отзыва очищает ошибку и перезагружает отзывы`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val review = Review(
            id = reviewId,
            productId = productId,
            userId = testUserId,
            userName = "Миша",
            rating = 5,
            comment = "Старый отзыв",
            createdAt = System.currentTimeMillis().toString()
        )
        val updatedReview = Review(
            id = reviewId,
            productId = productId,
            userId = testUserId,
            userName = "Миша",
            rating = 4,
            comment = "Обновленный отзыв",
            createdAt = System.currentTimeMillis().toString()
        )

        coEvery { updateReviewUseCase(any(), any(), any(), any(), any()) } returns Response.success(
            Unit
        )
        coEvery { getReviewsUseCase(productId) } returns listOf(updatedReview)

        viewModel.loadReviews(productId)
        advanceUntilIdle()
        assertEquals(1, viewModel.reviews.value.size)

        viewModel.updateReview(productId, reviewId, 4, "Обновленный отзыв")
        advanceUntilIdle()

        assertNull(viewModel.error.value)
        assertEquals(1, viewModel.reviews.value.size)
        assertEquals("Обновленный отзыв", viewModel.reviews.value.first().comment)
        assertEquals(4, viewModel.reviews.value.first().rating)

        coVerify { updateReviewUseCase(testUserId, productId, reviewId, 4, "Обновленный отзыв") }
        coVerify { getReviewsUseCase(productId) }
    }

    @Test
    fun `обновление отзыва с ошибкой 403 показывает правильное сообщение`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val errorResponse = Response.error<Unit>(403, "".toResponseBody(null))

        coEvery { updateReviewUseCase(any(), any(), any(), any(), any()) } returns errorResponse

        viewModel.updateReview(productId, reviewId, 5, "Тест")
        advanceUntilIdle()

        assertEquals(
            "Нельзя редактировать отзыв спустя 24 часа после создания",
            viewModel.error.value
        )
    }

    @Test
    fun `обновление отзыва с ошибкой 404 показывает правильное сообщение`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val errorResponse = Response.error<Unit>(404, "".toResponseBody(null))

        coEvery { updateReviewUseCase(any(), any(), any(), any(), any()) } returns errorResponse

        viewModel.updateReview(productId, reviewId, 5, "Тест")
        advanceUntilIdle()

        assertEquals("Отзыв не найден", viewModel.error.value)
    }

    @Test
    fun `обновление отзыва с ошибкой 400 показывает правильное сообщение`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val errorResponse = Response.error<Unit>(400, "".toResponseBody(null))

        coEvery { updateReviewUseCase(any(), any(), any(), any(), any()) } returns errorResponse

        viewModel.updateReview(productId, reviewId, 5, "Тест")
        advanceUntilIdle()

        assertEquals("Некорректные данные отзыва", viewModel.error.value)
    }

    @Test
    fun `обновление отзыва с ошибкой 500 показывает сообщение с кодом`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val errorResponse = Response.error<Unit>(500, "".toResponseBody(null))

        coEvery { updateReviewUseCase(any(), any(), any(), any(), any()) } returns errorResponse

        viewModel.updateReview(productId, reviewId, 5, "Тест")
        advanceUntilIdle()

        assertTrue(viewModel.error.value!!.contains("500"))
    }

    @Test
    fun `обновление отзыва без авторизации показывает ошибку`() = runTest {
        coEvery { authDataStore.getUserId() } returns null

        val viewModelWithoutUser = ProductsViewModel(
            getProductsUseCase,
            getUserFavoritesUseCase,
            toggleFavoriteUseCase,
            getReviewsUseCase,
            addReviewUseCase,
            updateReviewUseCase,
            deleteReviewUseCase,
            authDataStore
        )
        advanceUntilIdle()

        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()

        viewModelWithoutUser.updateReview(productId, reviewId, 5, "Тест")
        advanceUntilIdle()

        assertEquals("Пользователь не авторизован", viewModelWithoutUser.error.value)
    }

    @Test
    fun `обновление отзыва при исключении показывает ошибку`() = runTest {
        val productId = UUID.randomUUID()
        val reviewId = UUID.randomUUID()
        val errorMessage = "Ошибка сети"

        coEvery { updateReviewUseCase(any(), any(), any(), any(), any()) } throws Exception(
            errorMessage
        )

        viewModel.updateReview(productId, reviewId, 5, "Тест")
        advanceUntilIdle()

        assertTrue(viewModel.error.value!!.contains(errorMessage))
    }
}