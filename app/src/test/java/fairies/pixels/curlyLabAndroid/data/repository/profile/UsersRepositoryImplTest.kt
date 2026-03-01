package fairies.pixels.curlyLabAndroid.data.repository.profile

import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.UserRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.UserResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: UsersRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = UsersRepositoryImpl(apiService)
    }

    @Test
    fun `createUser возвращает id если он есть`() = runTest {
        val request = UserRequest("TestUser")

        coEvery { apiService.createUser(request) } returns mapOf("id" to "123")

        val result = repository.createUser(request)

        assertEquals("123", result)
    }

    @Test
    fun `getUser возвращает пользователя`() = runTest {
        val userId = "123"

        val response = UserResponse(
            id = userId,
            username = "TestUser",
            createdAt = LocalDateTime.now(),
            imageUrl = "avatar.jpg"
        )

        coEvery { apiService.getUser(userId) } returns response

        val result = repository.getUser(userId)

        assertEquals(response, result)
        coVerify(exactly = 1) { apiService.getUser(userId) }
    }

    @Test
    fun `updateUser вызывает apiService`() = runTest {
        val userId = "123"
        val request = UserRequest("UpdatedName")

        coEvery { apiService.updateUser(userId, request) } returns Unit

        repository.updateUser(userId, request)

        coVerify(exactly = 1) {
            apiService.updateUser(userId, request)
        }
    }

    @Test
    fun `deleteUser вызывает apiService`() = runTest {
        val userId = "123"

        coEvery { apiService.deleteUser(userId) } returns Unit

        repository.deleteUser(userId)

        coVerify(exactly = 1) { apiService.deleteUser(userId) }
    }

    @Test
    fun `uploadUserAvatar возвращает imageUrl если он есть`() = runTest {
        val userId = "123"

        val file = mockk<File>()
        val part = mockk<MultipartBody.Part>()

        coEvery {
            apiService.uploadUserAvatar(userId, part)
        } returns mapOf("imageUrl" to "http://image.jpg")

        val result = repository.uploadUserAvatar(userId, file, part)

        assertEquals("http://image.jpg", result)
    }

    @Test
    fun `deleteUserAvatar успешно удаляет аватар`() = runTest {
        val userId = "123"

        coEvery {
            apiService.deleteUserAvatar(userId)
        } returns mapOf("message" to "Avatar deleted successfully")

        repository.deleteUserAvatar(userId)

        coVerify { apiService.deleteUserAvatar(userId) }
    }
}