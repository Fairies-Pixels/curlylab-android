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
import org.junit.*
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

        authDataStore = AuthDataStore(
            sharedDataStore,
            sharedEncryptedPrefs
        )
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
    fun googleAuthorizationNewUser_success_userSavedInDataStore() = runTest {
        val idToken = "google-id-token"

        val authResponse = AuthResponse(
            access = "access.token.123",
            refresh = "refresh.token.456",
            userId = "user-999",
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

        // Проверяем DataStore
        Assert.assertEquals("user-999", authDataStore.getUserId())
        Assert.assertEquals("googleuser", authDataStore.getUsername())
        Assert.assertEquals("googleuser@gmail.com", authDataStore.getEmail())
        Assert.assertTrue(authDataStore.isLoggedIn.first())

        // Проверка запроса
        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/auth/google", request.path)
        Assert.assertEquals("POST", request.method)

        val requestBody = Gson().fromJson(request.body.readUtf8(), GoogleRequest::class.java)
        Assert.assertEquals(idToken, requestBody.idToken)
    }
}