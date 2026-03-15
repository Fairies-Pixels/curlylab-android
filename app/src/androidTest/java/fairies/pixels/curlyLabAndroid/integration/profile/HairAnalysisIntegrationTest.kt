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
import fairies.pixels.curlyLabAndroid.data.repository.profile.HairTypesRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel.HairAnalysisViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import io.mockk.mockk
import io.mockk.every
import io.mockk.coEvery
import io.mockk.verify

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
                produceFile = { File(context.filesDir, "datastore/test_hair_analysis.preferences_pb") }
            )

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                "test_hair_analysis_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    private lateinit var authDataStore: AuthDataStore
    private lateinit var hairTypesRepository: HairTypesRepositoryImpl

    @Before
    fun setup() {
        // Создаём мок ApiService, relaxed = true позволяет не реализовывать все функции
        val mockApiService = mockk<ApiService>(relaxed = true)

        hairTypesRepository = HairTypesRepositoryImpl(mockApiService)
        authDataStore = AuthDataStore(dataStore, encryptedPrefs)
    }

    @After
    fun teardown() {
        runTest { authDataStore.clearAuthData() }
    }

    @Test
    fun hairAnalysisViewModel_analyzeAndSave_updatesPorosity() = runTest {
        val userId = "user-123"

        // Сохраняем авторизованного пользователя
        authDataStore.saveAuthData(isLoggedIn = true, userId = userId)

        val mockPorosityResult = "ВЫСОКАЯ ПОРИСТОСТЬ"

        // Мокаем AnalysisRepository
        val analysisRepository = object : AnalysisRepository {
            override suspend fun analyzePhoto(imageBytes: ByteArray): String {
                return mockPorosityResult
            }
        }

        val viewModel = HairAnalysisViewModel(authDataStore, hairTypesRepository, analysisRepository)

        // Запускаем анализ
        viewModel.analyze("dummy".toByteArray())
        advanceUntilIdle()
        Assert.assertEquals(mockPorosityResult, viewModel.result.value)

        // Сохраняем результат
        viewModel.saveResult()
        advanceUntilIdle()
        Assert.assertEquals(true, viewModel.saved.value)
    }
}