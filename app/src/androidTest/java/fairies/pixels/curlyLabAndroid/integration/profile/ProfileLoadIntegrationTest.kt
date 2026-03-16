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
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.repository.profile.HairTypesRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.repository.profile.UsersRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.ThicknessTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@RunWith(AndroidJUnit4::class)
class ProfileLoadIntegrationTest {

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
                    File(
                        context.filesDir,
                        "datastore/test_profile_${System.currentTimeMillis()}.preferences_pb"
                    )
                }
            )

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                "test_profile_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var usersRepository: UsersRepository
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

        usersRepository = UsersRepositoryImpl(apiService)
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
    fun profileLoadAfterAuthorization_returnsUserDataAndHairType() = runTest {

        val userId = "user-123"

        authDataStore.saveAuthData(
            isLoggedIn = true,
            userId = userId
        )

        val userResponse = """
            {
                "id": "$userId",
                "username": "testuser",
                "imageUrl": "https://s3.yandex.com/avatars/user-123.jpg"
            }
        """.trimIndent()

        val hairResponse = """
            {
                "porosity": "${PorosityTypes.SEMI_POROUS.dbCode}",
                "isColored": "false",
                "thickness": "${ThicknessTypes.THIN.dbCode}"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(userResponse)
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(hairResponse)
        )

        val savedUserId = authDataStore.getUserId()

        val user = usersRepository.getUser(savedUserId!!)
        val hairType = hairTypesRepository.getHairType(savedUserId)

        Assert.assertEquals("testuser", user.username)
        Assert.assertEquals(
            "https://s3.yandex.com/avatars/user-123.jpg",
            user.imageUrl
        )

        Assert.assertEquals(PorosityTypes.SEMI_POROUS.dbCode, hairType.porosity)
        Assert.assertEquals(ThicknessTypes.THIN.dbCode, hairType.thickness)
        Assert.assertEquals(false, hairType.isColored)

        val userRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/$userId", userRequest.path)

        val hairRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/hairtypes/$userId", hairRequest.path)
    }
}