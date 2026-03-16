package fairies.pixels.curlyLabAndroid.integration.profile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import fairies.pixels.curlyLabAndroid.data.repository.analysis.AnalysisRepositoryImpl
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

@RunWith(AndroidJUnit4::class)
class HairAnalysisIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var analysisRepository: AnalysisRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        context = ApplicationProvider.getApplicationContext()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        analysisRepository = AnalysisRepositoryImpl(apiService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun analyzePhoto_withHighPorosity_returnsHighPorosity() = runTest {
        val mockResponse = """
            {
                "result": {
                    "porosity": "HIGH"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        val testImageBytes = createTestImageBytes()
        val result = analysisRepository.analyzePhoto(testImageBytes)

        Assert.assertEquals("Высокая пористость", result)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/analyze", request.path)
        Assert.assertEquals("POST", request.method)
        Assert.assertTrue(request.headers["Content-Type"]?.startsWith("multipart/form-data") == true)
    }

    @Test
    fun analyzePhoto_withMediumPorosity_returnsMediumPorosity() = runTest {
        val mockResponse = """
            {
                "result": {
                    "porosity": "MEDIUM"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        val testImageBytes = createTestImageBytes()
        val result = analysisRepository.analyzePhoto(testImageBytes)

        Assert.assertEquals("Средняя пористость", result)
    }

    @Test
    fun analyzePhoto_withLowPorosity_returnsLowPorosity() = runTest {
        val mockResponse = """
            {
                "result": {
                    "porosity": "LOW"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        val testImageBytes = createTestImageBytes()
        val result = analysisRepository.analyzePhoto(testImageBytes)

        Assert.assertEquals("Низкая пористость", result)
    }

    @Test
    fun analyzePhoto_withServerError_throwsException() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("""{"error": "Internal server error"}""")
        )

        val testImageBytes = createTestImageBytes()

        try {
            analysisRepository.analyzePhoto(testImageBytes)
            Assert.fail("Expected exception was not thrown")
        } catch (e: Exception) {
            Assert.assertTrue(
                e.message?.contains("Internal server error") == true ||
                        e.message?.contains("Ошибка анализа") == true
            )
        }
    }

    @Test
    fun analyzePhoto_withInvalidResponse_returnsRawResponse() = runTest {
        val mockResponse = "Invalid JSON response"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        val testImageBytes = createTestImageBytes()
        val result = analysisRepository.analyzePhoto(testImageBytes)

        Assert.assertEquals(mockResponse, result)
    }

    @Test
    fun analyzePhoto_withEmptyResponse_returnsDefaultMessage() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        val testImageBytes = createTestImageBytes()
        val result = analysisRepository.analyzePhoto(testImageBytes)

        Assert.assertEquals("Результат недоступен", result)
    }

    private fun createTestImageBytes(): ByteArray {
        return byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xDB.toByte())
    }
}