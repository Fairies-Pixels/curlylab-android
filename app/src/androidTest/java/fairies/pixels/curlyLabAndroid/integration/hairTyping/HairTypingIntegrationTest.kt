package fairies.pixels.curlyLabAndroid.integration.hairTyping

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
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.data.repository.profile.HairTypesRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.ThicknessTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
class HairTypingIntegrationTest {

    companion object {

        private lateinit var dataStore: DataStore<Preferences>
        private lateinit var encryptedPrefs: SharedPreferences
        private lateinit var context: Context

        @BeforeClass
        @JvmStatic
        fun setupClass() {

            context = ApplicationProvider.getApplicationContext()

            dataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    File(context.filesDir, "datastore/test_typing.preferences_pb")
                }
            )

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                "test_typing_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var hairTypesRepository: HairTypesRepository
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
        hairTypesRepository = HairTypesRepositoryImpl(apiService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()

        runBlocking {
            authDataStore.clearAuthData()
        }
    }
    
    @Test
    fun coloredHairTestResult_savedSuccessfully() = runTest {

        val userId = "user-123"
        authDataStore.saveAuthData (true, userId = userId)

        val request = HairTypeRequest(
            userId = userId,
            isColored = true
        )

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        hairTypesRepository.updateHairType(userId, request)

        val recordedRequest = mockWebServer.takeRequest()

        Assert.assertEquals("/hairtypes/$userId", recordedRequest.path)
        Assert.assertEquals("PUT", recordedRequest.method)

        val body = Gson().fromJson(
            recordedRequest.body.readUtf8(),
            HairTypeRequest::class.java
        )

        Assert.assertTrue(body.isColored!!)
    }

    @Test
    fun porosityTypingTestResult_savedSuccessfully() = runTest {

        val userId = "user-123"
        authDataStore.saveAuthData (true, userId = userId)

        val request = HairTypeRequest(
            userId = userId,
            porosity = PorosityTypes.POROUS.dbCode
        )

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        hairTypesRepository.updateHairType(userId, request)

        val recordedRequest = mockWebServer.takeRequest()

        Assert.assertEquals("/hairtypes/$userId", recordedRequest.path)

        val body = Gson().fromJson(
            recordedRequest.body.readUtf8(),
            HairTypeRequest::class.java
        )

        Assert.assertEquals(PorosityTypes.POROUS.dbCode, body.porosity)
    }

    @Test
    fun thicknessTypingTestResult_savedSuccessfully() = runTest {

        val userId = "user-123"
        authDataStore.saveAuthData (true, userId = userId)

        val request = HairTypeRequest(
            userId = userId,
            thickness = ThicknessTypes.MEDIUM.dbCode
        )

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        hairTypesRepository.updateHairType(userId, request)

        val recordedRequest = mockWebServer.takeRequest()

        Assert.assertEquals("/hairtypes/$userId", recordedRequest.path)

        val body = Gson().fromJson(
            recordedRequest.body.readUtf8(),
            HairTypeRequest::class.java
        )

        Assert.assertEquals(ThicknessTypes.MEDIUM.dbCode, body.thickness)
    }

    @Test
    fun hairPhotoAnalysisResult_savedSuccessfully() = runTest {

        val userId = "user-123"
        authDataStore.saveAuthData (true, userId = userId)

        val request = HairTypeRequest(
            userId = userId,
            porosity = PorosityTypes.SEMI_POROUS.dbCode
        )

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        hairTypesRepository.updateHairType(userId, request)

        val recordedRequest = mockWebServer.takeRequest()

        Assert.assertEquals("/hairtypes/$userId", recordedRequest.path)

        val body = Gson().fromJson(
            recordedRequest.body.readUtf8(),
            HairTypeRequest::class.java
        )

        Assert.assertEquals(PorosityTypes.SEMI_POROUS.dbCode, body.porosity)
    }
}