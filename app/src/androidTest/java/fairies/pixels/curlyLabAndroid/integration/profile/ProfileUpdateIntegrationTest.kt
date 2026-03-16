package fairies.pixels.curlyLabAndroid.integration.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.UserRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.UserResponse
import fairies.pixels.curlyLabAndroid.data.repository.profile.UsersRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@RunWith(AndroidJUnit4::class)
class ProfileUpdateIntegrationTest {

    companion object {
        private lateinit var sharedDataStore: DataStore<Preferences>
        private lateinit var sharedEncryptedPrefs: SharedPreferences
        private lateinit var sharedContext: Context
        private lateinit var sharedAuthDataStore: AuthDataStore

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            sharedContext = ApplicationProvider.getApplicationContext()

            sharedContext.filesDir.listFiles()?.forEach {
                if (it.name.startsWith("datastore/test_profile_update")) {
                    it.delete()
                }
            }

            sharedDataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    File(sharedContext.filesDir, "datastore/test_profile_update.preferences_pb")
                }
            )

            val masterKey = MasterKey.Builder(sharedContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedEncryptedPrefs = EncryptedSharedPreferences.create(
                sharedContext,
                "test_profile_update_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            sharedAuthDataStore = AuthDataStore(sharedDataStore, sharedEncryptedPrefs)

            runBlocking {
                sharedAuthDataStore.saveAuthData(
                    isLoggedIn = true,
                    userId = "user-123",
                    username = "oldusername",
                    email = "test@gmail.com"
                )
            }
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            runBlocking {
                sharedAuthDataStore.clearAuthData()
            }
        }
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var usersRepository: UsersRepository
    private lateinit var authDataStore: AuthDataStore

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        authDataStore = AuthDataStore(sharedDataStore, sharedEncryptedPrefs)
        usersRepository = UsersRepositoryImpl(apiService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        runBlocking {
            authDataStore.clearAuthData()
        }
    }

    @Test
    fun updateUsername_successfully_updatesProfile() = runTest {
        val userId = "user-123"
        val newUsername = "newusername"

        val updateResponse = UserResponse(
            id = userId,
            username = newUsername,
            imageUrl = null,
            createdAt = java.time.LocalDateTime.now()
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(updateResponse))
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(updateResponse))
        )

        val request = UserRequest(username = newUsername)
        usersRepository.updateUser(userId, request)

        val updatedUser = usersRepository.getUser(userId)

        Assert.assertEquals(newUsername, updatedUser.username)

        val updateRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/$userId", updateRequest.path)
        Assert.assertEquals("PUT", updateRequest.method)

        val requestBody = Gson().fromJson(updateRequest.body.readUtf8(), UserRequest::class.java)
        Assert.assertEquals(newUsername, requestBody.username)

        val getRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/$userId", getRequest.path)
        Assert.assertEquals("GET", getRequest.method)
    }

    @Test
    fun updateUsername_withEmptyUsername_returnsError() = runTest {
        val userId = "user-123"
        val emptyUsername = ""

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("""{"error": "Username cannot be empty"}""")
        )

        val request = UserRequest(username = emptyUsername)

        try {
            usersRepository.updateUser(userId, request)
            Assert.fail("Expected exception was not thrown")
        } catch (e: Exception) {
            Assert.assertTrue(e.message?.contains("400") == true)
        }

        val updateRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/$userId", updateRequest.path)
        Assert.assertEquals("PUT", updateRequest.method)
    }

    @Test
    fun updateUsername_withNonExistentUser_returnsError() = runTest {
        val userId = "non-existent-user"
        val newUsername = "newusername"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"error": "User not found"}""")
        )

        val request = UserRequest(username = newUsername)

        try {
            usersRepository.updateUser(userId, request)
            Assert.fail("Expected exception was not thrown")
        } catch (e: Exception) {
            Assert.assertTrue(e.message?.contains("404") == true)
        }
    }

    @Test
    fun updateUsername_withUnauthorizedUser_returnsError() = runTest {
        val userId = "user-123"
        val newUsername = "newusername"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "Unauthorized"}""")
        )

        val request = UserRequest(username = newUsername)

        try {
            usersRepository.updateUser(userId, request)
            Assert.fail("Expected exception was not thrown")
        } catch (e: Exception) {
            Assert.assertTrue(e.message?.contains("401") == true)
        }
    }
}