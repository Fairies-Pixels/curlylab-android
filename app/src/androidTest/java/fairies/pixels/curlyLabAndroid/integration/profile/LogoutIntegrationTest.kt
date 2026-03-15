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
import fairies.pixels.curlyLabAndroid.data.remote.model.request.auth.LogoutRequest
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
class LogoutIntegrationTest {

    companion object {

        private lateinit var dataStore: DataStore<Preferences>
        private lateinit var encryptedPrefs: SharedPreferences
        private lateinit var context: Context

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            context = ApplicationProvider.getApplicationContext()

            context.filesDir.listFiles()?.forEach {
                if (it.name.startsWith("datastore/test_logout")) {
                    it.delete()
                }
            }

            dataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    File(context.filesDir, "datastore/test_logout.preferences_pb")
                }
            )

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                "test_logout_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
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

        authDataStore = AuthDataStore(dataStore, encryptedPrefs)
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
    fun logoutSuccessful_userIsLoggedOutAndDataCleared() = runTest {

        val refreshToken = "refresh.token.456"

        authDataStore.saveAuthData(
            accessToken = "access.token.123",
            refreshToken = refreshToken,
            userId = "user-123",
            email = "test@gmail.com",
            username = "testuser",
            isLoggedIn = false
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        authRepository.logout()

        val userId = authDataStore.getUserId()
        val username = authDataStore.getUsername()
        val isLoggedIn = authDataStore.isLoggedIn.first()

        Assert.assertNull(userId)
        Assert.assertNull(username)
        Assert.assertFalse(isLoggedIn)

        val request = mockWebServer.takeRequest()

        Assert.assertEquals("/auth/logout", request.path)
        Assert.assertEquals("POST", request.method)

        val requestBody = Gson().fromJson(
            request.body.readUtf8(),
            LogoutRequest::class.java
        )

        Assert.assertEquals(refreshToken, requestBody.refreshToken)
    }
}