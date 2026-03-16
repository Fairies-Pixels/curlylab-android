package fairies.pixels.curlyLabAndroid.integration.products

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
import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.FavoriteRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.FavoriteResponse
import fairies.pixels.curlyLabAndroid.data.repository.products.ProductsRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
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
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FavoritesIntegrationTest {

    companion object {
        private lateinit var sharedDataStore: DataStore<Preferences>
        private lateinit var sharedEncryptedPrefs: SharedPreferences
        private lateinit var sharedContext: Context
        private lateinit var authDataStore: AuthDataStore
        private val testUserId = UUID.randomUUID()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            sharedContext = ApplicationProvider.getApplicationContext()

            sharedContext.filesDir.listFiles()?.forEach {
                if (it.name.startsWith("datastore/test_favorites")) {
                    it.delete()
                }
            }

            sharedDataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    File(sharedContext.filesDir, "datastore/test_favorites.preferences_pb")
                }
            )

            val masterKey = MasterKey.Builder(sharedContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedEncryptedPrefs = EncryptedSharedPreferences.create(
                sharedContext,
                "test_favorites_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            authDataStore = AuthDataStore(sharedDataStore, sharedEncryptedPrefs)

            runBlocking {
                authDataStore.saveAuthData(
                    isLoggedIn = true,
                    userId = testUserId.toString(),
                    username = "testuser",
                    email = "test@gmail.com"
                )
            }
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            runBlocking {
                authDataStore.clearAuthData()
            }
        }
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var productsRepository: ProductsRepository
    private val testProductId = UUID.randomUUID()
    private val testProductId2 = UUID.randomUUID()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        productsRepository = ProductsRepositoryImpl(apiService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun addProductToFavorites_productAppearsInFavoritesList() = runTest {
        val favoriteResponse = FavoriteResponse(testUserId, testProductId)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(listOf(favoriteResponse)))
        )

        productsRepository.addToFavorites(testUserId, testProductId)

        val favorites = productsRepository.getUserFavorites(testUserId)

        Assert.assertNotNull(favorites)
        Assert.assertEquals(1, favorites?.size)
        Assert.assertEquals(testProductId, favorites?.get(0)?.productId)

        val addRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", addRequest.path)
        Assert.assertEquals("POST", addRequest.method)

        val requestBody = Gson().fromJson(addRequest.body.readUtf8(), FavoriteRequest::class.java)
        Assert.assertEquals(testProductId, requestBody.productId)

        val getRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", getRequest.path)
        Assert.assertEquals("GET", getRequest.method)
    }

    @Test
    fun removeProductFromFavorites_productRemovedFromFavoritesList() = runTest {
        val favoriteResponse = FavoriteResponse(testUserId, testProductId)

        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(emptyList<FavoriteResponse>()))
        )

        productsRepository.addToFavorites(testUserId, testProductId)
        productsRepository.removeFromFavorites(testUserId, testProductId)

        val favorites = productsRepository.getUserFavorites(testUserId)

        Assert.assertNotNull(favorites)
        Assert.assertTrue(favorites?.isEmpty() == true)

        val addRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", addRequest.path)

        val removeRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites/${testProductId}", removeRequest.path)
        Assert.assertEquals("DELETE", removeRequest.method)

        val getRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", getRequest.path)
    }

    @Test
    fun addSameProductTwice_secondAttemptFails() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(409)
                .setBody("""{"error": "Product already in favorites"}""")
        )

        productsRepository.addToFavorites(testUserId, testProductId)
        productsRepository.addToFavorites(testUserId, testProductId)

        val firstRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", firstRequest.path)

        val secondRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", secondRequest.path)
    }

    @Test
    fun getUserFavorites_withNoFavorites_returnsEmptyList() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(emptyList<FavoriteResponse>()))
        )

        val favorites = productsRepository.getUserFavorites(testUserId)

        Assert.assertNotNull(favorites)
        Assert.assertTrue(favorites?.isEmpty() == true)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", request.path)
        Assert.assertEquals("GET", request.method)
    }

    @Test
    fun isProductFavorite_returnsTrueForFavoriteProduct() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("true")
        )

        productsRepository.addToFavorites(testUserId, testProductId)

        val isFavorite = productsRepository.isFavorite(testUserId, testProductId)

        Assert.assertTrue(isFavorite)

        mockWebServer.takeRequest()
        val checkRequest = mockWebServer.takeRequest()
        Assert.assertEquals(
            "/products/${testProductId}/is_favourite/${testUserId}",
            checkRequest.path
        )
        Assert.assertEquals("GET", checkRequest.method)
    }

    @Test
    fun isProductFavorite_returnsFalseForNonFavoriteProduct() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("false")
        )

        val isFavorite = productsRepository.isFavorite(testUserId, testProductId2)

        Assert.assertFalse(isFavorite)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/products/${testProductId2}/is_favourite/${testUserId}", request.path)
    }

    @Test
    fun addToFavorites_withInvalidUserId_returnsError() = runTest {
        val invalidUserId = UUID.randomUUID()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"error": "User not found"}""")
        )

        productsRepository.addToFavorites(invalidUserId, testProductId)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${invalidUserId}/favourites", request.path)
        Assert.assertEquals("POST", request.method)
    }

    @Test
    fun addToFavorites_withInvalidProductId_returnsError() = runTest {
        val invalidProductId = UUID.randomUUID()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"error": "Product not found"}""")
        )

        productsRepository.addToFavorites(testUserId, invalidProductId)

        val request = mockWebServer.takeRequest()
        Assert.assertEquals("/users/${testUserId}/favourites", request.path)
        Assert.assertEquals("POST", request.method)
    }
}