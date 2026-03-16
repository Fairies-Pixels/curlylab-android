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
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.GoogleRequest
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
class GoogleAuthIntegrationTest {

    companion object {
        private lateinit var sharedDataStore: DataStore<Preferences>
        private lateinit var sharedEncryptedPrefs: SharedPreferences
        private lateinit var sharedContext: Context

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            sharedContext = ApplicationProvider.getApplicationContext()

            sharedContext.filesDir.listFiles()?.forEach {
                if (it.name.startsWith("datastore/test_google_auth")) {
                    it.delete()
                }
            }

            sharedDataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    File(sharedContext.filesDir, "datastore/test_google_auth.preferences_pb")
                }
            )

            val masterKey = MasterKey.Builder(sharedContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedEncryptedPrefs = EncryptedSharedPreferences.create(
                sharedContext,
                "test_google_auth_prefs",
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
    fun googleAuth_newUser_successful() = runTest {
        val idToken = "google-id-token-12345"
        val authResponse = AuthResponse(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEyMyJ9.signature",
            refresh = "refresh.token.456",
            userId = "user-123",
            email = "googleuser@gmail.com",
            username = "googleuser"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(authResponse))
        )

        val result = authRepository.loginWithGoogle(idToken)

        Assert.assertTrue(result.isSuccess)

        val authResult = result.getOrNull()
        Assert.assertNotNull(authResult)
        Assert.assertEquals("googleuser@gmail.com", authResult?.email)
        Assert.assertEquals("googleuser", authResult?.username)

        val userId = authDataStore.getUserId()
        val isLoggedIn = authDataStore.isLoggedIn.first()

        Assert.assertEquals("user-123", userId)
        Assert.assertTrue(isLoggedIn)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/auth/google", request.path)
        Assert.assertEquals("POST", request.method)

        val requestBody = Gson().fromJson(request.body.readUtf8(), GoogleRequest::class.java)
        Assert.assertEquals(idToken, requestBody.idToken)
    }

    @Test
    fun googleAuth_existingUser_successful() = runTest {
        val idToken = "google-id-token-existing"
        val authResponse = AuthResponse(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTQ1NiJ9.signature",
            refresh = "refresh.token.789",
            userId = "user-456",
            email = "existing@gmail.com",
            username = "existinguser"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(authResponse))
        )

        val result = authRepository.loginWithGoogle(idToken)

        Assert.assertTrue(result.isSuccess)

        val userId = authDataStore.getUserId()
        val isLoggedIn = authDataStore.isLoggedIn.first()

        Assert.assertEquals("user-456", userId)
        Assert.assertTrue(isLoggedIn)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/auth/google", request.path)

        val requestBody = Gson().fromJson(request.body.readUtf8(), GoogleRequest::class.java)
        Assert.assertEquals(idToken, requestBody.idToken)
    }

    @Test
    fun googleAuth_withInvalidToken_returnsError() = runTest {
        val idToken = "invalid-google-token"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "Invalid token"}""")
        )

        val result = authRepository.loginWithGoogle(idToken)

        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull()?.message?.contains("401") == true)

        val isLoggedIn = authDataStore.isLoggedIn.first()
        Assert.assertFalse(isLoggedIn)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/auth/google", request.path)
    }
}