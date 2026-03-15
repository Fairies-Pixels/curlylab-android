package fairies.pixels.curlyLabAndroid.integration.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import fairies.pixels.curlyLabAndroid.data.repository.profile.HairTypesRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel.HairAnalysisViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
class HairAnalysisIntegrationTest {

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
                produceFile = { File(context.filesDir, "datastore/test_profile.preferences_pb") }
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
    private lateinit var hairTypesRepository: HairTypesRepositoryImpl
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
        runBlocking { authDataStore.clearAuthData() }
    }

    @Test
    fun hairAnalysisViewModel_analyzeAndSave_updatesPorosity() = runTest {
        val userId = "user-123"

        // Сохраняем авторизованного пользователя
        authDataStore.saveAuthData(isLoggedIn = true, userId = userId)

        // Мокаем ML-сервер через MockWebServer
        val mockPorosityResult = "ВЫСОКАЯ ПОРИСТОСТЬ"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockPorosityResult)
        )

        // Мокаем AnalysisRepository
        val analysisRepository = object : AnalysisRepository {
            override suspend fun analyzePhoto(imageBytes: ByteArray): String {
                val request = mockWebServer.takeRequest()
                Assert.assertEquals("/analyze", request.path)
                return mockPorosityResult
            }
        }

        val viewModel =
            HairAnalysisViewModel(authDataStore, hairTypesRepository, analysisRepository)

        // Запускаем анализ
        viewModel.analyze("dummy".toByteArray())
        advanceUntilIdle()
        Assert.assertEquals(mockPorosityResult, viewModel.result.value)

        // Сохраняем результат
        viewModel.saveResult()
        Assert.assertEquals(true, viewModel.saved.value)

        // Проверяем, что запрос на обновление HairType ушёл
        val updateRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/hairtypes/$userId", updateRequest.path)
        Assert.assertEquals("PUT", updateRequest.method)
    }
}