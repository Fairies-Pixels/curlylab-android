package fairies.pixels.curlyLabAndroid.integration.product

import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ReviewResponse
import fairies.pixels.curlyLabAndroid.data.repository.products.ProductsRepositoryImpl
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ProductsReviewsIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var productsRepository: ProductsRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        productsRepository = ProductsRepositoryImpl(apiService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getProductReviews_returnsAllReviews() = runTest {
        val productId = UUID.randomUUID()

        val reviewJson = """
            [
                {
                    "reviewId": "${UUID.randomUUID()}",
                    "userId": "${UUID.randomUUID()}",
                    "productId": "$productId",
                    "date": "2026-03-16T12:00:00Z",
                    "mark": 5,
                    "review": "Отличный продукт!"
                },
                {
                    "reviewId": "${UUID.randomUUID()}",
                    "userId": "${UUID.randomUUID()}",
                    "productId": "$productId",
                    "date": "2026-03-15T10:30:00Z",
                    "mark": 4,
                    "review": "Хорошо, но могло быть лучше"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(reviewJson)
        )

        val reviews: List<ReviewResponse> = productsRepository.getReviews(productId)

        Assert.assertEquals(2, reviews.size)
        Assert.assertEquals("Отличный продукт!", reviews[0].review)
        Assert.assertEquals("Хорошо, но могло быть лучше", reviews[1].review)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/products/$productId/reviews", request.path)
        Assert.assertEquals("GET", request.method)
    }

    @Test
    fun getProductReviews_returnsEmptyList() = runTest {
        val productId = UUID.randomUUID()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val reviews: List<ReviewResponse> = productsRepository.getReviews(productId)

        Assert.assertTrue(reviews.isEmpty())

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/products/$productId/reviews", request.path)
        Assert.assertEquals("GET", request.method)
    }
}