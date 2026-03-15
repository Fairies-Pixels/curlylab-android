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
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.data.repository.profile.HairTypesRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes
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
class HairTypeManualSaveIntegrationTest {

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
                    File(context.filesDir, "datastore/test_hairtype.preferences_pb")
                }
            )

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                "test_hairtype_prefs",
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
    fun saveHairTypeManually_updatesHairTypeInProfile() = runTest {

        val userId = "user-123"

        authDataStore.saveAuthData(isLoggedIn = true,userId = userId)

        val request = HairTypeRequest(
            userId = userId,
            porosity = PorosityTypes.SEMI_POROUS.dbCode
        )

        val updatedHairResponse = """
        {
            "userId": "user-123",
            "porosity": "${PorosityTypes.NON_POROUS.dbCode}",
            "thickness": null,
            "isColored": false
        }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(updatedHairResponse)
        )

        val savedUserId = authDataStore.getUserId()

        hairTypesRepository.updateHairType(savedUserId!!, request)

        val updatedHairType = hairTypesRepository.getHairType(savedUserId)

        Assert.assertEquals(PorosityTypes.NON_POROUS.dbCode, updatedHairType.porosity)

        val updateRequest = mockWebServer.takeRequest()

        Assert.assertEquals("/hairtypes/$userId", updateRequest.path)
        Assert.assertEquals("PUT", updateRequest.method)

        val body = Gson().fromJson(
            updateRequest.body.readUtf8(),
            HairTypeRequest::class.java
        )

        Assert.assertEquals(userId, body.userId)
        Assert.assertEquals(PorosityTypes.SEMI_POROUS.dbCode, body.porosity)

        val getRequest = mockWebServer.takeRequest()

        Assert.assertEquals("/hairtypes/$userId", getRequest.path)
        Assert.assertEquals("GET", getRequest.method)
    }
}