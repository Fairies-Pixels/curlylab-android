package fairies.pixels.curlyLabAndroid.data.oauth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named

class GoogleAuthUiClient @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("googleClientId") private val clientId: String
) {
    private val googleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun getTokenFromIntent(intent: Intent?): String? {
        if (intent == null) {
            return null
        }

        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.getResult(ApiException::class.java)
            account.idToken
        } catch (e: ApiException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}