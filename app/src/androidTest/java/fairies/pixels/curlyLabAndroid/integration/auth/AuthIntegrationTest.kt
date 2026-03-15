package fairies.pixels.curlyLabAndroid.integration.auth

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
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.LoginRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.RegisterRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.auth.AuthResponse
import fairies.pixels.curlyLabAndroid.data.repository.auth.AuthRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
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
class AuthIntegrationTest {

    companion object {
        private lateinit var sharedDataStore: DataStore<Preferences>
        private lateinit var sharedEncryptedPrefs: SharedPreferences
        private lateinit var sharedContext: Context

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            sharedContext = ApplicationProvider.getApplicationContext()

            sharedContext.filesDir.listFiles()?.forEach {
                if (it.name.startsWith("datastore/test_auth")) {
                    it.delete()
                }
            }

            sharedDataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    File(sharedContext.filesDir, "datastore/test_auth.preferences_pb")
                }
            )

            val masterKey = MasterKey.Builder(sharedContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedEncryptedPrefs = EncryptedSharedPreferences.create(
                sharedContext,
                "test_encrypted_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            runBlocking {
                val authDataStore = AuthDataStore(sharedDataStore, sharedEncryptedPrefs)
                authDataStore.clearAuthData()
            }
        }
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository
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
        authRepository = AuthRepositoryImpl(apiService, authDataStore)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        runBlocking {
            authDataStore.clearAuthData()
        }
    }

    @Test
    fun registrationSuccessful_dataSavedInDataStore() = runTest {
        val authResponse = AuthResponse(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEyMyJ9.signature",
            refresh = "refresh.token.456",
            userId = "user-123",
            email = "test@gmail.com",
            username = "testuser"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(authResponse))
        )

        val result = authRepository.register(
            email = "test@gmail.com",
            password = "password123",
            username = "testuser"
        )

        Assert.assertTrue(result.isSuccess)

        val userId = authDataStore.getUserId()
        val username = authDataStore.getUsername()
        val email = authDataStore.getEmail()
        val isLoggedIn = authDataStore.isLoggedIn.first()

        Assert.assertEquals("user-123", userId)
        Assert.assertEquals("testuser", username)
        Assert.assertEquals("test@gmail.com", email)
        Assert.assertTrue(isLoggedIn)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/auth/register", request.path)
        Assert.assertEquals("POST", request.method)

        val requestBody = Gson().fromJson(request.body.readUtf8(), RegisterRequest::class.java)
        Assert.assertEquals("test@gmail.com", requestBody.email)
        Assert.assertEquals("password123", requestBody.password)
        Assert.assertEquals("testuser", requestBody.username)
    }

    @Test
    fun registrationWithExistingEmail_dataNotSaved() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(409)
                .setBody("""{"error": "User already exists"}""")
        )

        val result = authRepository.register(
            email = "existing@gmail.com",
            password = "password123",
            username = "testuser"
        )

        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull()?.message?.contains("409") == true)

        Assert.assertNull(authDataStore.getUserId())
        Assert.assertNull(authDataStore.getUsername())
        Assert.assertFalse(authDataStore.isLoggedIn.first())
    }

    @Test
    fun loginWithWrongPassword_returnsError() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "Invalid credentials"}""")
        )

        val result = authRepository.login(
            email = "test@gmail.com",
            password = "wrongpassword"
        )

        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull()?.message?.contains("401") == true)

        Assert.assertNull(authDataStore.getUserId())
        Assert.assertFalse(authDataStore.isLoggedIn.first())

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/auth/login", request.path)

        val requestBody = Gson().fromJson(request.body.readUtf8(), LoginRequest::class.java)
        Assert.assertEquals("test@gmail.com", requestBody.email)
        Assert.assertEquals("wrongpassword", requestBody.password)
    }

    @Test
    fun loginWithNonExistentUser_returnsError() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"error": "User not found"}""")
        )

        val result = authRepository.login(
            email = "nonexistent@gmail.com",
            password = "password123"
        )

        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull()?.message?.contains("404") == true)

        Assert.assertNull(authDataStore.getUserId())
        Assert.assertFalse(authDataStore.isLoggedIn.first())
    }
}