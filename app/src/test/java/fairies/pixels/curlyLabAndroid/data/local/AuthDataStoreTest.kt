package fairies.pixels.curlyLabAndroid.data.local

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthDataStoreTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var encryptedPrefs: SharedPreferences
    private lateinit var authDataStore: AuthDataStore
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var preferences: Preferences

    @Before
    fun setUp() {
        dataStore = mockk()
        encryptedPrefs = mockk()
        editor = mockk(relaxed = true)
        preferences = mockk()

        every { encryptedPrefs.edit() } returns editor
        every { dataStore.data } returns flowOf(preferences)

        authDataStore = AuthDataStore(dataStore, encryptedPrefs)
    }

    @Test
    fun `saveAuthData сохраняет данные в DataStore и SharedPreferences`() = runTest {
        val isLoggedIn = true
        val accessToken = "access123"
        val refreshToken = "refresh123"
        val userId = "user123"
        val username = "testuser"
        val email = "test@example.com"

        coEvery { dataStore.updateData(any<suspend (Preferences) -> Preferences>()) } returns preferences

        every { editor.putString(any(), any()) } returns editor

        authDataStore.saveAuthData(
            isLoggedIn = isLoggedIn,
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            username = username,
            email = email
        )

        coVerify { dataStore.updateData(any()) }
        verify { encryptedPrefs.edit() }
        verify { editor.putString("access_token", accessToken) }
        verify { editor.putString("refresh_token", refreshToken) }
        verify { editor.apply() }
    }

    @Test
    fun `saveAuthData не сохраняет токены, если они null`() = runTest {
        val isLoggedIn = true

        coEvery { dataStore.updateData(any<suspend (Preferences) -> Preferences>()) } returns preferences

        authDataStore.saveAuthData(isLoggedIn = isLoggedIn)

        coVerify { dataStore.updateData(any()) }
        verify { encryptedPrefs.edit() }
        verify(exactly = 0) { editor.putString(any(), any()) }
    }

    @Test
    fun `isLoggedIn возвращает false по умолчанию`() = runTest {
        every { preferences[booleanPreferencesKey("is_logged_in")] } returns null

        val result = authDataStore.isLoggedIn.first()

        assertFalse(result)
    }

    @Test
    fun `isLoggedIn возвращает true, если пользователь авторизован`() = runTest {
        every { preferences[booleanPreferencesKey("is_logged_in")] } returns true

        val result = authDataStore.isLoggedIn.first()

        assertTrue(result)
    }

    @Test
    fun `getUserId возвращает userId из DataStore`() = runTest {
        val userId = "user123"
        every { preferences[stringPreferencesKey("user_id")] } returns userId

        val result = authDataStore.getUserId()

        assertEquals(userId, result)
    }

    @Test
    fun `getUserId возвращает null, если userId отсутствует`() = runTest {
        every { preferences[stringPreferencesKey("user_id")] } returns null

        val result = authDataStore.getUserId()

        assertNull(result)
    }

    @Test
    fun `getEmail возвращает email из DataStore`() = runTest {
        val email = "test@example.com"
        every { preferences[stringPreferencesKey("email")] } returns email

        val result = authDataStore.getEmail()

        assertEquals(email, result)
    }

    @Test
    fun `getEmail возвращает null, если email отсутствует`() = runTest {
        every { preferences[stringPreferencesKey("email")] } returns null

        val result = authDataStore.getEmail()

        assertNull(result)
    }

    @Test
    fun `getUsername возвращает username из DataStore`() = runTest {
        val username = "testuser"
        every { preferences[stringPreferencesKey("username")] } returns username

        val result = authDataStore.getUsername()

        assertEquals(username, result)
    }

    @Test
    fun `getUsername возвращает null, если username отсутствует`() = runTest {
        every { preferences[stringPreferencesKey("username")] } returns null

        val result = authDataStore.getUsername()

        assertNull(result)
    }

    @Test
    fun `getAccessTokenBlocking возвращает токен из SharedPreferences`() {
        val token = "access123"
        every { encryptedPrefs.getString("access_token", null) } returns token

        val result = authDataStore.getAccessTokenBlocking()

        assertEquals(token, result)
    }

    @Test
    fun `getAccessTokenBlocking возвращает, null если токен отсутствует`() {
        every { encryptedPrefs.getString("access_token", null) } returns null

        val result = authDataStore.getAccessTokenBlocking()

        assertNull(result)
    }

    @Test
    fun `getRefreshTokenBlocking возвращает токен из SharedPreferences`() {
        val token = "refresh123"
        every { encryptedPrefs.getString("refresh_token", null) } returns token

        val result = authDataStore.getRefreshTokenBlocking()

        assertEquals(token, result)
    }

    @Test
    fun `getRefreshTokenBlocking возвращает null, если токен отсутствует`() {
        every { encryptedPrefs.getString("refresh_token", null) } returns null

        val result = authDataStore.getRefreshTokenBlocking()

        assertNull(result)
    }

    @Test
    fun `getAccessToken вызывает getAccessTokenBlocking`() = runTest {
        val token = "access123"
        every { encryptedPrefs.getString("access_token", null) } returns token

        val result = authDataStore.getAccessToken()

        assertEquals(token, result)
    }

    @Test
    fun `getRefreshToken вызывает getRefreshTokenBlocking`() = runTest {
        val token = "refresh123"
        every { encryptedPrefs.getString("refresh_token", null) } returns token

        val result = authDataStore.getRefreshToken()

        assertEquals(token, result)
    }

    @Test
    fun `clearAuthData очищает все данные`() = runTest {
        coEvery { dataStore.updateData(any<suspend (Preferences) -> Preferences>()) } returns preferences

        every { editor.remove(any()) } returns editor
        every { editor.apply() } returns Unit

        authDataStore.clearAuthData()

        coVerify { dataStore.updateData(any()) }
        verify { encryptedPrefs.edit() }
        verify { editor.remove("access_token") }
        verify { editor.remove("refresh_token") }
        verify { editor.apply() }
    }
}