package fairies.pixels.curlyLabAndroid

import io.mockk.*
import kotlinx.coroutines.test.*
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.*
import org.junit.Assert.*
import fairies.pixels.curlyLabAndroid.data.repository.analysis.AnalysisRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType

import java.io.ByteArrayInputStream

@OptIn(ExperimentalCoroutinesApi::class)
class AnalysisRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var api: ApiService
    private lateinit var repository: AnalysisRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        api = mockk()
        repository = AnalysisRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `analyzePhoto returns HIGH porosity`() = runTest {

        val json = """
            {
              "result": {
                "porosity": "HIGH"
              }
            }
        """.trimIndent()

        val responseBody = json.toResponseBody("application/json".toMediaType())

        coEvery {
            api.analyzeHair(any())
        } returns Response.success(responseBody)

        val result = repository.analyzePhoto(byteArrayOf(1,2,3))

        assertEquals("Высокая пористость", result)
    }

    @Test
    fun `analyzePhoto returns MEDIUM porosity`() = runTest {

        val json = """{"result":{"porosity":"MEDIUM"}}"""
        val responseBody = json.toResponseBody("application/json".toMediaType())

        coEvery { api.analyzeHair(any()) } returns Response.success(responseBody)

        val result = repository.analyzePhoto(byteArrayOf())

        assertEquals("Средняя пористость", result)
    }

    @Test
    fun `analyzePhoto returns LOW porosity`() = runTest {

        val json = """{"result":{"porosity":"LOW"}}"""
        val responseBody = json.toResponseBody("application/json".toMediaType())

        coEvery { api.analyzeHair(any()) } returns Response.success(responseBody)

        val result = repository.analyzePhoto(byteArrayOf())

        assertEquals("Низкая пористость", result)
    }

    @Test(expected = Exception::class)
    fun `analyzePhoto throws when response not successful`() = runTest {

        val errorBody = "server error".toResponseBody("text/plain".toMediaType())

        coEvery {
            api.analyzeHair(any())
        } returns Response.error(500, errorBody)

        repository.analyzePhoto(byteArrayOf(1))
    }

    @Test
    fun `analyzePhoto returns raw when json invalid`() = runTest {

        val invalidJson = "broken json"
        val responseBody = invalidJson.toResponseBody("text/plain".toMediaType())

        coEvery {
            api.analyzeHair(any())
        } returns Response.success(responseBody)

        val result = repository.analyzePhoto(byteArrayOf())

        assertTrue(result.contains("broken"))
    }

}