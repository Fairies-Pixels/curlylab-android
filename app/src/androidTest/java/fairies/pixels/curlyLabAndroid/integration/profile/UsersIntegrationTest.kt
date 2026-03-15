package fairies.pixels.curlyLabAndroid.integration.profile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.repository.profile.UsersRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@RunWith(AndroidJUnit4::class)
class UsersIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var usersRepository: UsersRepository
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

        usersRepository = UsersRepositoryImpl(apiService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        context.cacheDir.listFiles()?.forEach { it.delete() }
    }

    @Test
    fun avatarUploadWithValidImage_savesToS3() = runTest {
        val userId = "user-123"
        val expectedUrl = "https://s3.yandex.com/avatars/user-123.jpg"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"imageUrl": "$expectedUrl"}""")
        )

        val testFile = createTestImageFile()
        val requestBody = testFile.asRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData("file", testFile.name, requestBody)

        val result = usersRepository.uploadUserAvatar(userId, testFile, part)

        Assert.assertEquals(expectedUrl, result)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/users/$userId/upload_image", request.path)
        Assert.assertTrue(request.headers["Content-Type"]?.startsWith("multipart/form-data") == true)

        testFile.delete()
    }

    @Test
    fun avatarUploadWithInvalidFormat_returnsError() = runTest {
        val userId = "user-123"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("""{"error": "Invalid file format. Only JPEG and PNG allowed."}""")
        )

        val testFile = createTestGifFile()
        val requestBody = testFile.asRequestBody("image/gif".toMediaType())
        val part = MultipartBody.Part.createFormData("file", testFile.name, requestBody)

        try {
            usersRepository.uploadUserAvatar(userId, testFile, part)
            Assert.fail("Expected exception was not thrown")
        } catch (e: Exception) {
            Assert.assertTrue(e.message?.contains("400") == true)
        }

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/users/$userId/upload_image", request.path)

        testFile.delete()
    }

    private fun createTestImageFile(): File {
        return File(context.cacheDir, "test_${System.currentTimeMillis()}.jpg").apply {
            writeBytes(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()))
        }
    }

    private fun createTestGifFile(): File {
        return File(context.cacheDir, "test_${System.currentTimeMillis()}.gif").apply {
            writeBytes("GIF89a".toByteArray())
        }
    }
}