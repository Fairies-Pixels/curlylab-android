package fairies.pixels.curlyLabAndroid.data.oauth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleAuthUiClientTest {

    private lateinit var context: Context
    private lateinit var googleAuthUiClient: GoogleAuthUiClient
    private val clientId = "test-client-id"
    private lateinit var mockGoogleSignInClient: GoogleSignInClient

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        mockGoogleSignInClient = mockk()

        mockkStatic(GoogleSignIn::class)
        every {
            GoogleSignIn.getClient(
                any<Context>(),
                any<GoogleSignInOptions>()
            )
        } returns mockGoogleSignInClient

        googleAuthUiClient = GoogleAuthUiClient(context, clientId)
    }

    @Test
    fun `getSignInIntent возвращает Intent`() {
        val expectedIntent = mockk<Intent>()
        every { mockGoogleSignInClient.signInIntent } returns expectedIntent

        val result = googleAuthUiClient.getSignInIntent()

        assertEquals(expectedIntent, result)
        verify { mockGoogleSignInClient.signInIntent }
    }

    @Test
    fun `getTokenFromIntent возвращает idToken при успешном входе`() {
        val intent = mockk<Intent>()
        val mockTask = mockk<Task<GoogleSignInAccount>>()
        val mockAccount = mockk<GoogleSignInAccount>()
        val expectedToken = "test-id-token"

        every { GoogleSignIn.getSignedInAccountFromIntent(intent) } returns mockTask
        every { mockTask.getResult(ApiException::class.java) } returns mockAccount
        every { mockAccount.idToken } returns expectedToken

        val result = googleAuthUiClient.getTokenFromIntent(intent)

        assertEquals(expectedToken, result)
        verify { GoogleSignIn.getSignedInAccountFromIntent(intent) }
    }

    @Test
    fun `getTokenFromIntent возвращает null при ApiException`() {
        val intent = mockk<Intent>()
        val mockTask = mockk<Task<GoogleSignInAccount>>()

        every { GoogleSignIn.getSignedInAccountFromIntent(intent) } returns mockTask
        every { mockTask.getResult(ApiException::class.java) } throws ApiException(Status.RESULT_CANCELED)

        val result = googleAuthUiClient.getTokenFromIntent(intent)

        assertNull(result)
        verify { GoogleSignIn.getSignedInAccountFromIntent(intent) }
    }

    @Test
    fun `getTokenFromIntent возвращает null при любом исключении`() {
        val intent = mockk<Intent>()

        every { GoogleSignIn.getSignedInAccountFromIntent(intent) } throws RuntimeException()

        val result = googleAuthUiClient.getTokenFromIntent(intent)

        assertNull(result)
        verify { GoogleSignIn.getSignedInAccountFromIntent(intent) }
    }

    @Test
    fun `getTokenFromIntent возвращает null при null intent`() {
        val result = googleAuthUiClient.getTokenFromIntent(null)

        assertNull(result)
    }

    @Test
    fun `signOut вызывает signOut у клиента`() {
        every { mockGoogleSignInClient.signOut() } returns mockTask<Void>()
        googleAuthUiClient.signOut()
        verify { mockGoogleSignInClient.signOut() }
    }

    private fun <T> mockTask(): Task<T> = mockk()
}